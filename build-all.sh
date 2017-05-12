#!/usr/bin/env bash

function note() {
    local GREEN NC
    GREEN='\033[0;32m'
    NC='\033[0m' # No Color
    printf "\n${GREEN}$@  ${NC}\n" >&2
}

set -e

. ./setup-env.sh

cd util;                                              note "Building util...";            ./gradlew clean build publishToMavenLocal; cd -

cd microservices/core/product-service;                note "Building product...";         ./gradlew clean build; cd -
cd microservices/core/recommendation-service;         note "Building recommendation...";  ./gradlew clean build; cd -
cd microservices/core/review-service;                 note "Building review...";          ./gradlew clean build; cd -
cd microservices/composite/product-composite-service; note "Building composite...";       ./gradlew clean build; cd -

cd microservices/support/auth-server;                 note "Building auth...";            ./gradlew clean build; cd -
cd microservices/support/config-server;               note "Building config...";          ./gradlew clean build; cd -
cd microservices/support/discovery-server;            note "Building discovery...";       ./gradlew clean build; cd -
cd microservices/support/edge-server;                 note "Building edge...";            ./gradlew clean build; cd -
cd microservices/support/monitor-dashboard;           note "Building monitor...";         ./gradlew clean build; cd -
cd microservices/support/turbine;                     note "Building turbine...";         ./gradlew clean build; cd -

find . -name *SNAPSHOT.jar -exec du -h {} \;

docker-compose build
