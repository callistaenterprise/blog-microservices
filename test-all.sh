#!/usr/bin/env bash

set -e

function testUrl() {
    url=$@
    echo -n "Is Up: $url ? "
    if curl $url -s -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo "Fail"
          return 1
    fi;
}

function waitForService() {

    url=$@
    n=0
    until testUrl $url
    do
        ((n++))
        if [[ $n == 20 ]]
        then
            echo "Give up"
            exit 1
        else
            echo -n "Call failed, wait before retry... "
            sleep 6
            echo "Try again, $n"
        fi
    done

}

function waitForServices() {
    host=docker.me
    waitForService $host:8761
    waitForService $host:8761/eureka/apps/configserver
    waitForService $host:8761/eureka/apps/edge-server
    waitForService $host:8761/eureka/apps/product-composite
    waitForService $host:8761/eureka/apps/product-service
    waitForService $host:8761/eureka/apps/review-service
    waitForService $host:8761/eureka/apps/recommendation-service
    waitForService $host:8761/eureka/apps/productapi
}

echo "Restarting the test environment..."
docker-compose stop
docker-compose up -d

waitForServices

echo -n "Tries to get an Access Token... "
TOKEN=`curl -ks https://acme:acmesecret@docker.me:9999/uaa/oauth/token   -d grant_type=password  -d client_id=acme  -d scope=webshop  -d username=user  -d password=password | jq -r .access_token`
echo "Got: $TOKEN"

echo -n "Tries to call the API with the Access Token... "
curl 'https://docker.me/api/product/123'   -H  "Authorization: Bearer $TOKEN" -ks | jq .

echo "We are done stopping the test environment..."
docker-compose stop
docker-compose rm -f




