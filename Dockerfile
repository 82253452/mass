FROM openjdk:8u181-jdk-alpine

ARG workdir=/app
ARG APPDIR
ARG PORT

VOLUME ${workdir}

WORKDIR ${workdir}

ADD ${APPDIR} app.jar
EXPOSE ${PORT}


ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Xmx1024m -Xms1024m -XX:MetaspaceSize=512m","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","app.jar"]