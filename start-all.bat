# Use Windows Services to ensure that the Rabbitmq service is started!

start /D microservices\support\discovery-server             gradlew bootRun
start /D microservices\support\edge-server                  gradlew bootRun
start /D microservices\support\monitor-dashboard            gradlew bootRun
start /D microservices\support\turbine                      gradlew bootRun

start /D microservices\core\product-service                 gradlew bootRun
start /D microservices\core\recommendation-service          gradlew bootRun
start /D microservices\core\review-service                  gradlew bootRun
start /D microservices\composite\product-composite-service  gradlew bootRun 