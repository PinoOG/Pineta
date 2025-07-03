
plugins{
    id("pineta-conventions")
}

dependencies{
    compileOnlyApi(libs.lettuce.lib)
    compileOnlyApi(libs.apache.commons.pool3.lib)
    compileOnlyApi(libs.alibaba.fastjson2.lib)
    compileOnlyApi(libs.caffeine.cache.lib)
    compileOnlyApi(libs.jetbrains.annotations.lib)
}

version = project.version as String

tasks.named("publish") {
    dependsOn("clean", "build")
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "pinodev-repo"
            val base = "https://repo.pinodev.it"
            val releasesRepoUrl = "$base/releases/"
            val snapshotsRepoUrl = "$base/snapshots/"
            setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("pineta-helper-redis")
                url.set("https://repo.pinodev.it")
            }
        }
    }
}