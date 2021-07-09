import Build_gradle.Versions.JUNIT_VERSION

plugins {
    kotlin("jvm") version "1.5.20"
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

group = "io.github.manamiproject"
version = project.findProperty("release.version") as String? ?: ""
val projectName = "modb-test"
val githubUsername = "manami-project"

dependencies {
    api(kotlin("test-junit5"))
    api(kotlin("stdlib-jdk8"))
    api("org.junit.jupiter:junit-jupiter-engine:$JUNIT_VERSION")
    api("org.junit.jupiter:junit-jupiter-params:$JUNIT_VERSION")
    api("org.junit.platform:junit-platform-launcher:1.7.2")
    api("org.assertj:assertj-core:3.19.0")
    api("com.github.tomakehurst:wiremock:2.27.2")

    implementation(platform(kotlin("bom")))
}

kotlin {
    explicitApi()
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = Versions.JVM_TARGET
    freeCompilerArgs = listOf("-Xinline-classes")
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = Versions.JVM_TARGET
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports.html.isEnabled = false
    reports.junitXml.isEnabled = false
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

object Versions {
    const val JVM_TARGET = "11"
    const val JUNIT_VERSION = "5.7.2"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javaDoc by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$githubUsername/$projectName")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: githubUsername
                password = project.findProperty("gpr.key") as String? ?: ""
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = projectName
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javaDoc.get())

            pom {
                packaging = "jar"
                name.set(projectName)
                description.set("This lib contains all essential dependencies as well as some convenience functions and classes for creating tests in modb prefixed kotlin projects.")
                url.set("https://github.com/$githubUsername/$projectName")

                licenses {
                    license {
                        name.set("AGPL-V3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:$githubUsername/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com:$githubUsername/$projectName.git")
                    url.set("https://github.com/$githubUsername/$projectName")
                }
            }
        }
    }
}