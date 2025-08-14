variable "resource_group_name" {
  description = "The name of the resource group."
  default     = "rg-terraform-sample"
}

variable "location" {
  description = "The Azure region where the resources will be created."
  default     = "West India"
}

variable "storage_account_name" {
  description = "The name of the storage account."
  default     = "stterraformsamplesa"
}

variable "cosmosdb_account_name" {
  description = "The name of the Cosmos DB account."
  default     = "cosdb-terraform-sample"
}

variable "acr_name" {
  description = "Azure Container Registry name (globally unique)."
  type        = string
}

variable "name_prefix" {
  description = "Short prefix used to name resources (e.g., demo-dev)."
  type        = string
}

variable "environment" {
  description = "Deployment environment (dev|qa|prod)."
  type        = string
  default     = "dev"
}

variable "vnet_address_space" {
  description = "Address space for VNet"
  type        = string
  default     = "10.10.0.0/16"
}

variable "subnet_containerapps_cidr" {
  description = "Subnet CIDR for Container Apps"
  type        = string
  default     = "10.10.1.0/24"
}

variable "subnet_private_endpoints_cidr" {
  description = "Subnet CIDR for Private Endpoints"
  type        = string
  default     = "10.10.2.0/24"
}

variable "subnet_mysql_cidr" {
  description = "Subnet CIDR for MySQL Flexible Server (delegated)"
  type        = string
  default     = "10.10.3.0/24"
}

variable "mysql_admin_username" {
  description = "MySQL admin username"
  type        = string
  default     = "dbadmin"
}

variable "mysql_admin_password" {
  description = "MySQL admin password"
  type        = string
  sensitive   = true
}

variable "mysql_database_name" {
  description = "Application database name"
  type        = string
  default     = "appdb"
}

variable "container_app_name" {
  description = "Container App name"
  type        = string
}

variable "image_repository" {
  description = "Image repository name in ACR (e.g., azure-app)"
  type        = string
  default     = "azure-app"
}

variable "image_tag" {
  description = "Image tag to deploy"
  type        = string
  default     = "latest"
}

variable "container_cpu" {
  description = "vCPU for Container App"
  type        = number
  default     = 0.5
}

variable "container_memory" {
  description = "Memory in Gi for Container App"
  type        = string
  default     = "1.0Gi"
}

variable "private_domain_name" {
  description = "Optional private DNS zone for custom internal domain (e.g., internal.local)"
  type        = string
  default     = ""
}

variable "enable_vpn" {
  description = "Whether to deploy a Point-to-Site VPN Gateway"
  type        = bool
  default     = false
}

variable "p2s_address_pool" {
  description = "Address pool for P2S VPN clients"
  type        = string
  default     = "172.16.0.0/24"
}

variable "p2s_root_cert_name" {
  description = "Name for the P2S root certificate"
  type        = string
  default     = "p2s-root"
}

variable "p2s_root_cert_public_data" {
  description = "Base64-encoded public certificate data of the P2S root cert (PEM without headers)"
  type        = string
  default     = ""
}
