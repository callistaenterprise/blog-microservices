#!/usr/bin/env bash

function note() {
    local GREEN NC
    GREEN='\033[0;32m'
    NC='\033[0m' # No Color
    printf "\n${GREEN}$@  ${NC}\n" >&2
}

set -e

cd util;                                              note "Building util..."; ./gradlew clean build publishToMavenLocal; cd -

cd microservices/core/product-service;                note "Building prod..."; ./gradlew clean build; cd -
cd microservices/core/recommendation-service;         note "Building rec...";  ./gradlew clean build; cd -
cd microservices/core/review-service;                 note "Building rev...";  ./gradlew clean build; cd -
cd microservices/composite/product-composite-service; note "Building comp..."; ./gradlew clean build; cd -

cd microservices/support/auth-server;                 note "Building auth..."; ./gradlew clean build; cd -
cd microservices/support/config-server;               note "Building conf..."; ./gradlew clean build; cd -
cd microservices/support/discovery-server;            note "Building disc..."; ./gradlew clean build; cd -
cd microservices/support/edge-server;                 note "Building edge..."; ./gradlew clean build; cd -
cd microservices/support/monitor-dashboard;           note "Building mon...";  ./gradlew clean build; cd -
cd microservices/support/turbine;                     note "Building turb..."; ./gradlew clean build; cd -

find . -name *SNAPSHOT.jar -exec du -h {} \;

docker-compose build