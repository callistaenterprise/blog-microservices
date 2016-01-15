#!/usr/bin/env bash
#
# Sample use of overriding parameters:
#
# $ host=localhost ./test-all.sh
#
: ${host=docker.me}

# set -e

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
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
    waitForService $host:8761
    waitForService $host:8761/eureka/apps/config-server
    waitForService $host:8761/eureka/apps/edge-server
    waitForService $host:8761/eureka/apps/product-service
    waitForService $host:8761/eureka/apps/review-service
    waitForService $host:8761/eureka/apps/recommendation-service
    waitForService $host:8761/eureka/apps/composite-service
}


function testAPI() {
    url=$@
#    url="https://$host/api/product/123 -H \"Authorization: Bearer $TOKEN\""
#    echo "Test API: $url"
#    if curl "$url" -ki -f
#    if curl $url -ks -f -o /dev/null
    if curl -ks https://$host/api/product/123 -H "Authorization: Bearer $TOKEN" -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "fail"
          return 1
    fi;
}

function waitForAPI() {

    url=$@
    echo -n "Wait for API: $url... "
    n=0
    until testAPI $url
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

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose stop"
    docker-compose stop
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForServices

echo ''
echo "Get an OAuth Access Token:"
echo "$ curl -ks https://acme:acmesecret@$host:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=user -d password=password | jq ."
OAUTH_RESPOSE=`curl -ks https://acme:acmesecret@$host:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=user -d password=password`
echo $OAUTH_RESPOSE | jq .
export TOKEN=`echo $OAUTH_RESPOSE | jq -r .access_token`
#TOKEN=`curl -ks https://acme:acmesecret@$host:9999/uaa/oauth/token   -d grant_type=password  -d client_id=acme  -d scope=webshop  -d username=user  -d password=password | jq -r .access_token`
echo "ACCESS TOKEN: $TOKEN"

echo ''
echo "Call API with Access Token... "
waitForAPI
echo "$ curl -ks https://$host/api/product/123 -H \"Authorization: Bearer \$TOKEN\" | jq ."
curl -ks https://$host/api/product/123 -H  "Authorization: Bearer $TOKEN" | jq .

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose stop"
    docker-compose stop
fi




