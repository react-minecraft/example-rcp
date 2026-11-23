import org.gradle.api.attributes.java.TargetJvmVersion

plugins {
    java
}

configurations.configureEach {
    if (name == "compileClasspath" || name.endsWith("CompileClasspath")) {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("com.github.g4vrk.Schedula:common:b902674126")
    compileOnly("com.github.react-minecraft.plugin:react-api:${property("reactApiVersion")}")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    compileOnly("org.incendo:cloud-core:2.0.0")
    compileOnly("com.github.g4vrk:FunctionalActions:2.2.0")
    compileOnly("com.github.g4vrk.TextSerializer:legacy:1.1.0")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(16)
    }

    processResources {
        val properties = mapOf("version" to project.version.toString())
        inputs.properties(properties)
        filesMatching("rcp-metadata.yml") {
            expand(properties)
        }
    }
}
