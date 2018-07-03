#!/usr/bin/env bash

set -eu

kubectl create -f definitions/namespace.yml

kubectl config set-context $(kubectl config current-context) --namespace=myns

kubectl create configmap review-config --from-file=../../microservices/core/review-service/src/main/resources/application.yml
kubectl create configmap review-config --from-file=../../microservices/core/review-service/src/main/resources/application.yml --dry-run -o yaml | kubectl replace -f -
kubectl describe configmap review-config

kubectl create -f definitions/mongodb.yml
kubectl create -f definitions/mysql.yml
kubectl create -f definitions/product.yml
kubectl create -f definitions/review.yml
kubectl create -f definitions/recommendation.yml
kubectl create -f definitions/product-composite.yml
