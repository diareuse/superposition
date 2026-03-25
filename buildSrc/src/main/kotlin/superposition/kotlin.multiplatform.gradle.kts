@file:OptIn(ExperimentalWasmDsl::class)

package superposition

import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.vanniktech.maven.publish")
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        ktlint()
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi()

    android {
        compileSdk {
            version = release(36)
        }
    }

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    tvosArm64()
    tvosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    linuxX64()
    linuxArm64()

    macosArm64()

    js {
        browser()
        nodejs()
    }
    wasmJs {
        browser()
        nodejs()
        d8()
    }
    wasmWasi {
        nodejs()
    }

    mingwX64()

    compilerOptions {
        allWarningsAsErrors = true
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}
