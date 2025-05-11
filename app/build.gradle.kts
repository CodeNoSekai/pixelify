plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "iiec.ditzdev.pixelify"
    compileSdk = 35

    defaultConfig {
        applicationId = "iiec.ditzdev.pixelify"
        minSdk = 23
        targetSdk = 35
        versionCode = 998
        versionName = "1.0.2-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
       release {
          storeFile = file("release-keystore.jks")
          storePassword = System.getenv("KEYSTORE_PASSWORD")
          keyAlias = System.getenv("KEY_ALIAS")
          keyPassword = System.getenv("KEY_PASSWORD")
      }
   }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.release
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
       checkReleaseBuilds = false
       abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.daimajia.androidanimations:library:2.4@aar")
    implementation(libs.glide)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
