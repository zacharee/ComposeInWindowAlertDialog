import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.zwander.compose.alertdialog.sample"

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

val javaVersionEnum: JavaVersion = JavaVersion.VERSION_21

kotlin {
    jvmToolchain(javaVersionEnum.toString().toInt())

    jvm {
        withJava()
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.fromTarget(javaVersionEnum.toString())
                }
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xdont-warn-on-error-suppression")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":library"))
                api(compose.foundation)
                api(compose.material3)
                api(compose.runtime)
                api(compose.ui)
                api(libs.kotlin.stdlib)
            }
        }

        val skiaMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(skiaMain)
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

compose.desktop {
    application {
        mainClass = "MainKt"

        val pkg = "dev.zwander.composedialog.sample"

        nativeDistributions {
            includeAllModules = true

            windows {
                menu = true
//                console = true
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
            }

            macOS {
                bundleID = pkg
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                targetFormats(TargetFormat.Dmg)
            }

            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
                targetFormats(TargetFormat.Deb, TargetFormat.AppImage)
            }

            packageName = pkg
            packageVersion = "1.0.0"

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
        }
    }
}
