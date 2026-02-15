plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
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
