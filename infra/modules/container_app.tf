resource "azurerm_container_app_environment" "env" {
  name                       = "${local.prefix}-acae"
  location                   = var.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.this.id
  tags                       = local.tags
}

resource "azurerm_container_app" "app" {
  name                         = local.container_app_name
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = var.revision_mode
  tags                         = local.tags

  identity { type = "SystemAssigned" }

  secret {
    name  = "db-password"
    value = var.pg_admin_password
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {

    min_replicas = 0
    max_replicas = 2

    container {
      name   = "backend"
      image  = var.backend_image
      cpu    = var.container_app_cpu
      memory = var.container_app_memory

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = local.spring_profiles_active
      }
      env {
        name  = "URBANGATE_SECURITY_ENABLED"
        value = var.urbangate_security_enabled
      }
      env {
        name  = "KEYCLOAK_ISSUER_URI"
        value = var.keycloak_issuer_uri
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = local.db_url
      }
      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = local.pg_user
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      liveness_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/liveness"
        interval_seconds        = 30
        timeout                 = 5
        failure_count_threshold = 3
      }
      readiness_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/readiness"
        interval_seconds        = 15
        timeout                 = 5
        failure_count_threshold = 3
      }
    }
  }
}
