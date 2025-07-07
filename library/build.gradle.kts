import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.DokkaConfiguration.Visibility.*
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
}

group = "com.teknobit.biometrik"
version = "1.0.0"

kotlin {
    jvm {
        compilations.all {
            this@jvm.compilerOptions {
                jvmTarget.set(JvmTarget.JVM_18)
            }
        }
    }

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Biometrik"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            webpackTask {
                dependencies {
                }
            }
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.tecknobit.biometrik"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildFeatures {
        buildConfig = true
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )
    coordinates(
        groupId = "io.github.n7ghtm4r3",
        artifactId = "Biometrik",
        version = "1.0.0"
    )
    pom {
        name.set("Biometrik")
        description.set("Biometric authentication for the Compose Multiplatform applications")
        inceptionYear.set("2025")
        url.set("https://github.com/N7ghtm4r3/Biometrik")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://opensource.org/license/apache-2-0")
            }
        }
        developers {
            developer {
                id.set("N7ghtm4r3")
                name.set("Manuel Maurizio")
                email.set("maurizio.manuel2003@gmail.com")
                url.set("https://github.com/N7ghtm4r3")
            }
        }
        scm {
            url.set("https://github.com/N7ghtm4r3/Biometrik")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

buildscript {
    dependencies {
        classpath(libs.dokka.base)
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

tasks.dokkaHtml {
    outputDirectory.set(layout.projectDirectory.dir("../docs"))
    dokkaSourceSets.configureEach {
        moduleName = "Biometrik"
        includeNonPublic.set(true)
        documentedVisibilities.set(setOf(PUBLIC, PROTECTED, PRIVATE, INTERNAL))
    }
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = "(c) 2025 Tecknobit"
    }
}