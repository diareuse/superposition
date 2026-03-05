plugins {
    id("superposition.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
