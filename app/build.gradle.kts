plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.el_3af4a_2af4a"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.el_3af4a_2af4a"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("org.apache.commons:commons-math3:3.6.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.13")
}