import java.util.Properties

// Carga del archivo local.properties para proteger endpoints/keys
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.example.clinickcheck"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clinickcheck"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Definición de variables globales para BuildConfig desde local.properties (con valores por defecto por seguridad)
        buildConfigField("String", "CLINICAL_TABLES_URL", localProperties.getProperty("CLINICAL_TABLES_URL", "\"https://clinicaltables.nlm.nih.gov/\""))
        buildConfigField("String", "RXNORM_URL", localProperties.getProperty("RXNORM_URL", "\"https://rxnav.nlm.nih.gov/\""))
        buildConfigField("String", "TRANSLATION_URL", localProperties.getProperty("TRANSLATION_URL", "\"https://api.mymemory.translated.net/\""))
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true // Activa la generación de BuildConfig
    }
    packaging {
        resources {
            excludes += "/META-INDEX/{AL2.0,LGPL2.1}"
        }
    }
}

// Configuración de KSP fuera del bloque android/defaultConfig
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Cifrado y SQLCipher
    implementation(libs.sqlcipher.android)
    implementation(libs.androidx.sqlite.ktx)
    implementation(libs.androidx.security.crypto)

    // Preferences DataStore para guardar la sesión
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Red: Retrofit + Gson + OkHttp (API NIH Clinical Tables - antecedentes medicos)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}