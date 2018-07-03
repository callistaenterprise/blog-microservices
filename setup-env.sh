#!/usr/bin/env bash

#export MY_JAVA_TOOL_OPTIONS="-Xmx256M -javaagent:/prometheus-lib/jmx_prometheus_javaagent-0.1.0.jar=7070:/prometheus-lib/spring-boot-jmx.yml"
export MY_JAVA_TOOL_OPTIONS="-Xmx1024M -javaagent:/prometheus-lib/jmx_prometheus_javaagent-0.1.0.jar=7070:/prometheus-lib/spring-boot-jmx.yml"
export MY_CONFIG_USER=config_client
export MY_CONFIG_PWD=config_client_pwd
export MY_CONFIG_ENCRYPT_KEY=my-very-secret-encryption-key
#export COMPOSE_FILE=docker-compose-with-elk.yml
#export COMPOSE_FILE=docker-compose-without-elk.yml
export COMPOSE_FILE=docker-compose-spring5.yml
