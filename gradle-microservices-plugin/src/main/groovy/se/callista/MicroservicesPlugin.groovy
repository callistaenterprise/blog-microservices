package se.callista

import org.gradle.api.Plugin
import org.gradle.api.Project

class MicroservicesPlugin implements Plugin<Project> {

    def springBootVersion = '1.2.2.RELEASE'
    
    @Override
    def void apply(Project project) {
        project.configurations {
            microservices
        }

        project.apply plugin: 'eclipse'
        project.apply plugin: 'idea'
        project.apply plugin: 'spring-boot'
        project.apply plugin: 'maven'

        project.sourceCompatibility = 1.8
        project.targetCompatibility = 1.8

        project.dependencies {
            compile("org.springframework.boot:spring-boot-starter-web:${springBootVersion}") {
                exclude module: 'spring-boot-starter-tomcat'
            }
            compile "org.springframework.boot:spring-boot-starter-undertow:${springBootVersion}"
            compile "org.springframework.boot:spring-boot-starter-actuator:${springBootVersion}"
            compile "org.springframework.cloud:spring-cloud-starter-hystrix:1.0.0.RELEASE"
            compile "org.springframework.cloud:spring-cloud-starter-eureka:1.0.0.RELEASE"

            testCompile "org.springframework.boot:spring-boot-starter-test:${springBootVersion}"
        }

        project.repositories {
            mavenLocal()
            mavenCentral()
            maven {
                url 'http://maven.springframework.org/release'
           }
        }
    }

}
