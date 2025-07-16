variable "resource_group_name" {
  description = "The name of the resource group."
  # Change the default value to your desired resource group name
  default     = "rg-terraform-sample"
}

variable "location" {
  description = "The Azure region where the resources will be created."
  # Change the default value to your desired location
  default     = "East US"
}

variable "storage_account_name" {
  description = "The name of the storage account."
  # Change the default value to your desired storage account name
  default     = "stterraformsamplesa"
}

variable "cosmosdb_account_name" {
  description = "The name of the Cosmos DB account."
  # Change the default value to your desired Cosmos DB account name
  default     = "cosdb-terraform-sample"
}
