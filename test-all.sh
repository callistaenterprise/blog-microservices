#!/usr/bin/env bash
#
# Sample use of overriding parameters:
# - host, name of the host where all microservices are running
# - port, the port where the API is available
#
# $ host=myhost port=8765 ./test-all.sh
#
: ${host=localhost}
: ${port=443}

# set -e

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
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
    echo "Is the API awake?"
    echo "$ curl -ks https://$host:$port/api/product/123 -H \"Authorization: Bearer \$TOKEN\" | jq ."
    if curl -ks https://$host:$port/api/product/123 -H "Authorization: Bearer $TOKEN" -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
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

echo "Start:" `date`

. ./setup-env.sh

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForServices

#echo ''
#echo "Call /info on each microservice:"
#docker-compose exec composite wget -qO- localhost:8080/info | jq
#docker-compose exec pro wget -qO- localhost:8080/info | jq
#docker-compose exec rev wget -qO- localhost:8080/info | jq
#docker-compose exec rec wget -qO- localhost:8080/info | jq


echo ''
echo "Get an OAuth Access Token:"
echo "$ curl -ks https://acme:acmesecret@$host:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=user -d password=password | jq ."
OAUTH_RESPOSE=`curl -ks https://acme:acmesecret@$host:9999/uaa/oauth/token -d grant_type=password -d client_id=acme -d scope=webshop -d username=user -d password=password`
echo $OAUTH_RESPOSE | jq .
export TOKEN=`echo $OAUTH_RESPOSE | jq -r .access_token`
echo "ACCESS TOKEN: $TOKEN"

echo ''
echo "Call API with Access Token... "
waitForAPI
echo "$ curl -ks https://$host:$port/api/product/123 -H \"Authorization: Bearer \$TOKEN\" | jq ."
curl -ks https://$host:$port/api/product/123 -H  "Authorization: Bearer $TOKEN" | jq .

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End:" `date`