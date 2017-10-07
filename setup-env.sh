#!/usr/bin/env bash

export MY_JAVA_TOOL_OPTIONS=-Xmx256M
export MY_CONFIG_USER=config_client
export MY_CONFIG_PWD=config_client_pwd
export MY_CONFIG_ENCRYPT_KEY=my-very-secret-encryption-key
#export COMPOSE_FILE=docker-compose-with-elk.yml
export COMPOSE_FILE=docker-compose-without-elk.yml
