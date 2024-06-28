plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-kapt")
    id ("kotlin-parcelize")

}

android {
    namespace = "com.sleepydev.bookvibe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sleepydev.bookvibe"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation(libs.pl.droidsonroids.gif.android.drawable)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    library ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")

    //    library untuk livedata
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha04")

    implementation ("androidx.datastore:datastore-preferences:1.0.0-alpha01")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("de.hdodenhof:circleimageview:3.1.0")




    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.room.ktx)


}