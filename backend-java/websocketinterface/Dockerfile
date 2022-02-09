FROM openjdk:11-jdk
ARG JAR_FILE=./build/libs/*.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]