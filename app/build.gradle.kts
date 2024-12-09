import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.hayakai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hayakai"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").reader())

    buildTypes {
        debug {
            buildConfigField("String", "API_URL", properties.getProperty("API_URL"))
            buildConfigField("String", "GEMINI_API_KEY", properties.getProperty("GEMINI_API_KEY"))
            buildConfigField("String", "GEMINI_API_URL", properties.getProperty("GEMINI_API_URL"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.activity)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // data store
    implementation(libs.androidx.datastore.preferences)

    // maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // glide
    implementation(libs.glide)
    ksp(libs.compiler)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)

    // viewpager2
    implementation(libs.androidx.viewpager2)


    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.ktx)

    // swipe refresh layout
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.tensorflow.lite.task.audio)

    // paging
    implementation(libs.androidx.paging.runtime.ktx)
}