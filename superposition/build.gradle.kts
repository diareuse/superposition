@file:Suppress("OPT_IN_USAGE")

plugins {
    id("superposition.kotlin.multiplatform")
}

kotlin {
    android {
        namespace = "superposition"
    }
    sourceSets {
        commonMain.dependencies {}
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
