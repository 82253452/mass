FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ../../../target/api.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","/app.jar"]

