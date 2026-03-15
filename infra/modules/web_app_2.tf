# App Service Plan
resource "azurerm_service_plan" "admin-ui-plan" {
  count               = local.infra-config.appService.enabled ? 1 : 0
  name                = "prm-${terraform.workspace}-admin-ui-plan"
  resource_group_name = azurerm_resource_group.prm.name
  location            = azurerm_resource_group.prm.location
  os_type             = "Linux"
  sku_name            = terraform.workspace == "prod" ? "P1v2" : "S1"
}

# Web App
resource "azurerm_linux_web_app" "admin-ui-web-app" {
  count               = local.infra-config.appService.enabled ? 1 : 0
  name                = "prm-${terraform.workspace}-admin-ui-web-app"
  resource_group_name = azurerm_resource_group.prm.name
  location            = azurerm_resource_group.prm.location
  service_plan_id     = azurerm_service_plan.admin-ui-plan[0].id

  site_config {
    always_on        = true
    ftps_state       = "Disabled"
    app_command_line = "/home/site/wwwroot/startup.sh"

    application_stack {
      node_version = "22-lts"
    }
  }

  identity {
    type = "SystemAssigned"
  }

  app_settings = {
    WEBSITE_RUN_FROM_PACKAGE              = "1" # ZIP deploy
    APPINSIGHTS_CONNECTION_STRING         = azurerm_application_insights.admin-ui-ai[0].connection_string
    APPLICATIONINSIGHTS_CONNECTION_STRING = azurerm_application_insights.admin-ui-ai[0].connection_string
    APPLICATIONINSIGHTS_ROLE_NAME         = "prm-${terraform.workspace}-admin-ui-web-app"
    WEBSITE_NODE_DEFAULT_VERSION          = "~22"
  }

}
