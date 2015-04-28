#!/usr/bin/env bash

cf start auth-server
cf start discovery-server
cf start edge-server
cf start monitor-dashboard
cf start product-api-service
cf start product-composite-service
cf start product-service
cf start recommendation-service
cf start review-service
cf start turbine

cf apps

