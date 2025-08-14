resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Standard"
  admin_enabled = true
}

output "acr_login_server" {
  value       = azurerm_container_registry.acr.login_server
  description = "Login server for ACR"
}
