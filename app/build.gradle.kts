import com.android.build.api.dsl.ApplicationProductFlavor
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}


fun configureCustomFlavor(
    flavors: NamedDomainObjectContainer<ApplicationProductFlavor>,
    name: String,
    appId: String,
    icon: String,
    iconMobile: String,
    locatorUrl: String,
    baseUrl: String,
    idmUrl: String,
    metricsUrl: String,
    searchUrl: String,
    passwordUrl: String,
    dynamicUrls: Boolean
) {
    flavors.create(name) {
        dimension = "app"
        applicationId = appId
        manifestPlaceholders["appIcon"] = icon
        manifestPlaceholders["appIconMobile"] = iconMobile

        buildConfigField("String", "LOCATOR_URL", "\"$locatorUrl\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "IDM_URL", "\"$idmUrl\"")
        buildConfigField("String", "METRICS_URL", "\"$metricsUrl\"")
        buildConfigField("String", "SEARCH_URL", "\"$searchUrl\"")
        buildConfigField("String", "PASSWORD_REC_URL", "\"$passwordUrl\"")
        buildConfigField("boolean", "DYNAMIC_URLS", "$dynamicUrls")
    }
}

fun configureFlavorWithMasmediaUrls(
    flavors: NamedDomainObjectContainer<ApplicationProductFlavor>,
    name: String,
    appId: String,
    icon: String,
    iconMobile: String
) {
    configureCustomFlavor(
        flavors, name, appId, icon, iconMobile,
        "https://locator.service.openstream.es/",
        "https://dyncont.masmediatv.es/",
        "https://idm.masmediatv.es/",
        "https://metrics.service.openstream.es/",
        "https://indexsrv-masmediatv.service.openstream.es/",
        "https://gestionclientes.masmediatv.es/masmediatv_mngr/",
        true
    )
}

android {
    namespace = "com.mamm.mammapps"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mamm.mammapps"
        minSdk = 24
        targetSdk = 35
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"  // Sí, está deprecated pero funciona
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "app"
    productFlavors {

        configureFlavorWithMasmediaUrls(
            this,
            "masmedia",
            "masmedia.openstream.com",
            "@drawable/banner_masmedia",
            "@mipmap/ic_launcher_masmedia"
        )

        configureCustomFlavor(
            this,
            "eligetv",
            "app.openstream.com",
            "@drawable/banner_openstream",
            "@mipmap/ic_launcher_openstream",
            "http://locator.service.openstream.es/",
            "https://dyncont.openstream.es/",
            "https://idm.openstream.es/",
            "https://metrics.service.openstream.es/",
            "https://indexsrv-openstream.service.openstream.es/",
            "https://gestionclientes.openstream.es/openstream_mngr/",
            false
        )

    }
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
    implementation(libs.bundles.retrofit)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.security.crypto)
    implementation(libs.coil.compose)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}

