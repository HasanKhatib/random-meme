FROM eclipse-temurin:17-jdk-focal

ENV RAPIDAPI_KEY default_value
COPY ./build/libs/*.jar /app.jar
USER 1000:1000

CMD ["java", "-jar", "/app.jar"]