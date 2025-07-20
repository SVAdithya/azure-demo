<details><summary> Infra Azure deploy - Terraform </summary>

# Terraform for Azure Blob Storage and Cosmos DB

This Terraform configuration deploys an Azure Resource Group, an Azure Storage Account with a container, and an Azure Cosmos DB account with a SQL database and a container.

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
You can change the values for the location, resource group name, storage account name, and Cosmos DB account name by editing the `deploy/terraform.tfvars` file.
Alternatively, you can pass the variables directly in the command line:
```bash
terraform apply -var="resource_group_name=my-dynamic-rg-name" -var="location=West US"
```

## Outputs
After the deployment is complete, Terraform will output the following:
- `storage_account_connection_string`: The connection string for the storage account.
- `cosmosdb_endpoint`: The endpoint for the Cosmos DB account.

```bash
terraform destroy
```
</details>
<details><summary> Infra Azure deploy - Azure ARM Template </summary>

# ARM Template for Azure Cosmos DB and Storage

This ARM template deploys an Azure Cosmos DB account and an Azure Storage account.

## How to Deploy

1. **Login to Azure:**
   ```bash
   az login
   ```

2. **Create a resource group:**
   ```bash
   az group create --name <resource-group-name> --location <location>
   ```

3. **Deploy the ARM template:**
   ```bash
   az deployment group create --resource-group <resource-group-name> --template-file deploy.json --parameters storageAccountName=<storage-account-name> cosmosDbAccountName=<cosmos-db-account-name>
   ```

## Using a Parameters File

You can also use a parameters file to pass values to the ARM template. The parameters file is a JSON file that contains the values for the parameters in the ARM template.

1. **Create a parameters file:**
   Create a file named `azuredeploy.parameters.json` with the following content:
   ```json
   {
     "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
     "contentVersion": "1.0.0.0",
     "parameters": {
       "storageAccountName": {
         "value": "<storage-account-name>"
       },
       "cosmosDbAccountName": {
         "value": "<cosmos-db-account-name>"
       }
     }
   }
   ```

2. **Deploy the ARM template with the parameters file:**
   ```bash
   az deployment group create --resource-group <resource-group-name> --template-file deploy.json --parameters @azuredeploy.parameters.json
   ```

</details>