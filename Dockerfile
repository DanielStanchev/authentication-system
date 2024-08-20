FROM amazoncorretto:21-alpine

WORKDIR /app

COPY rest/target/rest-0.0.1-SNAPSHOT.jar /app/authentication.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/authentication.jar"]


#docker file for image creation