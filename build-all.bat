pushd util &                                              call gradlew clean publishToMavenLocal & popd

pushd microservices\core\product-service &                call gradlew clean publishToMavenLocal build distDocker & popd
pushd microservices\core\recommendation-service &         call gradlew clean publishToMavenLocal build distDocker & popd
pushd microservices\core\review-service &                 call gradlew clean publishToMavenLocal build distDocker & popd
pushd microservices\composite\product-composite-service & call gradlew clean build distDocker & popd
pushd microservices\api\product-api-service &             call gradlew clean build distDocker & popd

pushd microservices\support\auth-server &                 call gradlew clean build distDocker & popd
pushd microservices\support\discovery-server &            call gradlew clean build distDocker & popd
pushd microservices\support\edge-server &                 call gradlew clean build distDocker & popd
pushd microservices\support\monitor-dashboard &           call gradlew clean build distDocker & popd
pushd microservices\support\turbine &                     call gradlew clean build distDocker & popd