plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ensinativapackage.ensinativa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ensinativapackage.ensinativa"
        minSdk = 30
        targetSdk = 34
        versionCode = 11
        versionName = "11.0"

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
    viewBinding {
        enable = true
    }
    buildFeatures {
        viewBinding = true
    }

}
dependencies {
    implementation("com.google.android.play:integrity:1.3.0")
    implementation("com.google.firebase:firebase-appcheck")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("commons-validator:commons-validator:1.8.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.1")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.appcompat:appcompat:1.6.0-alpha04")
}
apply(plugin = ("com.google.gms.google-services"))

