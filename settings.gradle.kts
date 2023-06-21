rootProject.name = "meal-planner"
include("backend", "frontend")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        "classpath"(group = "org.postgresql", name = "postgresql", version = "42.6.0")
    }
}
