module "common" {
  source   = "../../modules/common"
  project  = var.project
  env      = "dev"
  location = var.location
}

resource "azurerm_resource_group" "rg" {
  name     = "${module.common.prefix}-rg"
  location = var.location
  tags     = module.common.tags
}

module "log" {
  source              = "../../modules/log_analytics"
  name                = "${module.common.prefix}-law"
  resource_group_name = azurerm_resource_group.rg.name
  location            = var.location
  tags                = module.common.tags
}

module "acr" {
  source              = "../../modules/acr"
  name                = replace("${module.common.prefix}acr", "-", "")
  resource_group_name = azurerm_resource_group.rg.name
  location            = var.location
  tags                = module.common.tags
}

module "pg" {
  source              = "../../modules/postgres"
  name                = "${module.common.prefix}-pg"
  resource_group_name = azurerm_resource_group.rg.name
  location            = var.location
  tags                = module.common.tags

  admin_username = var.pg_admin_username
  admin_password = var.pg_admin_password
  db_name        = var.pg_db_name
  sku_name       = var.pg_sku_name
  storage_mb     = var.pg_storage_mb
}

locals {
  db_url        = "jdbc:postgresql://${module.pg.server_fqdn}:5432/${module.pg.db_name}?sslmode=require"
  initial_image = "${module.acr.login_server}/urbangate-backend:bootstrap"
  pg_user       = "${var.pg_admin_username}@${split(".", module.pg.server_fqdn)[0]}"
}

module "backend" {
  source                     = "../../modules/container_app"
  project                    = var.project
  env                        = "dev"
  location                   = var.location
  resource_group_name        = azurerm_resource_group.rg.name
  tags                       = module.common.tags

  log_analytics_id           = module.log.id
  acr_login_server           = module.acr.login_server

  container_app_name         = "${module.common.prefix}-backend"
  image                      = local.initial_image

  spring_profiles_active     = "dev"
  urbangate_security_enabled = var.urbangate_security_enabled
  keycloak_issuer_uri        = var.keycloak_issuer_uri

  db_url      = local.db_url
  db_username = local.pg_user
  db_password = var.pg_admin_password

  revision_mode = "Single"
}
