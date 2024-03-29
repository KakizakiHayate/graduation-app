plugins {
    id("com.android.application")
}

android {
    namespace = "jp.ac.jec.cm0110.graduationapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.ac.jec.cm0110.graduationapp"
        minSdk = 24
        targetSdk = 33
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

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.3.0-beta01"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:$camerax_version")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}