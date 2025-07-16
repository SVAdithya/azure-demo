# Terraform for Azure Blob Storage and Cosmos DB

This Terraform configuration deploys an Azure Resource Group, an Azure Storage Account with a container, and an Azure Cosmos DB account with a SQL database and a container.

## Prerequisites

- [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli) installed on your local machine.
- [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) installed on your local machine.
- An active Azure subscription.

## Setup

1. **Clone the repository:**

   ```bash
   git clone <repository-url>
   cd <repository-directory>/deploy
   ```

2. **Login to Azure:**

   ```bash
   az login
   ```

3. **Initialize Terraform:**

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
