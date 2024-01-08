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