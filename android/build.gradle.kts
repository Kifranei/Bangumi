import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.application")
    alias(libs.plugins.baselineprofile)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

baselineProfile {
    warnings {
        maxAgpVersion = false
    }
}

android {
    namespace = "com.kifranei.bgm.miuix"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.kifranei.bgm.miuix"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "false"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            val localProps = Properties()
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { localProps.load(it) }
            }
            fun secret(key: String, env: String, fallback: String) =
                localProps.getProperty(key) ?: System.getenv(env) ?: fallback

            storeFile = file(
                secret(
                    "RELEASE_STORE_FILE",
                    "BGM_RELEASE_STORE_FILE",
                    "$rootDir/android/keystore/why.keystore",
                )
            )
            storePassword = secret("RELEASE_STORE_PASSWORD", "BGM_RELEASE_STORE_PASSWORD", "why981229")
            keyAlias = secret("RELEASE_KEY_ALIAS", "BGM_RELEASE_KEY_ALIAS", "whykey")
            keyPassword = secret("RELEASE_KEY_PASSWORD", "BGM_RELEASE_KEY_PASSWORD", "why981229")
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(projects.composeApp)

    implementation(libs.bundles.compose.common)
    implementation(libs.bundles.kotlinx)

    implementation(projects.shared.ui)
    implementation(projects.shared.core)
    implementation(projects.shared.coreNative)
    implementation(projects.shared.coreResource)
    implementation(projects.shared.data)

    implementation(libs.androidx.activity.compose)
    implementation(libs.android.immersionbar)
    implementation(libs.androidx.profileinstaller)

    "baselineProfile"(project(":baselineprofile"))

    coreLibraryDesugaring(libs.desugar.jdk.libs)
    debugImplementation(libs.compose.ui.tooling)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

