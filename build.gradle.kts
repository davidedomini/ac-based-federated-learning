plugins {
    id("java")
    id("scala")
}

group = "io.github.davidedomini"
version = "1.0"

repositories {
    mavenCentral()
}

scala {
    zincVersion.set("1.6.1")
}

sourceSets {
    main {
        scala {
            setSrcDirs(listOf("src/main/scala"))
        }
    }
    test {
        scala {
            setSrcDirs(listOf("src/test/scala"))
        }
    }
}

dependencies {
    implementation(libs.scala2)
    implementation(libs.scalapy)
    implementation(libs.alchemist)
    implementation(libs.alchemistGui)
    implementation(libs.alchemistScafi)
}

tasks.test {
    useJUnitPlatform()
}

File(rootProject.rootDir.path + "/src/main/yaml").listFiles()
    .filter { it.extension == "yml" } // pick all yml files in src/main/yaml
    .sortedBy { it.nameWithoutExtension } // sort them, we like reproducibility
    .forEach {
        // one simulation file -> one gradle task
        tasks.register<JavaExec>("run${it.nameWithoutExtension.capitalize()}") {
            group = "Alchemist simulations" // This is for better organization when running ./gradlew tasks
            description = "Launches simulation ${it.nameWithoutExtension}" // Just documentation
            mainClass.set("it.unibo.alchemist.Alchemist") // The class to launch
            classpath = sourceSets["main"].runtimeClasspath // The classpath to use
            // In case our simulation produces data, we write it in the following folder:
            val exportsDir = File("${projectDir.path}/build/exports/${it.nameWithoutExtension}")
            doFirst {
                // this is not executed upfront, but only when the task is actually launched
                // If the export folder does not exist, create it and its parents if needed
                if (!exportsDir.exists()) {
                    exportsDir.mkdirs()
                }
            }
            // These are the program arguments
            args("-y", it.absolutePath, "-e", "$exportsDir/${it.nameWithoutExtension}-${System.currentTimeMillis()}")
            args("-b")
            jvmArgs("-Dscalapy.python.library=python3.11")
            // This tells gradle that this task may modify the content of the export directory
            outputs.dir(exportsDir)
        }
    }

