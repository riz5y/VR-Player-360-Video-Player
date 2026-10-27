plugins {
    alias(libs.plugins.android.application)
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.videoplayer.hdvideos.vrplayer1"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.vrplayer.videoplayer360.vrplayer360"
        minSdk = 26
        targetSdk = 36
        versionCode = 11
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            resValue("string", "admob_ad_id",                       "ca-app-pub-7693688626230606~1538571939")
            resValue("string", "admob_native_on_boarding",          "ca-app-pub-7693688626230606/3268197408")
            resValue("string", "admob_native_on_boarding_full_screen", "ca-app-pub-7693688626230606/1368462804")
            resValue("string", "canShowNativeOnBoardingFullScreenafterfirstaddunit", "ca-app-pub-7693688626230606/4566395538")


            buildConfigField("String", "admob_banner_id",                  "\"ca-app-pub-7693688626230606/2681544475\"")
            buildConfigField("String", "admob_banner_id_Splash",           "\"ca-app-pub-7693688626230606/9797946736\"")
            buildConfigField("String", "admob_interstitial_id",            "\"ca-app-pub-7693688626230606/7342698427\"")
            buildConfigField("String", "admob_reels_interstitial_id",      "\"ca-app-pub-7693688626230606/4032436277\"")
            buildConfigField("String", "admob_interstitial_id_onBoarding", "\"ca-app-pub-7693688626230606/1679308200\"")
            buildConfigField("String", "admob_interstitial_splash_id",     "\"ca-app-pub-7693688626230606/6923061873\"")
            buildConfigField("String", "app_open_ad_id_admob",             "\"ca-app-pub-7693688626230606/3036767695\"")
        }

        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            resValue("string", "admob_ad_id",                          "ca-app-pub-1072519317331909~2642677144")
            resValue("string", "admob_native_on_boarding",             "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "admob_native_on_boarding_full_screen", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "canShowNativeOnBoardingFullScreenafterfirstaddunit", "ca-app-pub-3940256099942544/2247696110")

            buildConfigField("String", "admob_banner_id",                  "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "admob_banner_id_Splash",           "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "admob_reels_interstitial_id",      "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "admob_interstitial_id",            "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "admob_interstitial_id_onBoarding", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "admob_interstitial_splash_id",     "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "app_open_ad_id_admob",             "\"ca-app-pub-3940256099942544/9257395921\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
        resValues = true

    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.media3.datasource.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Dimension
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    // Media3 / ExoPlayer
//    implementation(libs.androidx.media3.exoplayer)
//    implementation(libs.androidx.media3.ui)
//    implementation(libs.androidx.media3.common)
//    implementation(libs.androidx.media3.extractor)
//    implementation(libs.androidx.media3.exoplayer.hls)
//    implementation(libs.androidx.media3.exoplayer.dash)
//    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
//    implementation(libs.androidx.media3.datasource.okhttp)
//    implementation("androidx.media3:media3-exoplayer-rtsp:1.3.1")
//    implementation("androidx.media3:media3-exoplayer-workmanager:1.3.1")

    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-session:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")


// For VP9 software decoding
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    implementation("com.google.code.gson:gson:2.10.1")

    implementation("io.coil-kt:coil:2.6.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //dot indciator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    //googlebilling
    implementation("com.android.billingclient:billing:8.0.0")
    //ads
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.lifecycle:lifecycle-process:2.6.1")

//firebase
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:23.1.1")
    implementation("com.google.firebase:firebase-database:20.1.0")
    // firebase configuration
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    // Firebase Remote Config (Kotlin)
    implementation("com.google.firebase:firebase-config-ktx")
    //crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx:19.4.3")

    // Shimmer effect for loading states
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    //shimmer effect
    implementation("io.github.usefulness:shimmer-android-core:1.0.0")
    //Lottie Animation
    implementation("com.airbnb.android:lottie:4.1.0")
    //upgrade
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    //meta
    implementation("com.google.ads.mediation:facebook:6.20.0.0")

//    implementation("com.github.bumptech.glide:glide:537.0.5")

// Glide
    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.glide.okhttp)
}

tasks.matching { it.name.startsWith("uploadCrashlytics") && it.name.endsWith("Debug") }.configureEach {
    enabled = false
}