pushd microservices\core\product-service &                call gradlew clean publishToMavenLocal & popd
pushd microservices\core\recommendation-service &         call gradlew clean publishToMavenLocal & popd
pushd microservices\core\review-service &                 call gradlew clean publishToMavenLocal & popd
pushd microservices\composite\product-composite-service & call gradlew clean build & popd

pushd microservices\support\discovery-server &            call gradlew clean build & popd
pushd microservices\support\edge-server &                 call gradlew clean build & popd
pushd microservices\support\monitor-dashboard &           call gradlew clean build & popd
pushd microservices\support\turbine &                     call gradlew clean build & popd
