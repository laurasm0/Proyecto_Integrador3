plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

// change to your nickname eg dev.pepitoperez
group = "dev.laurafelipe"
version = "1.0-SNAPSHOT"

application {
    // the group must match here
    mainClass.set("dev.laurafelipe.proyectointegrador3")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "21"
    modules("javafx.controls")
}

tasks.test {
    useJUnitPlatform()
}
