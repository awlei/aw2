plugins {
    id("com.android.application") version "8.1.2"
    id("com.jaredsburrows.license")
}

android {
    namespace = "com.v2ray.ang"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.v2ray.ang"
        minSdk = 24
        targetSdk = 36
        versionCode = 706
        versionName = "2.0.6"
        multiDexEnabled = true

        val abiFilterList = (properties["ABI_FILTERS"] as? String)?.split(';')
        splits {
            abi {
                isEnable = true
                reset()
                if (abiFilterList != null && abiFilterList.isNotEmpty()) {
                    include(*abiFilterList.toTypedArray())
                } else {
                    include(
                        "arm64-v8a",
                        "armeabi-v7a",
                        "x86_64",
                        "x86"
                    )
                }
                isUniversalApk = abiFilterList.isNullOrEmpty()
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("distribution")
    productFlavors {
        create("fdroid") {
            dimension = "distribution"
            applicationIdSuffix = ".fdroid"
            buildConfigField("String", "DISTRIBUTION", "\"F-Droid\"")
        }
        create("playstore") {
            dimension = "distribution"
            buildConfigField("String", "DISTRIBUTION", "\"Play Store\"")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Note: version code override for ABI splits is disabled due to AGP 9.0 API changes
    // This may cause version code conflicts in multi-ABI builds

    sourceSets {
        getByName("main") {
            jniLibs {
                srcDirs("libs")
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable.add("MissingTranslation")
        disable.add("ExtraTranslation")
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

}

dependencies {
    // Core Libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    // AndroidX Core Libraries
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity:1.12.2")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // UI Libraries
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("com.blacksquircle.ui:editorkit:2.9.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Data and Storage Libraries
    implementation("com.tencent:mmkv-static:1.3.16")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")

    // Reactive and Utility Libraries
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // Language and Processing Libraries
    implementation("com.blacksquircle.ui:language-base:2.9.0")
    implementation("com.blacksquircle.ui:language-json:2.9.0")

    // Intent and Utility Libraries
    implementation("com.github.T8RIN.QuickieExtended:quickie-foss:1.14.0")
    implementation("com.google.zxing:core:3.5.4")

    // AndroidX Lifecycle and Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")

    // Background Task Libraries
    implementation("androidx.work:work-runtime-ktx:2.11.0")
    implementation("androidx.work:work-multiprocess:2.11.0")

    // Multidex Support
    implementation("androidx.multidex:multidex:2.0.1")

    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
}
