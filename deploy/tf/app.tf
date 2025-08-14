resource "azurerm_log_analytics_workspace" "law" {
  name                = "${var.name_prefix}-law"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_container_app_environment" "cae" {
  name                           = "${var.name_prefix}-cae"
  location                       = azurerm_resource_group.rg.location
  resource_group_name            = azurerm_resource_group.rg.name
  log_analytics_workspace_id     = azurerm_log_analytics_workspace.law.id
  infrastructure_subnet_id       = azurerm_subnet.snet_containerapps.id
  internal_load_balancer_enabled = true
}

# Get keys for Cosmos and Storage
data "azurerm_cosmosdb_account" "cosmos" {
  name                = azurerm_cosmosdb_account.cosmosdb.name
  resource_group_name = azurerm_resource_group.rg.name
}

data "azurerm_storage_account" "storage" {
  name                = azurerm_storage_account.storage.name
  resource_group_name = azurerm_resource_group.rg.name
}

# Construct Storage connection string
locals {
  storage_connection_string = "DefaultEndpointsProtocol=https;AccountName=${data.azurerm_storage_account.storage.name};AccountKey=${data.azurerm_storage_account.storage.primary_access_key};EndpointSuffix=core.windows.net"
}

resource "azurerm_container_app" "app" {
  name                         = var.container_app_name
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  registry {
    server   = azurerm_container_registry.acr.login_server
    username = azurerm_container_registry.acr.admin_username
    password = azurerm_container_registry.acr.admin_password
  }

  ingress {
    external_enabled = false
    target_port      = 8080
    transport        = "auto"
  }

  template {
    container {
      name   = var.image_repository
      image  = "${azurerm_container_registry.acr.login_server}/${var.image_repository}:${var.image_tag}"
      cpu    = var.container_cpu
      memory = var.container_memory

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = var.environment
      }
      env {
        name  = "COSMOS_ENDPOINT"
        value = data.azurerm_cosmosdb_account.cosmos.endpoint
      }
      secret {
        name  = "cosmos-key"
        value = data.azurerm_cosmosdb_account.cosmos.primary_key
      }
      env {
        name        = "COSMOS_KEY"
        secret_name = "cosmos-key"
      }

      env {
        name  = "AZURE_STORAGE_CONTAINER_NAME"
        value = azurerm_storage_container.container.name
      }
      secret {
        name  = "storage-conn"
        value = local.storage_connection_string
      }
      env {
        name        = "AZURE_STORAGE_CONNECTION_STRING"
        secret_name = "storage-conn"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:mysql://${azurerm_mysql_flexible_server.mysql.fqdn}:3306/${var.mysql_database_name}?useSSL=true&requireSSL=true&serverTimezone=UTC"
      }
      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.mysql_admin_username
      }
      secret {
        name  = "mysql-password"
        value = var.mysql_admin_password
      }
      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "mysql-password"
      }
    }
    http_scale_rule {
      name                = "default"
      concurrent_requests = 50
      auth {
        secret_name       = null
        trigger_parameter = null
      }
    }
  }
}

output "container_app_internal_fqdn" {
  value       = azurerm_container_app.app.latest_revision_fqdn
  description = "Internal FQDN of the Container App"
}
