plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("F:\\Android\\key3.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
        create("release") {
            storeFile = file("F:\\Android\\key3.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
    }
    namespace = "com.smartwallet"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.smartwallet"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionNameSuffix = "budget_"
        signingConfig = signingConfigs.getByName("debug")
        proguardFiles("proguard-rules.pro")
        applicationIdSuffix = "smartwallet"
    }

    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}
dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)


    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
    implementation("androidx.core:core-ktx:1.18.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.10.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.10.0")
    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")


    // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") {
        exclude(group = "com.android.support")
    }
    // Fragment
    implementation("androidx.fragment:fragment:1.8.9")
}