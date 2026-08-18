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

tasks.register<Jar>("desktopJar") {
    group = "distribution"
    description = "Fat JAR you can double-click. Does not include the original game."
    archiveFileName.set("bounce-tales-runtime.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "io.github.zh667.bouncetales.pc.DesktopRuntime"
        attributes["Implementation-Title"] = "bounce-tales-runtime"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.exists() && it.name.endsWith(".jar") }.map { zipTree(it) }
    })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/INDEX.LIST", "META-INF/MANIFEST.MF")
}

tasks.named("assemble") {
    dependsOn("desktopJar")
}
