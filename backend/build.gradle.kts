import nu.studer.gradle.jooq.JooqGenerate
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging
import org.springdoc.openapi.gradle.plugin.OpenApiGeneratorTask
import java.sql.DriverManager
import java.util.*

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"

    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"

    id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
    id("nu.studer.jooq") version "8.2"
    id("org.flywaydb.flyway") version "9.8.1"
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.5"

    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql")

    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")

    implementation("org.jsoup:jsoup:1.16.1")

    jooqGenerator("org.postgresql:postgresql:42.6.0")
}

val buildProps = Properties().also {
    val buildConfig = file("$projectDir/build-config.properties")
    if (buildConfig.exists()) {
        buildConfig.bufferedReader().use { reader ->
            it.load(reader)
        }
    } else {
        error("Error: build-config.properties not found")
    }
}

val buildDatabaseName = buildProps["build.database.name"]
val buildDatabaseHost = buildProps["build.database.host"]
val buildDatabasePort = buildProps["build.database.port"]

tasks.flywayMigrate {
    dependsOn("processResources")

    doFirst {
        //connect as superuser and drop and create database
        val url =
            "jdbc:postgresql://$buildDatabaseHost:$buildDatabasePort/${buildProps["build.database.superuser.database"]}"
        val user = buildProps["build.database.superuser.name"] as String
        val password = buildProps["build.database.superuser.password"] as String
        val driver = buildProps["build.database.driver"] as String

        Class.forName(driver)
        DriverManager.getConnection(url, user, password).use { conn ->
            conn.createStatement().use { stmt ->
                //check if db exists
                val dbExists = stmt.executeQuery(
                    "SELECT EXISTS(SELECT datname FROM pg_catalog.pg_database WHERE datname = '$buildDatabaseName')"
                ).use { rs ->
                    rs.next()
                    rs.getBoolean(1)
                }
                if (!dbExists) {
                    println("Database $buildDatabaseName does not exist, creating it...")
                    stmt.execute("CREATE DATABASE \"$buildDatabaseName\" WITH OWNER = \"${buildProps["build.database.username"]}\"")
                }
            }
        }
    }
}

flyway {
    cleanOnValidationError = true
    cleanDisabled = false
    url =
        "jdbc:postgresql://$buildDatabaseHost:$buildDatabasePort/$buildDatabaseName"
    user = buildProps["build.database.username"] as String
    password = buildProps["build.database.password"] as String
    schemas = arrayOf("public")
    locations = arrayOf("classpath:db/migration")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = buildProps["build.database.driver"] as String
                    url =
                        "jdbc:postgresql://$buildDatabaseHost:$buildDatabasePort/$buildDatabaseName"
                    user = buildProps["build.database.username"] as String
                    password = buildProps["build.database.password"] as String
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                    }
                    target.apply {
                        packageName = "at.robbert.mealplanner.jooq"
                        directory = "${buildDir.absolutePath}/generated/jooq/primary"
                    }
                    strategy.name = "org.jooq.codegen.example.JPrefixGeneratorStrategy"
                }
            }
        }
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    dependsOn("flywayMigrate")

    inputs.dir("$projectDir/src/main/resources/db/migration")
    outputs.dir("$buildDir/generated/jooq/primary")
    outputs.cacheIf { true }

    allInputsDeclared.set(true)
}

openApi {
    outputDir.set(file("$buildDir/docs"))
    apiDocsUrl.set("http://localhost:18080/v3/api-docs")
    outputFileName.set("swagger.json")
    waitTimeInSeconds.set(30)
    customBootRun {
        environment.put("DB_URL", "jdbc:postgresql://$buildDatabaseHost:$buildDatabasePort/$buildDatabaseName")
        environment.put("DB_USER", buildProps["build.database.username"] as String)
        environment.put("DB_PASSWORD", buildProps["build.database.password"] as String)

        args.set(listOf("--spring.profiles.active=springdoc"))
    }
}

springBoot {
    mainClass.set("at.robert.mealplanner.MealPlannerApplicationKt")
}

tasks.withType<OpenApiGeneratorTask> {
    inputs.dir("$projectDir/src/main/kotlin/at/robert/mealplanner/controller")
    inputs.dir("$projectDir/src/main/kotlin/at/robert/mealplanner/data")

    outputs.file("$buildDir/docs/swagger.json")
    outputs.cacheIf { true }
}

tasks.all {
    if (name == "forkedSpringBootRun") {
        mustRunAfter("compileTestJava", "test", "bootJar", "jar")
    }
}
