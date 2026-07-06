plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.github.rwsbillyang.iReadPDF"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "com.github.rwsbillyang.iReadPDF"
        minSdk = 26
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Disables PNG crunching for the "release" build type.
            isCrunchPngs = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    // 注意：在 AGP 9.2.1 中，传统的 composeOptions 已被废弃。
    // Compose 编译器版本现在通常通过 Kotlin 官方的 Compose 编译器 Gradle 插件在项目根目录统一管理。
    // 如果你的项目仍在使用旧方案，可取消注释，但在 AGP 9.x 中推荐移除并使用新插件。
    // composeOptions {
    //     kotlinCompilerExtensionVersion = libs.versions.composeVersion.get()
    // }

    // packagingOptions 在新版 AGP 中更名为 packaging
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
kotlin {
    jvmToolchain(21)
    compilerOptions {
        // 比如开启一些实验性特性
        // freeCompilerArgs.add("-Xcontext-receivers")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
    //https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh-cn
    room {
        schemaDirectory("${project.projectDir}/schemas")
    }
}
dependencies {
    //implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.androidx.compose)

    implementation(libs.androidx.compose.material3.window.sizeclass)

    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    // implementation(libs.ktor.serialization.kotlinx.json)
    // implementation(libs.ktor.client.cio.jvm)
    // implementation(libs.ktor.client.content.negotiation)
    // implementation(libs.slf4j.simple)

    //implementation(libs.androidx.compose.material)
    //implementation(libs.androidx.compose.material.icons.extended) // https://github.com/adrielcafe/bonsai/issues/4

    implementation(libs.composePrefs) // https://github.com/rwsbillyang/ComposePrefs3

    // implementation("androidx.documentfile:documentfile:1.1.0")

    implementation(libs.composerouter)
    implementation(libs.composeui)

    // 使用 AndroidPdfViewer 库 (免费开源) depend very old lib android-support-v4:1.0.0
    // implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")

    // https://github.com/afreakyelf/Pdf-Viewer 全家桶，port the core pdf view code
    // implementation("io.github.afreakyelf:Pdf-Viewer:2.3.7")

    // implementation(project(":lib"))
}