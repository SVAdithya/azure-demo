## Full Validation Guide (Private Azure + CI/CD + VPN)

This guide walks you through end-to-end deployment and validation for this repo:

- Private-only Azure networking (VNet, Private Endpoints, Private DNS)
- Azure Container Registry (ACR), Azure Container Apps (internal-only)
- MySQL Flexible Server, Cosmos DB, Storage Account (all private)
- GitHub Actions CI/CD building/pushing Docker image and deploying
- VPN-based access and verification

### 1) Prerequisites

- Azure subscription with permission to create RG-level resources
- CLI tools installed:
    - Azure CLI: `az version`
    - Terraform: `terraform version`
    - Docker: `docker version`
    - Git: `git --version`
- GitHub repository for this code base

Optional (manual/local validation):

- cURL, dig/nslookup (macOS has `dig` in `bind` tools)
- Azure VPN Client (only if you enable VPN)

### 2) Clone repo and review project

```bash
git clone <your-repo-url>
cd AzureDemo
```

Key locations:

- App: `azure-app`
- Root Dockerfile: `Dockerfile`
- Terraform: `deploy/tf`
- CI/CD: `.github/workflows/cicd.yml`

### 3) Configure Terraform variables

Edit `deploy/terraform.tfvars` as needed:

- `resource_group_name`, `location`, `name_prefix`, `environment`
- `storage_account_name`, `cosmosdb_account_name`, `acr_name`, `container_app_name`
- `mysql_database_name`
- `private_domain_name` (optional; example: `internal.local`)
- Do NOT commit secrets; supply `mysql_admin_password` via CLI/secret

Example (already provided):

```hcl
name_prefix         = "demo-dev"
environment         = "dev"
acr_name            = "demodevacr1234"
container_app_name  = "demo-dev-azure-app"
mysql_database_name = "appdb"
```

### 4) Configure GitHub Actions

Create the following in your repo settings:

- Secrets:
    - `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`
    - `MYSQL_ADMIN_PASSWORD`
    - If VPN enabled: `P2S_ROOT_CERT_PUBLIC_DATA` (base64 of the root cert public key without PEM headers)
- Variables:
    - `NAME_PREFIX`, `ENVIRONMENT`, `RESOURCE_GROUP_NAME`, `AZURE_LOCATION`
    - `STORAGE_ACCOUNT_NAME`, `COSMOS_ACCOUNT_NAME`, `ACR_NAME`, `CONTAINER_APP_NAME`, `MYSQL_DATABASE_NAME`
    - Optional: `PRIVATE_DOMAIN_NAME`, `ENABLE_VPN` (true/false)

Note: The workflow uses OIDC (`azure/login`). Ensure the service principal tied to `AZURE_CLIENT_ID` has Contributor on
the target Resource Group or subscription.

### 5) One-time infra deployment (optional local run)

You can let the CI run this, or apply locally first to see outputs.

```bash
az login
cd deploy/tf
terraform init
terraform apply \
  -var-file="../terraform.tfvars" \
  -var "subscription_id=<your_subscription_id>" \
  -var "mysql_admin_password=<secure_password>"
```

Expected outputs (examples):

- `acr_login_server` → `<acr>.azurecr.io`
- `container_app_internal_fqdn` → internal ACA FQDN
- `internal_app_url` → if you set a private domain, e.g., `app.internal.local`
- `mysql_fqdn` → MySQL private FQDN
- `cosmosdb_endpoint` → Cosmos private endpoint URL
- `vpn_gateway_public_ip` → only if `enable_vpn=true`

Retrieve outputs later if needed:

```bash
terraform output -raw acr_login_server
terraform output container_app_internal_fqdn
terraform output -raw mysql_fqdn
terraform output -raw cosmosdb_endpoint
```

### 6) CI/CD: build, push, deploy

Ensure `.github/workflows/cicd.yml` exists on branch `main`. Push a commit to trigger:

