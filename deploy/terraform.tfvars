resource_group_name  = "rg-terraform-sample"
location             = "South India"
storage_account_name = "stterraformsamplesa"
cosmosdb_account_name = "cosdb-terraform-sample"

# New variables
name_prefix         = "demo-dev"
environment         = "dev"
acr_name            = "demodevacr1234"
container_app_name  = "demo-dev-azure-app"
mysql_database_name = "appdb"
private_domain_name = ""
# Set via env/secrets for automation:
# mysql_admin_password = "<secure>"
