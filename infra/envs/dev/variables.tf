variable "project" {
  type    = string
  default = "urbangate"
}
variable "location" {
  type    = string
  default = "westeurope"
}

variable "pg_admin_username" {
  type    = string
  default = "ugadmin"
}
variable "pg_admin_password" {
  type      = string
  sensitive = true
}
variable "pg_db_name" {
  type    = string
  default = "urbangate"
}
variable "pg_zone" {
  type    = string
  default = ""
}
variable "pg_sku_name" {
  type    = string
  default = "B_Standard_B1ms"
}
variable "pg_storage_mb" {
  type    = number
  default = 32768
}

variable "urbangate_security_enabled" {
  type    = string
  default = "false"
}
variable "keycloak_issuer_uri" {
  type    = string
  default = ""
}

variable "backend_image" {
  type    = string
  default = ""
}
