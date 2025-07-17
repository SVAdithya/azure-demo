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

## Architecture Diagram

```
+-------------------------------------------------------------------------------------------------+
|                                                                                                 |
|  +-------------------------+      +-------------------------+      +-------------------------+  |
|  |   Application Gateway   |----->|      App Service        |----->|        Key Vault        |  |
|  +-------------------------+      +-------------------------+      +-------------------------+  |
|               |                                |                                |                 |
|               |                                |                                |                 |
|  +-------------------------+      +-------------------------+      +-------------------------+  |
|  |      Storage Account    |----->|        Cosmos DB        |<-----|        App Service        |  |
|  +-------------------------+      +-------------------------+      +-------------------------+  |
|                                                                                                 |
+-------------------------------------------------------------------------------------------------+
```

## Architecture Overview

This Terraform configuration creates a secure and private Azure environment for a web application. The architecture consists of the following components:

* **Virtual Network (VNet):** A private network that isolates the resources from the public internet. The VNet is divided into three subnets:
    * **App Service Subnet:** Hosts the App Service.
    * **Private Endpoint Subnet:** Hosts the private endpoints for the other services.
    * **Application Gateway Subnet:** Hosts the Application Gateway.

* **Private Endpoints:** Private endpoints are used to connect to the Storage Account, Cosmos DB, and Key Vault over a private IP address within the VNet. This ensures that all traffic to these services remains on the Azure backbone and is not exposed to the public internet.

* **Network Security Group (NSG):** An NSG is associated with the subnets to control traffic flow. It includes a rule to allow inbound traffic to the App Service from the Application Gateway.

* **Application Gateway:** An Application Gateway is used to publish the App Service to the internet securely. It provides a public IP address and acts as a reverse proxy, forwarding requests to the App Service. It also provides features like SSL termination and a Web Application Firewall (WAF).

* **App Service:** The App Service hosts the web application. It is configured to only accept traffic from the Application Gateway and to connect to the other services using private endpoints.

* **Storage Account, Cosmos DB, and Key Vault:** These services are configured to deny public network access and only allow connections from the private endpoints.
