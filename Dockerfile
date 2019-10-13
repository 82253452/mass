FROM openjdk:8u181-jdk-alpine
ARG workdir=/tmp
VOLUME ${workdir}
WORKDIR ${workdir}
ADD ./target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","app.jar"]