output "resource_group" { value = azurerm_resource_group.rg.name }
output "acr_name" { value = module.acr.name }
output "acr_login_server" { value = module.acr.login_server }
output "container_app_name" { value = module.backend.container_app_name }
output "container_app_fqdn" { value = module.backend.container_app_fqdn }
