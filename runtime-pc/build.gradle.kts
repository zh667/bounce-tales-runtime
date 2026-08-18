import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

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

val committedHostJar = rootProject.layout.projectDirectory.file("bounce-tales-runtime.jar")

tasks.register<Jar>("desktopJar") {
    group = "distribution"
    description = "Fat JAR you can double-click. Does not include the original game."
    archiveFileName.set("bounce-tales-runtime.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
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

tasks.register("syncHostJar") {
    group = "distribution"
    description = "Copy the host JAR to the repository root so GitHub clones can double-click it."
    dependsOn("desktopJar")
    doLast {
        val built = tasks.named<Jar>("desktopJar").get().archiveFile.get().asFile
        Files.copy(built.toPath(), committedHostJar.asFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}

tasks.register("checkDesktopJar") {
    group = "verification"
    description = "Fails if the committed host JAR does not match a fresh desktopJar build."
    dependsOn("desktopJar")
    doLast {
        val committed = committedHostJar.asFile
        val built = tasks.named<Jar>("desktopJar").get().archiveFile.get().asFile
        if (!committed.isFile) {
            throw GradleException(
                "Missing committed bounce-tales-runtime.jar. Run :runtime-pc:syncHostJar and commit the root file."
            )
        }
        val committedHash = sha256(committed)
        val builtHash = sha256(built)
        if (committedHash != builtHash) {
            throw GradleException(
                "Committed bounce-tales-runtime.jar is stale. Run :runtime-pc:syncHostJar and commit the root file."
            )
        }
    }
}

tasks.named("assemble") {
    dependsOn("desktopJar")
}

tasks.named("check") {
    dependsOn("checkDesktopJar")
}

fun sha256(file: java.io.File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        while (true) {
            val n = input.read(buffer)
            if (n < 0) {
                break
            }
            digest.update(buffer, 0, n)
        }
    }
    return digest.digest().joinToString("") { byte: Byte -> "%02x".format(byte) }
}
