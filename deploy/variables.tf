variable "resource_group_name" {
  description = "The name of the resource group."
  default     = "rg-terraform-sample"
}

variable "location" {
  description = "The Azure region where the resources will be created."
  default     = "East US"
}

variable "storage_account_name" {
  description = "The name of the storage account."
  default     = "stterraformsamplesa"
}

variable "cosmosdb_account_name" {
  description = "The name of the Cosmos DB account."
  default     = "cosdb-terraform-sample"
}
