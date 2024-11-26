import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.7.22"
    // GraphQL
    id("com.apollographql.apollo") version "2.5.14"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.12.0"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.0.0"
    //Lombok
    id("io.freefair.lombok") version "6.6"
}

group = properties("pluginGroup")
version = properties("pluginVersion")


// Configure project's dependencies
repositories {
    jcenter()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


dependencies {
    // GraphQL API
    implementation("com.apollographql.apollo:apollo-runtime:2.5.14")

    // OS detection and file handling
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

    //codiga.yml parsing
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.1")

    implementation("com.google.code.gson:gson:2.10.1")

    // markdown support
    implementation("com.github.rjeschke:txtmark:0.13")

    // For rollbar support
    implementation("com.rollbar:rollbar-java:1.9.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.slf4j:slf4j-log4j12:2.0.5") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set("${project.version}")
    path.set("${project.projectDir}/CHANGELOG.md")
}


tasks {

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    // Set the compatibility versions to 1.11
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }


    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Get the latest available change notes from the changelog file
        changeNotes.set(
            provider {
                changelog.getLatest().toHTML()
            }
        )
    }

//    runIde {
        /*
            Enables debug level logging in the sandbox IDE's idea.log. NOTE: this doesn't enable debug level for the console.
            The category value 'Codiga' comes from 'io.codiga.plugins.jetbrains.Constants.LOGGER_NAME'.
            See docs:
                - https://plugins.jetbrains.com/docs/intellij/ide-infrastructure.html#logging
                - https://plugins.jetbrains.com/docs/intellij/testing-faq.html#how-to-enable-debugtrace-logging
         */
//        systemProperty("idea.log.debug.categories", "Codiga")
//    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}

