# Config Server Sample

Run this project as a Spring Boot app, e.g. import into IDE and run
main method, or use Maven: 

```
$ mvn spring-boot:run
```

or

```
$ mvn package
$ java -jar target/*.jar
```

It will start up on port 8888 and serve configuration data from
"https://github.com/spring-cloud-samples/config-repo":

## Resources

| Path             | Description  |
|------------------|--------------|
| /{app}/{profile} | Configuration data for app in Spring profile (comma-separated).|
| /{app}/{profile}/{label} | Add a git label |

## Security

The server is protected by HTTP Basic authentication. The user name is
"user" and the password is printed on the console on startup (standard Spring Boot approach), e.g.

```
2014-10-23 08:55:01.579  INFO 8185 --- [           main] b.a.s.AuthenticationManagerConfiguration : 

Using default security password: 83805c57-8c76-4940-ae17-299359888177


```

There is also a password stored in a keystore in the jar file if you
want to use that for a more realistic simulation of a real system. To
unlock the password you need the full strength JCE extensions
(download from Oracle and unpack the zip then copy the jar files to
`<JAVA_HOME>/jre/lib/security`), and the keystore password ("foobar"
stored in plain text in this README for the purposes of a demo, but in
a real system you would keep it secret and only expose via environment
variables).  The password is bound to the app from the Spring
environment key `keystore.password` (so an OS environment variable
KEYSTORE_PASSWORD works).  E.g.

```
$ KEYSTORE_PASSWORD=foobar java -jar target/*.jar
```