- Job infra: provisions/updates Azure infra (idempotent)
- Job build_and_push: builds Docker image from root `Dockerfile` and pushes to ACR
- Job deploy_app: re-applies Terraform with the `image_tag` to roll ACA to the new revision

Verify in GitHub Actions UI that all three jobs succeed.

### 7) Verify ACR image

```bash
az login
az acr repository list --name "$ACR_NAME" --output table
az acr repository show-tags --name "$ACR_NAME" --repository azure-app --output table
```

Look for the tag equal to the Git commit SHA used in the workflow.

### 8) Verify Container App and revision

```bash
# Set these vars
RG="<your RG>"
APP="<container app name>"
az containerapp show -g "$RG" -n "$APP" --output jsonc
az containerapp revision list -g "$RG" -n "$APP" --output table
```

Confirm:

- Ingress is internal-only (external_enabled = false)
- Image points to `<acr>.azurecr.io/azure-app:<gitsha>`

### 9) Connect to VPN (if enabled)

If `enable_vpn=true`, you need a Point-to-Site connection.

Option A: Azure Portal

- Go to your `Virtual network gateway` → `Point-to-site configuration` → `Download VPN client`
- Import the profile into Azure VPN Client (macOS/Windows) and connect

Option B: Azure CLI (generates package you can download)

```bash
GW_NAME="${NAME_PREFIX}-vpngw"
RG="<your RG>"
az network vnet-gateway vpn-client generate -g "$RG" -n "$GW_NAME" --processor-architecture Amd64 --output json
# This returns an URL to download the VPN client package; download, import, and connect
```

Confirm connection on macOS:

```bash
ifconfig | grep -A3 utun     # a utun interface will appear when connected
```

### 10) Verify Private DNS resolution over VPN

Use Terraform outputs or known FQDNs:

```bash
# App (internal ACA FQDN or custom private domain)
dig +short $(terraform -chdir=deploy/tf output -raw internal_app_url 2>/dev/null || terraform -chdir=deploy/tf output -raw container_app_internal_fqdn)

# Cosmos (privatelink)
dig +short $(terraform -chdir=deploy/tf output -raw cosmosdb_endpoint | sed 's#https://##; s#/$##')

# Storage (privatelink)
ST_ACC=$(terraform -chdir=deploy/tf output -raw acr_login_server 2>/dev/null >/dev/null; echo "$STORAGE_ACCOUNT_NAME")
dig +short ${ST_ACC}.blob.core.windows.net

# MySQL (private FQDN)
dig +short $(terraform -chdir=deploy/tf output -raw mysql_fqdn)
```

IP addresses should be private (RFC1918) when connected via VPN.

### 11) Verify app is reachable internally

```bash
APP_URL=$(terraform -chdir=deploy/tf output -raw internal_app_url 2>/dev/null || terraform -chdir=deploy/tf output -raw container_app_internal_fqdn)
# Health (Actuator is included via parent POM)
curl -s https://${APP_URL}/actuator/health | jq .
```

Expect `{ "status": "UP" }`. If TLS is required in your environment, ACA will terminate TLS for internal endpoint;
otherwise use http.

### 12) Verify application configuration inside the running container

You can exec into the Container App to inspect environment variables:

```bash
RG="<your RG>"
APP="<container app name>"
az containerapp exec -g "$RG" -n "$APP" --command "/bin/sh -lc 'printenv | egrep "^(SPRING_|COSMOS_|AZURE_STORAGE_|DB_|SERVER_)"'"
```

Confirm the following are present and non-empty (secrets won’t print themselves, but env values mapped from secrets
will):

- `SPRING_PROFILES_ACTIVE`
- `COSMOS_ENDPOINT`, `COSMOS_KEY`
- `AZURE_STORAGE_CONNECTION_STRING`, `AZURE_STORAGE_CONTAINER_NAME`
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

Also verify the Spring Boot config file contains placeholders wired to env:

