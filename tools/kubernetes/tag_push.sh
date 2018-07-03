#!/usr/bin/env bash

# Create build tags and push it
TAG=Spring5-1.0.0
docker tag blogmicroservices_pro       magnuslarsson/ms-blog-product-service:$TAG
docker tag blogmicroservices_rec       magnuslarsson/ms-blog-recommendation-service:$TAG
docker tag blogmicroservices_rev       magnuslarsson/ms-blog-review-service:$TAG
docker tag blogmicroservices_composite magnuslarsson/ms-blog-product-composite-service:$TAG

#docker push magnuslarsson/ms-blog-product-service:master.$TAG
#docker push magnuslarsson/ms-blog-recommendation-service:master.$TAG
#docker push magnuslarsson/ms-blog-review-service:master.$TAG
#docker push magnuslarsson/ms-blog-product-composite-service:master.$TAG
