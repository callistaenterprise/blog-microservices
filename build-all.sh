#!/usr/bin/env bash

set -e

cd util;                                              ./gradlew clean build publishToMavenLocal; cd -

cd microservices/core/product-service;                ./gradlew clean build buildDockerImage; cd -
cd microservices/core/recommendation-service;         ./gradlew clean build buildDockerImage; cd -
cd microservices/core/review-service;                 ./gradlew clean build buildDockerImage; cd -
cd microservices/composite/product-composite-service; ./gradlew clean build buildDockerImage; cd -
cd microservices/api/product-api-service;             ./gradlew clean build buildDockerImage; cd -

cd microservices/support/auth-server;                 ./gradlew clean build buildDockerImage; cd -
cd microservices/support/config-server;               ./gradlew clean build buildDockerImage; cd -
cd microservices/support/discovery-server;            ./gradlew clean build buildDockerImage; cd -
cd microservices/support/edge-server;                 ./gradlew clean build buildDockerImage; cd -
cd microservices/support/monitor-dashboard;           ./gradlew clean build buildDockerImage; cd -
cd microservices/support/turbine;                     ./gradlew clean build buildDockerImage; cd -

find . -name *SNAPSHOT.jar -exec du -h {} \;