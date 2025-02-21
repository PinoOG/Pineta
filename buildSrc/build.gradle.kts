plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("net.linguica.maven-settings:net.linguica.maven-settings.gradle.plugin:0.5")
}