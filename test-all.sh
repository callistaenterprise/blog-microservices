#!/usr/bin/env bash

set -e

function testUrl() {
    url=$@
    if curl $url -s -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "fail"
          return 1
    fi;
}

function waitForService() {

    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        ((n++))
        if [[ $n == 20 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done

}

function waitForServices() {
    host=docker.me
    waitForService $host:8761
    waitForService $host:8761/eureka/apps/configserver
    waitForService $host:8761/eureka/apps/edge-server
    waitForService $host:8761/eureka/apps/product-service
    waitForService $host:8761/eureka/apps/review-service
    waitForService $host:8761/eureka/apps/recommendation-service
    waitForService $host:8761/eureka/apps/composite-service
}

if [[ $@ != *"skip-start"* ]]
then
    echo "Restarting the test environment..."
    docker-compose stop
    docker-compose up -d
fi

waitForServices

echo -n "Tries to get an Access Token... "
TOKEN=`curl -ks https://acme:acmesecret@docker.me:9999/uaa/oauth/token   -d grant_type=password  -d client_id=acme  -d scope=webshop  -d username=user  -d password=password | jq -r .access_token`
echo "Got: $TOKEN"

echo -n "Tries to call the API with the Access Token... "
curl 'https://docker.me/api/product/123'   -H  "Authorization: Bearer $TOKEN" -ks | jq .

if [[ $@ != *"skip-stop"* ]]
then
    echo "We are done, stopping the test environment..."
    docker-compose stop
#    docker-compose rm -f
fi




