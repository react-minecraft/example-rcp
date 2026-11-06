plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("com.github.react-minecraft.plugin:react-api:${property("reactApiVersion")}")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.github.g4vrk.Schedula:common:1.1.0")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    processResources {
        val properties = mapOf("version" to project.version.toString())
        inputs.properties(properties)
        filesMatching("rcp-metadata.yml") {
            expand(properties)
        }
    }
}
