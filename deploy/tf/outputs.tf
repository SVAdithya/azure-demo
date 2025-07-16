output "storage_account_connection_string" {
  description = "The connection string of the storage account."
  value       = azurerm_storage_account.storage.primary_connection_string
  sensitive   = true
}

output "cosmosdb_endpoint" {
  description = "The endpoint of the Cosmos DB account."
  value       = azurerm_cosmosdb_account.cosmosdb.endpoint
}
