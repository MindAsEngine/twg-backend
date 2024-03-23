FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM eclipse-temurin
COPY --from=build /home/app/target/twg-alfa.jar /usr/local/lib/twg-alfa.jar
EXPOSE 1337
ENTRYPOINT ["java","-jar","/usr/local/lib/twg-alfa.jar"]