import Build_gradle.Versions.JUNIT_VERSION

plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
    `java-library`
    jacoco
    id("com.github.nbaztec.coveralls-jacoco") version "1.2.17"
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
    api(kotlin("stdlib"))
    api("org.junit.jupiter:junit-jupiter-engine:$JUNIT_VERSION")
    api("org.junit.jupiter:junit-jupiter-params:$JUNIT_VERSION")
    api("org.junit.platform:junit-platform-launcher:1.10.0")
    api("org.assertj:assertj-core:3.24.2")
    api("com.github.tomakehurst:wiremock:3.0.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")

    implementation(platform(kotlin("bom")))
}

kotlin {
    explicitApi()
    jvmToolchain(Versions.JVM_TARGET.toInt())
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports.html.required.set(false)
    reports.junitXml.required.set(false)
    maxParallelForks = Runtime.getRuntime().availableProcessors()
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
            name = projectName
            url = uri("https://maven.pkg.github.com/$githubUsername/$projectName")
            credentials {
                username = parameter("GH_USERNAME", githubUsername)
                password = parameter("GH_PACKAGES_RELEASE_TOKEN")
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

coverallsJacoco {
    reportPath = "${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"
}

tasks.jacocoTestReport {
    reports {
        html.required.set(false)
        xml.required.set(true)
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"))
    }
    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
}

object Versions {
    const val JVM_TARGET = "21"
    const val JUNIT_VERSION = "5.9.2"
}

fun parameter(name: String, default: String = ""): String {
    val env = System.getenv(name) ?: ""
    if (env.isNotBlank()) {
        return env
    }

    val property = project.findProperty(name) as String? ?: ""
    if (property.isNotEmpty()) {
        return property
    }

    return default
}