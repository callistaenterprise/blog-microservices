#!/usr/bin/env bash

cf stop auth-server
cf stop discovery-server
cf stop config-server
cf stop edge-server
cf stop monitor-dashboard
cf stop product-api-service
cf stop product-composite-service
cf stop product-service
cf stop recommendation-service
cf stop review-service
cf stop turbine

cf apps

