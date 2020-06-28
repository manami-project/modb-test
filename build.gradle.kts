plugins {
    kotlin("jvm") version "1.3.72"
    id("com.jfrog.bintray") version "1.8.5"
    `maven-publish`
    `java-library`
}

repositories {
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/manami-project/maven")
    }
}

group = "io.github.manamiproject"
version = project.findProperty("releaseVersion") as String? ?: ""
val projectName = "modb-test"

dependencies {
    api(kotlin("test-junit5"))
    api("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    api("org.assertj:assertj-core:3.16.1")
    api("com.github.tomakehurst:wiremock-jre8:2.26.3")

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.junit.platform:junit-platform-launcher:1.6.0")
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
}

bintray {
    user = project.findProperty("bintrayUser") as String? ?: ""
    key = project.findProperty("bintrayApiKey") as String? ?: ""

    setPublications("maven")

    with(pkg) {
        repo = "maven"
        name = "modb-test"
        with(version) {
            name = project.version.toString()
            vcsTag = project.version.toString()
        }
        setLicenses("AGPL-V3")
        vcsUrl = "https://github.com/manami-project/$projectName"
    }
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
                url.set("https://github.com/manami-project/$projectName")

                licenses {
                    license {
                        name.set("AGPL-V3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:manami-project/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com:manami-project/$projectName.git")
                    url.set("https://github.com/manami-project/$projectName")
                }
            }
        }
    }
}