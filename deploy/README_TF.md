# Terraform: Private Azure Stack for Spring Boot (ACA + ACR + MySQL + Cosmos + Storage)

This Terraform deploys a private-by-default Azure foundation and an internal-only Container App for your Spring Boot
service:

- Resource Group
- Virtual Network with subnets for Container Apps, Private Endpoints, and MySQL
- Azure Container Registry (ACR)
- Azure Container Apps Environment with internal-only ingress
- Azure Container App (image from ACR) with secrets/env wired from Terraform
- Azure Database for MySQL Flexible Server (private)
- Azure Storage Account + Container (private endpoint)
- Azure Cosmos DB SQL (private endpoint)
- Private DNS Zones for MySQL, Cosmos, and Blob
- Optional Point-to-Site VPN Gateway to access the app privately

## Prerequisites

- Terraform CLI and Azure CLI installed
- An Azure Subscription
- If using GitHub Actions: a Federated Credential (OIDC) Service Principal with sufficient permissions (contributor on
  RG)

## Variables

Set these in `deploy/terraform.tfvars` or pass via `-var`:

- `resource_group_name`, `location`, `name_prefix`, `environment`
- `storage_account_name`, `cosmosdb_account_name`, `acr_name`, `container_app_name`
- `mysql_database_name`, `mysql_admin_password` (sensitive)
- `private_domain_name` (optional custom private zone like `internal.local`)
- Networking CIDRs (optional): `vnet_address_space`, `subnet_containerapps_cidr`, `subnet_private_endpoints_cidr`,
  `subnet_mysql_cidr`
- VPN (optional): `enable_vpn`, `p2s_address_pool`, `p2s_root_cert_public_data`

## How to deploy (local)
```bash
az login
cd deploy/tf
terraform init
terraform apply -var-file="../terraform.tfvars" -var "subscription_id=<your_subscription_id>" -var "mysql_admin_password=<secure_password>"
```

Outputs include:

- `container_app_internal_fqdn` and `internal_app_url`
- `acr_login_server`, `mysql_fqdn`, `cosmosdb_endpoint`

If `enable_vpn=true`, also note `vpn_gateway_public_ip`. Configure P2S clients using your root cert and the address pool
you set.

## App configuration (wired via Terraform)

Your Spring Boot app reads connection details from environment variables. Terraform injects these into the Container
App:

- `SPRING_PROFILES_ACTIVE` -> `environment` (e.g., dev/qa/prod)
- `COSMOS_ENDPOINT`, `COSMOS_KEY`
- `AZURE_STORAGE_CONNECTION_STRING`, `AZURE_STORAGE_CONTAINER_NAME`
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

## CI/CD (GitHub Actions)

A workflow is provided at `.github/workflows/cicd.yml`:

- Job 1 (infra): applies Terraform to provision/update infra
- Job 2 (build_and_push): builds Docker image from `azure-app/Dockerfile` and pushes to ACR
- Job 3 (deploy_app): re-applies Terraform with the new `image_tag` to update the Container App revision

### Required GitHub secrets/variables

- Secrets: `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`, `MYSQL_ADMIN_PASSWORD`,
  `P2S_ROOT_CERT_PUBLIC_DATA` (if VPN)
- Variables: `NAME_PREFIX`, `ENVIRONMENT`, `RESOURCE_GROUP_NAME`, `AZURE_LOCATION`, `STORAGE_ACCOUNT_NAME`,
  `COSMOS_ACCOUNT_NAME`, `ACR_NAME`, `CONTAINER_APP_NAME`, `MYSQL_DATABASE_NAME`, `PRIVATE_DOMAIN_NAME` (optional),
  `ENABLE_VPN` (true/false)

## Accessing the app privately

- The app exposes only an internal endpoint in your VNet
- If you enabled the VPN, connect your client and resolve the app using the output `internal_app_url`
- Without a custom domain, use the `container_app_internal_fqdn` which is resolved by the Private DNS zone linked to the
  VNet

## Destroy
```bash
terraform destroy -var-file="../terraform.tfvars" -var "subscription_id=<your_subscription_id>"
```
