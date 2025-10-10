import com.android.build.api.dsl.ApplicationProductFlavor

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    kotlin("plugin.parcelize")
}


fun configureCustomFlavor(
    flavors: NamedDomainObjectContainer<ApplicationProductFlavor>,
    name: String,
    appId: String,
    icon: String,
    iconMobile: String,
    iconRound: String,
    banner: String,
    locatorUrl: String,
    baseUrl: String,
    idmUrl: String,
    metricsUrl: String,
    searchUrl: String,
    passwordUrl: String,
    dynamicUrls: Boolean,
    operatorNameDRM: String
) {
    flavors.create(name) {
        dimension = "app"
        applicationId = appId
        manifestPlaceholders["appIcon"] = icon
        manifestPlaceholders["appIconMobile"] = iconMobile
        manifestPlaceholders["appIconRound"] = iconRound
        manifestPlaceholders["banner"] = banner

        buildConfigField("String", "LOCATOR_URL", "\"$locatorUrl\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "IDM_URL", "\"$idmUrl\"")
        buildConfigField("String", "METRICS_URL", "\"$metricsUrl\"")
        buildConfigField("String", "SEARCH_URL", "\"$searchUrl\"")
        buildConfigField("String", "PASSWORD_REC_URL", "\"$passwordUrl\"")
        buildConfigField("boolean", "DYNAMIC_URLS", "$dynamicUrls")
        buildConfigField("String", "OPERATORNAME_DRM", "\"$operatorNameDRM\"")
    }
}

fun configureFlavorWithMasmediaUrls(
    flavors: NamedDomainObjectContainer<ApplicationProductFlavor>,
    name: String,
    appId: String,
    icon: String,
    iconMobile: String,
    iconRound: String,
    banner: String
) {
    configureCustomFlavor(
        flavors = flavors,
        name = name,
        appId = appId,
        icon = icon,
        iconMobile = iconMobile,
        iconRound = iconRound,
        banner = banner,
        "https://locator.service.openstream.es/",
        "https://dyncont.masmediatv.es/",
        "https://idm.masmediatv.es/",
        "https://metrics.service.openstream.es/",
        "https://indexsrv-masmediatv.service.openstream.es/",
        "https://gestionclientes.masmediatv.es/masmediatv_mngr/",
        true,
        "masmediatv"
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
            "@mipmap/ic_launcher_masmedia",
            "@mipmap/ic_launcher_masmedia",
            "@mipmap/ic_launcher_masmedia_round",
            "@drawable/banner_masmedia"
        )

        configureCustomFlavor(
            this,
            "eligetv",
            "app.openstream.com",
            "@drawable/banner_openstream",
            "@mipmap/ic_launcher_openstream",
            "@mipmap/ic_launcher_openstream_round",
            "@drawable/banner_openstream",
            "http://locator.service.openstream.es/",
            "https://dyncont.openstream.es/",
            "https://idm.openstream.es/",
            "https://metrics.service.openstream.es/",
            "https://indexsrv-openstream.service.openstream.es/",
            "https://gestionclientes.openstream.es/openstream_mngr/",
            false,
            "openstream"
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
    implementation(libs.bundles.android.tv)
    implementation(libs.bundles.video.player)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //--------TOKENS------------
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly("io.jsonwebtoken:jjwt-orgjson:0.12.6") {
        exclude(group = "org.json", module = "json") // provided by Android natively
    }
    implementation(libs.bouncycastle.bcprov)

    //--------PREVIEW SEEK BAR------------
    implementation(libs.bundles.previewseekbar)
    implementation(libs.androidxLeanback)
    implementation(libs.androidxAnnotation)
    implementation(libs.checkerFramework)

    //--------PLAYER----------------------
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //--------EPG----------------------
    implementation(project(":jctvguide"))

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

