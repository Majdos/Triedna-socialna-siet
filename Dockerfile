FROM openjdk:8-jdk-alpine3.9

ENTRYPOINT ["java", "-jar", "/usr/share/myservice/myservice.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD ./target/lib /usr/share/maturita/lib
# Add the service itself
ARG JAR_FILE
ADD ./target/${JAR_FILE} /usr/share/myservice/myservice.jar
