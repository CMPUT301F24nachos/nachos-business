plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.nachosbusiness"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        // ...
    }

    defaultConfig {
        applicationId = "com.example.nachosbusiness"
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

    testOptions {
        unitTests {
            all {
                isReturnDefaultValues = true
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform() // Correct placement for JUnit 5 support
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.fragment)
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation(libs.rules)

    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation(libs.ext.junit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}