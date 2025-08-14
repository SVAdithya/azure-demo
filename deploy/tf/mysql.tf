resource "azurerm_mysql_flexible_server" "mysql" {
  name                   = "${var.name_prefix}-mysql"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  administrator_login    = var.mysql_admin_username
  administrator_password = var.mysql_admin_password
  version                = "8.0.21"
  sku_name               = "B_Standard_B1ms"

  high_availability {
    mode = "ZoneRedundant"
  }

  storage {
    size_gb           = 20
    auto_grow_enabled = true
    iops              = 360
  }

  backup {
    retention_days = 7
  }

  network {
    delegated_subnet_id = azurerm_subnet.snet_mysql.id
    private_dns_zone_id = azurerm_private_dns_zone.mysql_private_dns.id
  }
}

resource "azurerm_mysql_flexible_database" "appdb" {
  name                = var.mysql_database_name
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azurerm_mysql_flexible_server.mysql.name
  charset             = "utf8"
  collation           = "utf8_general_ci"
}

output "mysql_fqdn" {
  value       = azurerm_mysql_flexible_server.mysql.fqdn
  description = "Private FQDN of MySQL server"
}
