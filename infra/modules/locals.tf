locals {
  prefix = "${var.project}-${var.env}"
  tags = {
    project   = var.project
    env       = var.env
    managedBy = "terraform"
  }

  resource_group_name = "${local.prefix}-rg"
  log_analytics_name  = "${local.prefix}-law"
  postgres_name       = "${local.prefix}-pg"
  container_app_name  = "${local.prefix}-backend"

  spring_profiles_active = var.spring_profiles_active != "" ? var.spring_profiles_active : var.env
  db_url                 = "jdbc:postgresql://${azurerm_postgresql_flexible_server.this.fqdn}:5432/${var.pg_db_name}?sslmode=require"
  pg_user                = var.pg_admin_username
}
