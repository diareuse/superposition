plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.36.0")
}
