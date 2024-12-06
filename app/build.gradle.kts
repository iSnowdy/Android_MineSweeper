import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
}

val emailString: String = gradleLocalProperties(rootDir, providers).getProperty("emailString", "default")
val emailPassword: String = gradleLocalProperties(rootDir, providers).getProperty("emailPassword", "default")

android {
    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }

    namespace = "com.example.minesweeper"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.minesweeper"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "emailString", "\"" + emailString + "\"")
        resValue("string", "emailPassword", "\"" + emailPassword + "\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "TEST_EMAIL_USERNAME", "\"${System.getenv("TEST_EMAIL_USERNAME")}\"")
            buildConfigField("String", "TEST_EMAIL_PASSWORD", "\"${System.getenv("TEST_EMAIL_PASSWORD")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation(libs.android.mail)
    implementation(libs.android.activation)
}