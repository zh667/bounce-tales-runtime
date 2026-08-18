plugins {
    `java-library`
}

dependencies {
    implementation(project(":game-logic"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
