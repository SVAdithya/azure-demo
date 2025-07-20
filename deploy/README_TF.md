# Terraform for Azure Blob Storage, Cosmos DB, Key Vault, and App Configuration

This Terraform configuration deploys an Azure Resource Group, an Azure Storage Account with a container, an Azure Cosmos DB account with a SQL database and a container, an Azure Key Vault, and an Azure App Configuration store.

## Prerequisites

- [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli) installed on your local machine.
- [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) installed on your local machine.
- An active Azure subscription.

## Setup

1. **Login to Azure:**
   ```bash
   az login
   ```
2. **Initialize Terraform:**
   Navigate to the `tf` directory and run `terraform init`:
   ```bash
   cd tf
   terraform init
   ```
## Usage

### Planning
To see what resources will be created, run `terraform plan` from the `tf` directory:
```bash
terraform plan -var-file="../terraform.tfvars"
```

### Applying
To deploy the resources, run `terraform apply` from the `tf` directory:
```bash
terraform apply -var-file="../terraform.tfvars"
```
You will be prompted to confirm the deployment. Type `yes` to proceed.

### Modifying Variables
You can change the values for the location, resource group name, storage account name, Cosmos DB account name, Key Vault name, and App Configuration name by editing the `deploy/terraform.tfvars` file.
Alternatively, you can pass the variables directly in the command line:
```bash
terraform apply -var="resource_group_name=my-dynamic-rg-name" -var="location=West US"
```

### Managing Application Properties
The application properties are managed in the `deploy/tf/app.properties` file. To add, remove, or modify a property, simply edit this file. Terraform will automatically update the App Configuration store on the next `terraform apply`.

## Outputs
After the deployment is complete, Terraform will output the following:
- `storage_account_connection_string`: The connection string for the storage account.
- `cosmosdb_endpoint`: The endpoint for the Cosmos DB account.
- `key_vault_uri`: The URI of the Key Vault.
- `app_configuration_endpoint`: The endpoint of the App Configuration store.

```bash
terraform destroy
```
