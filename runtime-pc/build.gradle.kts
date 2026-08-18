plugins {
    application
}

dependencies {
    implementation(project(":game-logic"))
    implementation(project(":j2me-api"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("io.github.zh667.bouncetales.pc.DesktopRuntime")
}

tasks.named<JavaExec>("run") {
    systemProperty("bounce.debug.dump", System.getProperty("bounce.debug.dump", "false"))
    systemProperty("bounce.debug.host", System.getProperty("bounce.debug.host", "true"))
}
