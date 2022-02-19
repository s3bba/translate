plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "org.op65n"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.tomlj:tomlj:1.0.0")

    compileOnly("io.github.waterfallmc:waterfall-api:1.18-R0.1-SNAPSHOT")
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:all")
        options.encoding = "UTF-8"
    }

    withType<Jar> {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version as String)
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        archiveVersion.set(project.version as String)

        val destination = "org.op65n.translate.libs"

        relocate("org.jetbrains", "$destination.jetbrains")
        relocate("com.google.code.gson", "$destination.gson")
        relocate("com.squareup.okhttp", "$destination.okhttp")
        relocate("org.tomlj", "$destination.tomlj")
    }

    jar.get().finalizedBy(shadowJar)
}
