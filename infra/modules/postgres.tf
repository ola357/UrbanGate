resource "azurerm_postgresql_flexible_server" "this" {
  name                   = local.postgres_name
  resource_group_name    = azurerm_resource_group.rg.name
  location               = var.location
  version                = "16"
  administrator_login    = var.pg_admin_username
  administrator_password = var.pg_admin_password

  zone = var.pg_zone == "" ? null : var.pg_zone

  sku_name   = var.pg_sku_name
  storage_mb = var.pg_storage_mb

  backup_retention_days        = 7
  geo_redundant_backup_enabled = false

  authentication {
    password_auth_enabled = true
  }

  tags = local.tags
}

resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = var.pg_db_name
  server_id = azurerm_postgresql_flexible_server.this.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

# bootstrap-friendly: allow Azure services
resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.this.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}