```text
azure-app/src/main/resources/application.properties
- azure.cosmos.endpoint=${COSMOS_ENDPOINT:}
- azure.cosmos.key=${COSMOS_KEY:}
- azure.storage.connection-string=${AZURE_STORAGE_CONNECTION_STRING:}
- spring.datasource.url=${SPRING_DATASOURCE_URL:}
- spring.datasource.username=${SPRING_DATASOURCE_USERNAME:}
- spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
```

### 13) Verify logs for connectivity

```bash
RG="<your RG>"
APP="<container app name>"
# Tail logs
az containerapp logs show -g "$RG" -n "$APP" --follow --format text
```

Look for successful DB connection initialization (HikariCP), and any app startup messages hitting Cosmos/Storage. If you
have specific endpoints for reading/writing to Cosmos/Blob, call them and watch logs.

### 14) Verify database connectivity directly (optional)

From your machine (connected to VPN), if you have the MySQL client:

```bash
MYSQL_FQDN=$(terraform -chdir=deploy/tf output -raw mysql_fqdn)
mysql -h "$MYSQL_FQDN" -u <dbadmin> -p -P 3306 --protocol=TCP -e "SHOW DATABASES;"
```

If SSL enforcement causes issues, ensure your client trusts Azure MySQL certs or connect via the application only.

### 15) Verify Cosmos and Storage private resolution

```bash
# Cosmos endpoint to private IP
dig +short $(terraform -chdir=deploy/tf output -raw cosmosdb_endpoint | sed 's#https://##; s#/$##')

# Storage blob private IP
dig +short ${STORAGE_ACCOUNT_NAME}.blob.core.windows.net
```

These should resolve to `privatelink` IPs.

### 16) Redeploy with a new image

Push a new commit to `main`. The workflow will:

- Build a new image tagged with the commit SHA
- Push to ACR
- Re-apply Terraform with `image_tag` to roll ACA revision

Verify the new revision:

```bash
RG="<your RG>"; APP="<container app name>"
az containerapp revision list -g "$RG" -n "$APP" --output table
```

### 17) Troubleshooting

- ACR pull failures: Ensure ACR admin is enabled (current TF does this) or bind managed identity with AcrPull role.
- DNS not resolving: Check Private DNS zones are linked to the VNet (`dns.tf`) and your client is connected to VPN.
- MySQL SSL issues: The app uses JDBC URL with SSL; verify `SPRING_DATASOURCE_URL` is set correctly.
- App unreachable: Ingress is internal-only by design. Ensure VPN is connected.
- Secrets not visible: Container App masks secret values. Verify env injection via `az containerapp show` and logs.

### 18) Cleanup

```bash
cd deploy/tf
terraform destroy -var-file="../terraform.tfvars" -var "subscription_id=<your_subscription_id>"
```

### 19) Notes for multi-environment

- Create separate tfvars and GitHub Environments (dev/qa/prod) with distinct variables and prefixes
- Parameterize the workflow by environment or create one workflow per environment
- The same immutable image runs in all envs; configuration is provided via environment variables/secrets

### Size Comparison in Local Docker

To test the size difference in local Docker, you can compare the build context and image sizes with and without `.dockerignore`:

```bash
# With .dockerignore
DOCKER_BUILDKIT=0 time docker build --no-cache -t demo:with-ign --build-arg MODULE=azure-app -f Dockerfile . |& grep 'Sending build context\|Successfully built'

# Temporarily disable .dockerignore
mv .dockerignore .dockerignore.off

# Without .dockerignore
DOCKER_BUILDKIT=0 time docker build --no-cache -t demo:without-ign --build-arg MODULE=azure-app -f Dockerfile . |& grep 'Sending build context\|Successfully built'

# Restore
mv .dockerignore.off .dockerignore
```

Then, compare the image sizes:

```bash
docker images demo:with-ign demo:without-ign
```

To see the layer breakdown and where bytes went:

```bash
docker history demo:with-ign
docker history demo:without-ign
```

Note that you’ll see “Sending build context to Docker daemon …” shrink with `.dockerignore`. The final runtime image size may be the same (multi-stage copies only the jar), but build time and the build-stage layers will be smaller/faster when large files/dirs are ignored.
