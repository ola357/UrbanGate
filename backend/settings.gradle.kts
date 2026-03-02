pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "urbangate-backend"

include(":modules:shared")
include(":modules:app")
include(":modules:iam")
include(":modules:property")
include(":modules:access")
include(":modules:billing")
include(":modules:wallet")
include(":modules:directory")
include(":modules:notifications")
include(":modules:audit")
