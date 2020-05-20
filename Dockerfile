FROM openjdk:8u181-jdk-alpine

ARG workdir=/app
ARG APPDIR
ARG PORT

VOLUME ${workdir}

WORKDIR ${workdir}

ADD ${APPDIR} app.jar
EXPOSE ${PORT}


ENTRYPOINT ["java","-Xmx1024m -Xms1024m -XX:NewRatio=4 -XX:MaxPermSize=1024m","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","app.jar"]