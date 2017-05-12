#!/usr/bin/env bash

set -e

cd util;                                              ./gradlew dependencies --configuration compile; cd -

cd microservices/core/product-service;                ./gradlew dependencies --configuration compile; cd -
cd microservices/core/recommendation-service;         ./gradlew dependencies --configuration compile; cd -
cd microservices/core/review-service;                 ./gradlew dependencies --configuration compile; cd -
cd microservices/composite/product-composite-service; ./gradlew dependencies --configuration compile; cd -

cd microservices/support/auth-server;                 ./gradlew dependencies --configuration compile; cd -
cd microservices/support/config-server;               ./gradlew dependencies --configuration compile; cd -
cd microservices/support/discovery-server;            ./gradlew dependencies --configuration compile; cd -
cd microservices/support/edge-server;                 ./gradlew dependencies --configuration compile; cd -
cd microservices/support/monitor-dashboard;           ./gradlew dependencies --configuration compile; cd -
cd microservices/support/turbine;                     ./gradlew dependencies --configuration compile; cd -

