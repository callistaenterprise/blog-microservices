FROM openjdk:8u111-jre-alpine
MAINTAINER Magnus Larsson <magnus.larsson.ml@gmail.com>

EXPOSE 8761

ADD ./build/libs/*.jar app.jar

# Regarding settings of java.security.egd, see http://wiki.apache.org/tomcat/HowTo/FasterStartUp#Entropy_Source
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
