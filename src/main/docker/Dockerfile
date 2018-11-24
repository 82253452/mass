FROM openjdk:8-jdk-alpine
VOLUME /opt/buildagent/work/887e7abfd6d84df1/target
ARG JAR_FILE
ADD api.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","/app.jar"]

