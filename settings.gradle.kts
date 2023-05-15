pluginManagement { repositories {  mavenLocal(); mavenCentral(); google(); gradlePluginPortal()  }  }9
plugins {
    //id("com.soywiz.kproject.settings") version "0.0.1-SNAPSHOT"
    id("com.soywiz.kproject.settings") version "0.2.9"
}

rootProject.name = "${rootDir.name}-example"

kproject("./deps")
