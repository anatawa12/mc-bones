plugins {
    kotlin("jvm") version "1.4.20" apply false
    java
}

version = property("modVersion")!!
group = property("modGroup")!!
base { archivesBaseName = property("modBaseName")!!.toString() }

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.google.guava:guava:17.0")
    compileOnly("org.lwjgl.lwjgl:lwjgl:2.9.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.test {
    useJUnitPlatform()
}
