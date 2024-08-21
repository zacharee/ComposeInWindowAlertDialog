import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.zwander.compose.alertdialog"

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

val javaVersionEnum: JavaVersion by rootProject.extra

kotlin {
    jvmToolchain(javaVersionEnum.toString().toInt())

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-Xdont-warn-on-error-suppression")
                    jvmTarget = JvmTarget.fromTarget(javaVersionEnum.toString())
                }
            }
        }
    }

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget = JvmTarget.fromTarget(javaVersionEnum.toString())
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "InWindowAlertDialog"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    listOf(
        js(IR),
        wasmJs(),
    ).forEach {
        it.moduleName = "InWindowAlertDialog"
        it.browser()
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
                api(compose.foundation)
                api(compose.material3)
                api(compose.runtime)
                api(compose.ui)
                api(libs.kotlin)
            }
        }

        val skiaMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(skiaMain)
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }

        val iosMain by creating {
            dependsOn(skiaMain)
        }

        val iosX64Main by getting {
            dependsOn(iosMain)
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        val macosMain by creating {
            dependsOn(skiaMain)
        }

        val macosArm64Main by getting {
            dependsOn(macosMain)
        }

        val macosX64Main by getting {
            dependsOn(macosMain)
        }

        val jsMain by getting {
            dependsOn(skiaMain)
        }

        val wasmJsMain by getting {
            dependsOn(skiaMain)
        }
    }
}

android {
    this.compileSdk = 34

    defaultConfig {
        this.minSdk = 21
    }

    namespace = "dev.zwander.compose.alertdialog"

    compileOptions {
        val javaVersionEnum: JavaVersion by rootProject.extra
        sourceCompatibility = javaVersionEnum
        targetCompatibility = javaVersionEnum
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        aidl = true
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    dependencies {
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
