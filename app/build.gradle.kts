plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.health"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.health"
        minSdk = 28
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // 添加 MPAndroidChart
    implementation(libs.mpandroidchart)

    // 数据库
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // 高德地图
    implementation("com.amap.api:3dmap:latest.integration")
    implementation(files("libs\\AMap_Location_V6.4.9_20241226.jar"))
    implementation("com.amap.api:location:latest.integration")

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.databinding:databinding-runtime:7.1.2")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.core:core:1.7.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    // 图片处理
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // 权限管理
    implementation("pub.devrel:easypermissions:3.0.0")

    // 图片裁剪
    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")

    // 日期选择器
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")

    // 蓝牙支持
    implementation("no.nordicsemi.android:ble:2.3.1")

    // 日期格式化
    implementation("org.ocpsoft.prettytime:prettytime:4.0.6.Final")

    // 短信验证码
    //implementation("com.github.jiusetian:EasyCountDownTextureView:2.1.0")
    implementation("com.github.mini-o:health:Tag")

    // 设备信息
    //implementation("com.github.jaredrummler:android-device-names:2.0.0")

    // IP地址获取
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")

    // 弹窗
    implementation("com.github.f0ris.sweetalert:library:1.6.2")

    // 加密
    //implementation("com.github.iamMehedi:Secure-Pref-Manager:6e20ba3")

    // 生命周期
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.0")


    // 网络状态
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")
    implementation("androidx.navigation:navigation-fragment:2.4.0")
    implementation("androidx.navigation:navigation-ui:2.4.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}