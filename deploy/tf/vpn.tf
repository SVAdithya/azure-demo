resource "azurerm_subnet" "snet_gateway" {
  count                = var.enable_vpn ? 1 : 0
  name                 = "GatewaySubnet"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = [cidrsubnet(var.vnet_address_space, 8, 255)]
}

resource "azurerm_public_ip" "vpn_pip" {
  count               = var.enable_vpn ? 1 : 0
  name                = "${var.name_prefix}-vpn-pip"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  allocation_method   = "Dynamic"
  sku                 = "Basic"
}

resource "azurerm_virtual_network_gateway" "vnet_gw" {
  count               = var.enable_vpn ? 1 : 0
  name                = "${var.name_prefix}-vpngw"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  type                = "Vpn"
  vpn_type            = "RouteBased"
  active_active       = false
  enable_bgp          = false
  sku                 = "VpnGw1"

  ip_configuration {
    name                          = "vpngw-ipcfg"
    public_ip_address_id          = azurerm_public_ip.vpn_pip[0].id
    private_ip_address_allocation = "Dynamic"
    subnet_id                     = azurerm_subnet.snet_gateway[0].id
  }

  vpn_client_configuration {
    address_space = [var.p2s_address_pool]
    root_certificate {
      name             = var.p2s_root_cert_name
      public_cert_data = var.p2s_root_cert_public_data
    }
  }
}

output "vpn_gateway_public_ip" {
  value       = var.enable_vpn ? azurerm_public_ip.vpn_pip[0].ip_address : null
  description = "Public IP of the VPN Gateway"
}
