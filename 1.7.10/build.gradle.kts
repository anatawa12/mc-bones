import net.minecraftforge.gradle.user.patch.UserPatchExtension

buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "ossrh-snapshot"
        }
        maven(url = "https://files.minecraftforge.net/maven") {
            name = "forge"
        }
    }
    dependencies {
        classpath("com.anatawa12.forge:ForgeGradle:1.2-1.0.+") {
            isChanging = true
        }
    }
    configurations.classpath.get().resolutionStrategy.cacheDynamicVersionsFor(10, "minutes")
    configurations.classpath.get().resolutionStrategy.cacheChangingModulesFor(10, "minutes")
}

plugins {
    kotlin("jvm")
}

apply(plugin = "forge")

version = property("modVersion")!!
group = property("modGroup")!!
base { archivesBaseName = property("modBaseName")!!.toString() }

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

val Project.minecraft: UserPatchExtension get() =
    (this as ExtensionAware).extensions.getByName("minecraft") as UserPatchExtension
fun Project.minecraft(configure: UserPatchExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("minecraft", configure)

minecraft {
    version = project.property("forgeVersion1.7.10").toString()
    runDir = "run"
}

val shade by configurations.creating
configurations.implementation.get().extendsFrom(shade)

repositories {
    mavenCentral()
}

dependencies {
    shade(kotlin("stdlib-jdk7"))
    shade("org.jetbrains:annotations:20.1.0")
    shade(project(":"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

val processResources by tasks.getting(Copy::class) {
    // this will ensure that this task is redone when the versions change.
    inputs.property("version", project.version)
    inputs.property("mcversion", project.minecraft.version)

    // replace stuff in mcmod.info, nothing else
    from(sourceSets.main.get().resources.srcDirs) {
        include("mcmod.info")

        // replace version and mcversion
        expand(mapOf(
            "version" to project.version,
            "mcversion" to project.minecraft.version
        ))
    }

    // copy everything else, thats not the mcmod.info
    from(sourceSets.main.get().resources.srcDirs) {
        exclude("mcmod.info")
    }
}

val runClient by tasks.getting(JavaExec::class) {
    if (!project.hasProperty("noLogin") && project.hasProperty("minecraft.login.username") && project.hasProperty("minecraft.login.password"))
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        args = args!! + listOf(
            "-username", project.property("minecraft.login.username").toString(),
            "-password", project.property("minecraft.login.password").toString()
        )
}

val jar by tasks.getting(Jar::class) {
    shade.forEach { dep ->
        from(project.zipTree(dep)) {
            exclude("META-INF", "META-INF/**")
            exclude("LICENSE.txt")
        }
    }
}

tasks.compileKotlin {
    kotlinOptions {
        //freeCompilerArgs = ["-XXLanguage:+InlineClasses"]
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val makeSourceDir by tasks.creating {
    doLast {
        buildDir.resolve("sources/main/java").mkdirs()
    }
}
tasks.compileJava.get().dependsOn(makeSourceDir)

tasks.test {
    useJUnitPlatform()
}

runClient.outputs.upToDateWhen { false }
