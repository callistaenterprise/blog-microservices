#!/usr/bin/env bash

cd util;                                              ./gradlew clean publishToMavenLocal; cd -

cd microservices/core/product-service;                ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/core/recommendation-service;         ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/core/review-service;                 ./gradlew clean publishToMavenLocal build distDocker; cd -
cd microservices/composite/product-composite-service; ./gradlew clean build distDocker; cd -
cd microservices/api/product-api-service;             ./gradlew clean build distDocker; cd -

cd microservices/support/auth-server;                 ./gradlew clean build distDocker; cd -
cd microservices/support/discovery-server;            ./gradlew clean build distDocker; cd -
cd microservices/support/edge-server;                 ./gradlew clean build distDocker; cd -
cd microservices/support/monitor-dashboard;           ./gradlew clean build distDocker; cd -
cd microservices/support/turbine;                     ./gradlew clean build distDocker; cd -

find . -name *SNAPSHOT.jar -exec du -h {} \;