import java.io.BufferedReader
import java.io.InputStreamReader

var projectVersion = "0.1"
var defaultPerm = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP

plugins {
    kotlin("jvm") version "1.7.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "com.hasunemiku2015"
version = projectVersion

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map{zipTree(it)})
    }

    register("upload") {
        dependsOn(build)

        doLast {
            val removeOldFile = true
            val process = Runtime.getRuntime().exec("python server_pro.py ${rootProject.name}-$version.jar $removeOldFile")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line = reader.readLine()
            while (line != null) {
                println(line)
                line = reader.readLine()
            }
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven(uri("https://jitpack.io"))
    maven(uri("https://repo.dmulloy2.net/repository/public/"))
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.18-R0.1-SNAPSHOT")

    implementation("com.github.deanveloper:KBukkit:master-SNAPSHOT")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.7.0")

    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
}

bukkit {
    name = rootProject.name
    version = projectVersion
    main = "$group.minecraftnano.NanoPlugin"
    apiVersion = "1.16"
    softDepend = listOf("ProtocolLib")

    permissions {
        register("minecraftnano.path") {
            description = "Allow player view file paths in server directory via tab completion."
            default = defaultPerm
        }
    }

    commands {
        register("nano") {
            description = "Edit a file in server directory or its sub directory."
            usage = "/nano <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("nanopref") {
            description = "Set the max no. of lines and max. char per line displayed in chat (Default: 20, 80)."
            usage = "/nanopref <max_line_chat> <max_char_line>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("rm") {
            description = "Remove a file/directory in server directory or its sub directory."
            usage = "/rm <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("mkdir") {
            description = "Create a directory in server directory or its sub directory."
            usage = "/mkdir <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("mkfile") {
            description = "Create a file in server directory or its sub directory."
            usage = "/mkdir <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("mv") {
            description = "Move/Rename a file in server directory or its sub directory."
            usage = "/mv <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("cp") {
            description = "Copy a file in server directory or its sub directory."
            usage = "/cp <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }

        register("path") {
            description = "Specify a file as destination. Used in conjunction with /mv and /cp."
            usage = "/cp <path-with-spaces>"
            permission = "minecraftnano.use"
            permissionMessage = "§4You have no permission to use this command."
        }
    }

    libraries = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib:1.7.0",
        "org.jetbrains.kotlin:kotlin-reflect:1.7.0"
    )
}