variable "resource_group_name" {
  description = "The name of the resource group."
  # Change the default value to your desired resource group name
  default     = "rg-terraform-sample"
}

variable "location" {
  description = "The Azure region where the resources will be created."
  # Change the default value to your desired location
  default     = "West India"
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

variable "key_vault_name" {
  description = "The name of the Key Vault."
  # Change the default value to your desired Key Vault name
  default     = "kv-terraform-sample"
}

variable "app_configuration_name" {
  description = "The name of the App Configuration."
  # Change the default value to your desired App Configuration name
  default     = "appconf-terraform-sample"
}

variable "app_service_plan_name" {
  description = "The name of the App Service Plan."
  # Change the default value to your desired App Service Plan name
  default     = "asp-terraform-sample"
}

variable "app_service_name" {
  description = "The name of the App Service."
  # Change the default value to your desired App Service name
  default     = "app-terraform-sample"
}

variable "app_insights_name" {
  description = "The name of the Application Insights."
  # Change the default value to your desired Application Insights name
  default     = "appi-terraform-sample"
}
