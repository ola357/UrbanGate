module "stack" {
  source   = "../../modules"
  project  = var.project
  env      = "dev"
  location = var.location

  pg_admin_username = var.pg_admin_username
  pg_admin_password = var.pg_admin_password
  pg_db_name        = var.pg_db_name
  pg_zone           = var.pg_zone
  pg_sku_name       = var.pg_sku_name
  pg_storage_mb     = var.pg_storage_mb

  backend_image              = var.backend_image
  urbangate_security_enabled = var.urbangate_security_enabled
  keycloak_issuer_uri        = var.keycloak_issuer_uri
}
