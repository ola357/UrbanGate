resource "azurerm_service_plan" "web" {
  name                = local.web_app_plan_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = var.location
  os_type             = "Linux"
  sku_name            = var.web_app_sku_name
  tags                = local.tags
}

resource "azurerm_linux_web_app" "web" {
  name                = local.web_app_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = var.location
  service_plan_id     = azurerm_service_plan.web.id
  # https_only          = true
  tags                = local.tags

  site_config {
    always_on        = true
    ftps_state       = "Disabled"
    app_command_line = ""

    application_stack {
      node_version = var.web_app_node_version
    }
  }

  app_settings = {
    WEBSITE_RUN_FROM_PACKAGE           = "1"
    # SCM_DO_BUILD_DURING_DEPLOYMENT     = "false"
    # WEBSITES_ENABLE_APP_SERVICE_STORAGE = "true"
    WEBSITE_NODE_DEFAULT_VERSION       = var.web_app_node_version
  }
}
