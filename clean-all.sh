#!/usr/bin/env bash

cd util;                                              ./gradlew clean; cd -

cd microservices/core/product-service;                ./gradlew clean; cd -
cd microservices/core/recommendation-service;         ./gradlew clean; cd -
cd microservices/core/review-service;                 ./gradlew clean; cd -
cd microservices/composite/product-composite-service; ./gradlew clean; cd -

cd microservices/support/auth-server;                 ./gradlew clean; cd -
cd microservices/support/config-server;               ./gradlew clean; cd -
cd microservices/support/discovery-server;            ./gradlew clean; cd -
cd microservices/support/edge-server;                 ./gradlew clean; cd -
cd microservices/support/monitor-dashboard;           ./gradlew clean; cd -
cd microservices/support/turbine;                     ./gradlew clean; cd -
cd microservices/support/zipkin-server;               ./gradlew clean; cd -
