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
