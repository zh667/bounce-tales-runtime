plugins {
    application
}

dependencies {
    implementation(project(":game-logic"))
    implementation(project(":j2me-api"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("io.github.zh667.bouncetales.pc.DesktopRuntime")
}
