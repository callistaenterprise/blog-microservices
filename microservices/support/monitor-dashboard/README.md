# Hystrix Dashboard

Run this app as a normal Spring Boot app and then go to the home page
in a browser. If you run from this project it will be on port 7979
(per the `application.yml`). On the home page is a form where you can
enter the URL for an event stream to monitor, for example (the
customers service running locally):
`http://localhost:9000/hystrix.stream`. Any app that uses
`@EnableHystrix` will expose the stream.

To aggregate many streams together you can use the
[Turbine sample](https://github.com/spring-cloud-samples/turbine).
