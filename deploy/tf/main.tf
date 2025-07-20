variable "subscription_id" {}

provider "azurerm" {
  features {}
  subscription_id = var.subscription_id
}

# Resource Group
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

# Storage Account
resource "azurerm_storage_account" "storage" {
  name                     = var.storage_account_name
  resource_group_name      = azurerm_resource_group.rg.name
  location                 = azurerm_resource_group.rg.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

# Storage Container
resource "azurerm_storage_container" "container" {
  name                  = "test1"
  storage_account_name  = azurerm_storage_account.storage.name
  container_access_type = "private"
}

# Cosmos DB Account
resource "azurerm_cosmosdb_account" "cosmosdb" {
  name                = var.cosmosdb_account_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  offer_type          = "Standard"
  kind                = "GlobalDocumentDB"

  consistency_policy {
    consistency_level       = "Session"
    max_interval_in_seconds = 5
    max_staleness_prefix    = 100
  }

  geo_location {
    location          = azurerm_resource_group.rg.location
    failover_priority = 0
  }
}

# Key Vault
resource "azurerm_key_vault" "kv" {
  name                = var.key_vault_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku_name            = "standard"
  tenant_id           = data.azurerm_client_config.current.tenant_id

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    key_permissions = [
      "Get",
      "Create",
      "Delete",
      "List",
      "Update",
      "Import",
      "Backup",
      "Restore",
      "Recover",
      "Purge",
    ]

    secret_permissions = [
      "Get",
      "List",
      "Set",
      "Delete",
      "Backup",
      "Restore",
      "Recover",
      "Purge",
    ]
  }
}

# App Configuration
resource "azurerm_app_configuration" "appconf" {
  name                = var.app_configuration_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "standard"
}

# Secret for App Configuration Connection String
resource "azurerm_key_vault_secret" "appconf_conn_string" {
  name         = "app-configuration-connection-string"
  value        = azurerm_app_configuration.appconf.primary_read_key[0].connection_string
  key_vault_id = azurerm_key_vault.kv.id
}

data "azurerm_client_config" "current" {}

locals {
  properties = {
    for line in split("\n", file("${path.module}/app.properties")) :
    split("=", line)[0] => split("=", line)[1]
    if can(regex("=", line))
  }
}

resource "azurerm_app_configuration_key" "app_properties" {
  for_each          = local.properties
  configuration_store_id = azurerm_app_configuration.appconf.id
  key               = each.key
  value             = each.value
}

# Cosmos DB SQL Database
resource "azurerm_cosmosdb_sql_database" "database" {
  name                = "pocs"
  resource_group_name = azurerm_resource_group.rg.name
  account_name        = azurerm_cosmosdb_account.cosmosdb.name
}

# Cosmos DB SQL Container
resource "azurerm_cosmosdb_sql_container" "container" {
  name                  = "items"
  resource_group_name   = azurerm_resource_group.rg.name
  account_name          = azurerm_cosmosdb_account.cosmosdb.name
  database_name         = azurerm_cosmosdb_sql_database.database.name
  partition_key_version = 1
  partition_key_paths    = ["/id"]
  autoscale_settings {
    max_throughput = 4000
  }
}
