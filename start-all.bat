start /D microservices\support\discovery-server            gradlew bootRun
start /D microservices\support\edge-server                 gradlew bootRun

start /D microservices\core\product-service                gradlew bootRun
start /D microservices\core\recommendation-service         gradlew bootRun
start /D microservices\core\review-service                 gradlew bootRun
start /D microservices\compoite\product-composite-service  gradlew bootRun 