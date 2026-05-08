terraform {
  backend "azurerm" {
    resource_group_name  = "urbangate-tfstate-rg"
    storage_account_name = "ugtfstatestrgacct"
    container_name       = "tfstate"
    key                  = "dev.tfstate"
  }
}
