resource "azurerm_private_endpoint" "pe_blob" {
  name                = "${var.name_prefix}-pe-blob"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  subnet_id           = azurerm_subnet.snet_private_endpoints.id

  private_service_connection {
    name                           = "pe-blob-conn"
    private_connection_resource_id = azurerm_storage_account.storage.id
    is_manual_connection           = false
    subresource_names              = ["blob"]
  }
}

resource "azurerm_private_dns_zone_group" "pe_blob_dns" {
  name                 = "pe-blob-dns"
  private_endpoint_id  = azurerm_private_endpoint.pe_blob.id
  private_dns_zone_ids = [azurerm_private_dns_zone.blob_private_dns.id]
}

resource "azurerm_private_endpoint" "pe_cosmos" {
  name                = "${var.name_prefix}-pe-cosmos"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  subnet_id           = azurerm_subnet.snet_private_endpoints.id

  private_service_connection {
    name                           = "pe-cosmos-conn"
    private_connection_resource_id = azurerm_cosmosdb_account.cosmosdb.id
    is_manual_connection           = false
    subresource_names              = ["Sql"]
  }
}

resource "azurerm_private_dns_zone_group" "pe_cosmos_dns" {
  name                 = "pe-cosmos-dns"
  private_endpoint_id  = azurerm_private_endpoint.pe_cosmos.id
  private_dns_zone_ids = [azurerm_private_dns_zone.cosmos_private_dns.id]
}
