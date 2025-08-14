locals {
  use_custom_private_domain = length(trim(var.private_domain_name)) > 0
}

resource "azurerm_private_dns_zone" "app_private" {
  count               = local.use_custom_private_domain ? 1 : 0
  name                = var.private_domain_name
  resource_group_name = azurerm_resource_group.rg.name
}

resource "azurerm_private_dns_zone_virtual_network_link" "app_private_link" {
  count                 = local.use_custom_private_domain ? 1 : 0
  name                  = "app-private-domain-link"
  resource_group_name   = azurerm_resource_group.rg.name
  private_dns_zone_name = azurerm_private_dns_zone.app_private[0].name
  virtual_network_id    = azurerm_virtual_network.vnet.id
}

resource "azurerm_private_dns_cname_record" "app_cname" {
  count               = local.use_custom_private_domain ? 1 : 0
  name                = "app"
  zone_name           = azurerm_private_dns_zone.app_private[0].name
  resource_group_name = azurerm_resource_group.rg.name
  ttl                 = 300
  record              = azurerm_container_app.app.latest_revision_fqdn
}

output "internal_app_url" {
  value       = local.use_custom_private_domain ? "app.${var.private_domain_name}" : azurerm_container_app.app.latest_revision_fqdn
  description = "Internal URL to access the app over VPN"
}
