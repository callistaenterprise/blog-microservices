# Zuul Server

Run this app as a normal Spring Boot app. If you run from this project 
it will be on port 8765 (per the `application.yml`). Also run the
[stores](https://github.com/spring-cloud-samples/customers-stores/tree/master/rest-microservices-store) 
and [customers](https://github.com/spring-cloud-samples/customers-stores/tree/master/rest-microservices-customers) 
samples from the [customer-stores](https://github.com/spring-cloud-samples/customers-stores) 
sample.  

You should then be able to view json content from 
`http://localhost:8765/stores` and `http://localhost:8765/customers` which are
configured in `application.yml` as proxy routes.
