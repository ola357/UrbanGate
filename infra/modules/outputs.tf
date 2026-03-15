output "resource_group_name" { value = azurerm_resource_group.rg.name }
output "container_app_name" { value = azurerm_container_app.app.name }
output "container_app_fqdn" { value = azurerm_container_app.app.ingress[0].fqdn }
output "log_analytics_id" { value = azurerm_log_analytics_workspace.this.id }
output "pg_server_fqdn" { value = azurerm_postgresql_flexible_server.this.fqdn }
output "pg_db_name" { value = azurerm_postgresql_flexible_server_database.db.name }
output "web_app_name" { value = azurerm_linux_web_app.web.name }
output "web_app_default_hostname" { value = azurerm_linux_web_app.web.default_hostname }
