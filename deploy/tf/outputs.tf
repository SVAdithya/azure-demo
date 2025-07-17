output "storage_account_connection_string" {
  description = "The connection string of the storage account."
  value       = azurerm_storage_account.storage.primary_connection_string
  sensitive   = true
}

output "cosmosdb_endpoint" {
  description = "The endpoint of the Cosmos DB account."
  value       = azurerm_cosmosdb_account.cosmosdb.endpoint
}

output "app_service_name" {
  description = "The name of the App Service."
  value       = azurerm_linux_web_app.appservice.name
}

output "app_service_hostname" {
  description = "The hostname of the App Service."
  value       = azurerm_linux_web_app.appservice.default_hostname
}
