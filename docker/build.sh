#! /bin/bash -e

#rm -fr build
#mkdir build
#cp ../build/libs/spring-boot-restful-service.jar build

docker build -t "magnus-larsson/spring-boot" .
