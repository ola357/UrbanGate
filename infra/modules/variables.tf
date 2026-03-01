variable "project" { type = string }
variable "env" { type = string }
variable "location" { type = string }

variable "pg_admin_username" { type = string }
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

variable "backend_image" { type = string }

variable "spring_profiles_active" {
  type    = string
  default = ""
}
variable "urbangate_security_enabled" { type = string }
variable "keycloak_issuer_uri" {
  type    = string
  default = ""
}

variable "container_app_cpu" {
  type    = number
  default = 0.5
}
variable "container_app_memory" {
  type    = string
  default = "1Gi"
}

variable "revision_mode" {
  type    = string
  default = "Single"
}
