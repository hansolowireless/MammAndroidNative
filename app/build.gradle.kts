import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationProductFlavor
import java.io.FileInputStream
import java.util.Properties

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
    staticServiceUrl: String,
    dynamicUrls: Boolean,
    operatorNameDRM: String,
    signingConfig: ApkSigningConfig? = null
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
        buildConfigField("String", "STATIC_SERVICE_URL", "\"$staticServiceUrl\"")
        buildConfigField("boolean", "DYNAMIC_URLS", "$dynamicUrls")
        buildConfigField("String", "OPERATORNAME_DRM", "\"$operatorNameDRM\"")

        if (signingConfig != null) {
            this.signingConfig = signingConfig
        }
    }
}

fun configureFlavorWithMasmediaUrls(
    flavors: NamedDomainObjectContainer<ApplicationProductFlavor>,
    name: String,
    appId: String,
    icon: String,
    iconMobile: String,
    iconRound: String,
    banner: String,
    // Valores de Masmedia por defecto. Se pueden cambiar a demanda si un operador necesita una modificación
    locatorUrl: String = "https://locator.service.openstream.es/",
    baseUrl: String = "https://dyncont.masmediatv.es/",
    idmUrl: String = "https://idm.masmediatv.es/",
    metricsUrl: String = "https://metrics.service.openstream.es/",
    searchUrl: String = "https://indexsrv-masmediatv.service.openstream.es/",
    passwordUrl: String = "https://gestionclientes.masmediatv.es/masmediatv_mngr/",
    staticServiceUrl: String = "https://static.masmediatv.com/",
    signingConfig: ApkSigningConfig? = null
) {
    configureCustomFlavor(
        flavors = flavors,
        name = name,
        appId = appId,
        icon = icon,
        iconMobile = iconMobile,
        iconRound = iconRound,
        banner = banner,
        locatorUrl = locatorUrl,
        baseUrl = baseUrl,
        idmUrl = idmUrl,
        metricsUrl = metricsUrl,
        searchUrl = searchUrl,
        passwordUrl = passwordUrl,
        staticServiceUrl = staticServiceUrl,
        dynamicUrls = true,
        operatorNameDRM = "masmediatv",
        signingConfig = signingConfig
    )
}

android {
    namespace = "com.mamm.mammapps"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mamm.mammapps"
        minSdk = 21
        targetSdk = 35
        versionCode = 565
        versionName = "4.8.036"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    signingConfigs {
        create("keystoreMamm") {
            val keystoreProperties = Properties() // Línea corregida
            val keystorePropertiesFile = rootProject.file("key.properties")

            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }

            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storePassword = keystoreProperties.getProperty("storePassword")

            val storeFilePath = keystoreProperties.getProperty("storeFile")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
            }

            enableV1Signing = true
            enableV2Signing = true
        }

        create("keystoreDiego") {
            val keystoreProperties = Properties()
            val keystorePropertiesFile = rootProject.file("key_keystorediego.properties")

            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }

            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storePassword = keystoreProperties.getProperty("storePassword")

            val storeFilePath = keystoreProperties.getProperty("storeFile")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
            }

            enableV1Signing = true
            enableV2Signing = true
        }
    }



    buildTypes {
        release {
            // Enables code-related app optimization.
            isMinifyEnabled = true
            // Enables resource shrinking.
            isShrinkResources = true
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
            flavors = this,
            name = "masmedia",
            appId = "masmedia.openstream.com",
            icon = "@mipmap/ic_launcher_masmedia",
            iconMobile = "@mipmap/ic_launcher_masmedia",
            iconRound = "@mipmap/ic_launcher_masmedia_round",
            banner = "@drawable/banner_masmedia",
            signingConfig = signingConfigs.getByName("keystoreDiego")
        )

        configureFlavorWithMasmediaUrls(
            flavors = this,
            name = "fibrazo",
            appId = "app.fibrazo.com",
            icon = "@mipmap/ic_launcher_fibrazo",
            iconMobile = "@mipmap/ic_launcher_fibrazo",
            iconRound = "@mipmap/ic_launcher_fibrazo_round",
            banner = "@drawable/banner_fibrazo",
            //La función de PIN requiere que apunte a Colombia por defecto
            idmUrl = "https://idm.co.masmediatv.es/",
            signingConfig = signingConfigs.getByName("keystoreMamm")
        )

        configureCustomFlavor(
            flavors = this,
            name = "eligetv",
            appId = "app.openstream.com",
            icon = "@mipmap/ic_launcher_eligetv",
            iconMobile = "@mipmap/ic_launcher_eligetv",
            iconRound = "@mipmap/ic_launcher_eligetv_round",
            banner = "@drawable/banner_eligetv",
            locatorUrl = "https://idm.openstream.es/",
            baseUrl = "https://dyncont.openstream.es/",
            idmUrl = "https://idm.openstream.es/",
            metricsUrl = "https://metrics.service.openstream.es/",
            searchUrl = "https://indexsrv-openstream.service.openstream.es/",
            passwordUrl = "https://gestionclientes.openstream.es/openstream_mngr/",
            staticServiceUrl = "https://static.masmediatv.com/",
            dynamicUrls = false,
            operatorNameDRM = "openstream",
            signingConfig = signingConfigs.getByName("keystoreDiego")
        )

    }

}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val flavorName = variant.flavorName ?: "app"
        val versionName = variant.outputs.first().versionName.get() ?: "0.0"
        val versionCode = variant.outputs.first().versionCode.get() ?: 0

        variant.outputs.forEach { output ->
            if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                output.outputFileName.set("${flavorName}-release-${versionName}-${versionCode}.apk")
            }
        }
    }
}

dependencies {

    //Splash screen
    implementation(libs.androidx.core.splashscreen)

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

    //--------EPG--------------
    implementation(libs.programguide)

    //--------CHROMECAST--------------
    implementation(libs.play.services.cast.framework)

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

