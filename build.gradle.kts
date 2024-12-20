import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val user = "MaleficCompose"
val repo = "MaleficExtensions"
val g = "xyz.malefic.compose"
val artifact = "ext"
val v = "1.2.0"
val desc = "A Compose Desktop library containing common extension methods"

val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
    alias(libs.plugins.compose.kotlin)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
    alias(libs.plugins.compose)
    alias(libs.plugins.central)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = g
version = v

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.common)
    implementation(compose.animation)
    implementation(compose.foundation)
    implementation(libs.precompose)
}

spotless {
    kotlin {
        ktlint().customRuleSets(
            listOf(
                "io.nlopez.compose.rules:ktlint:0.4.22",
            ),
        )
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = g
            artifactId = artifact
            version = v

            from(components["java"])

            pom {
                name.set(repo)
                description.set(desc)
                url.set("https://github.com/$user/$repo")
                developers {
                    developer {
                        name.set("Om Gupta")
                        email.set("ogupta4242@gmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$user/$repo.git")
                    developerConnection.set("scm:git:ssh://github.com/$user/$repo.git")
                    url.set("https://github.com/$user/$repo")
                }
            }
        }
        repositories {
            maven {
                url = localMavenRepo
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

centralPortalPlus {
    url = localMavenRepo
    username = System.getenv("centralPortalUsername") ?: ""
    password = System.getenv("centralPortalPassword") ?: ""
    publishingType = PublishingType.AUTOMATIC
}

tasks.apply {
    build {
        dependsOn(dokkaGenerate)
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }
}
