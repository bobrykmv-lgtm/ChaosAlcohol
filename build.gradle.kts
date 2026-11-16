plugins {
    java
}

group = "com.chaossmp.chaosalcohol"
version = "1.0"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(23)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.release.set(23)
}
