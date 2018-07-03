#!/usr/bin/env bash

kubectl create -f namespace.yml

kubectl config set-context $(kubectl config current-context) --namespace=myns

kubectl create -f definitions/mongodb.yml
kubectl create -f definitions/mysql.yml
kubectl create -f definitions/product.yml
kubectl create -f definitions/review.yml
kubectl create -f definitions/recommendation.yml
kubectl create -f definitions/composite.yml
