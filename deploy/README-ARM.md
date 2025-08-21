# Azure Infrastructure ARM Template

This ARM template deploys a complete Azure infrastructure setup including VNet, Cosmos DB, Storage Account, Key Vault, and App Configuration.

## Resources Deployed

### 1. Virtual Network (VNet) - **Starting Point**
- **Resource**: `Microsoft.Network/virtualNetworks`
- **Features**: 
  - Address space: `10.0.0.0/16`
  - Subnet: `10.0.1.0/24`
  - Network Security Group with HTTP/HTTPS rules
  - Service endpoints for Storage, Cosmos DB, and Key Vault

### 2. Storage Account
- **Resource**: `Microsoft.Storage/storageAccounts`
- **Features**:
  - Standard LRS replication
  - StorageV2 kind
  - TLS 1.2 minimum
  - Private endpoint access only (VNet restricted)
  - Blob and file encryption enabled

### 3. Cosmos DB
- **Resource**: `Microsoft.DocumentDB/databaseAccounts`
- **Features**:
  - SQL API (GlobalDocumentDB)
  - Session consistency level
  - VNet integration with service endpoints
  - Automatic backups configured
  - TLS 1.2 minimum

### 4. Key Vault
- **Resource**: `Microsoft.KeyVault/vaults`
- **Features**:
  - Standard SKU
  - Soft delete enabled (90 days retention)
  - VNet restricted access
  - Access policies for admin user
  - Template deployment enabled

### 5. App Configuration
- **Resource**: `Microsoft.AppConfiguration/configurationStores`
- **Features**:
  - Standard SKU
  - VNet restricted access
  - Soft delete enabled (7 days retention)
  - Local auth available

## Network Security

All resources are configured with **private network access**:
- Storage Account: VNet rules deny public access
- Cosmos DB: VNet filtering enabled
- Key Vault: Public network access disabled
- App Configuration: Public network access disabled

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `location` | string | Resource Group location | Azure region for deployment |
| `environmentName` | string | `dev` | Environment (dev/staging/prod) |
| `projectName` | string | `myapp` | Project name for resource naming |
| `adminObjectId` | string | Required | Object ID for Key Vault access |

## Deployment

### Prerequisites

1. Azure CLI installed and configured
2. Appropriate Azure subscription permissions
3. User Object ID for Key Vault access

### Get Your Object ID

```bash
az ad signed-in-user show --query id -o tsv
```

### Quick Deployment

```bash
# Make script executable (if not already)
chmod +x deploy/deploy-infrastructure.sh

# Deploy with default values
./deploy/deploy-infrastructure.sh myapp-rg YOUR-OBJECT-ID

# Deploy with custom values
./deploy/deploy-infrastructure.sh myapp-rg YOUR-OBJECT-ID prod mycompany "West US 2"
```

### Manual Deployment

```bash
# Create resource group
az group create --name myapp-rg --location "East US"

# Validate template
az deployment group validate \
    --resource-group myapp-rg \
    --template-file azure-infrastructure.json \
    --parameters @azure-infrastructure.parameters.json

# Deploy template
az deployment group create \
    --resource-group myapp-rg \
    --template-file azure-infrastructure.json \
    --parameters @azure-infrastructure.parameters.json
```

## Resource Naming Convention

Resources are named using the pattern: `{projectName}-{environmentName}-{resourceType}`

Example for project "myapp" in "dev" environment:
- VNet: `myapp-dev-vnet`
- Cosmos DB: `myapp-dev-cosmos`
- Key Vault: `myapp-dev-kv`
- Storage: `myappdevstorage` (no hyphens due to naming restrictions)
- App Config: `myapp-dev-appconfig`

## Outputs

The template provides the following outputs:
- `vnetId`: Resource ID of the Virtual Network
- `subnetId`: Resource ID of the subnet
- `storageAccountName`: Name of the storage account
- `cosmosDbAccountName`: Name of the Cosmos DB account
- `keyVaultName`: Name of the Key Vault
- `appConfigName`: Name of the App Configuration
- `keyVaultUri`: URI of the Key Vault

## Security Considerations

1. **Network Isolation**: All services are configured for VNet-only access
2. **Encryption**: All data is encrypted at rest and in transit
3. **Access Control**: Key Vault uses access policies (can be changed to RBAC)
4. **Soft Delete**: Enabled for Key Vault and App Configuration
5. **TLS**: Minimum TLS 1.2 enforced

## Post-Deployment Steps

1. Configure application-specific settings in App Configuration
2. Add secrets to Key Vault
3. Set up Cosmos DB databases and containers
4. Configure storage containers and access policies
5. Set up monitoring and alerts

## Troubleshooting

### Common Issues

1. **Key Vault Access Denied**: Ensure the `adminObjectId` parameter is correct
2. **Network Restrictions**: Resources are VNet-restricted; ensure access from appropriate networks
3. **Naming Conflicts**: Resource names must be globally unique (especially storage accounts)

### Validation

```bash
# Check deployment status
az deployment group show --resource-group myapp-rg --name DEPLOYMENT-NAME

# List all resources
az resource list --resource-group myapp-rg --output table
```

## Clean Up

```bash
# Delete the entire resource group (WARNING: This deletes ALL resources)
az group delete --name myapp-rg --yes --no-wait
```

## Integration with Applications

After deployment, use the outputs to configure your applications:

```bash
# Get outputs
az deployment group show \
    --resource-group myapp-rg \
    --name DEPLOYMENT-NAME \
    --query properties.outputs
```

The infrastructure is designed to support microservices architectures with secure, network-isolated Azure PaaS services.