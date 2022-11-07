FROM eclipse-temurin:17-jdk-focal

ENV RAPIDAPI_KEY default_value
ENV JAEGER_URI http://localhost:14268/api/traces
ENV PORT 8080
COPY ./build/libs/*.jar /app.jar
USER 1000:1000

CMD ["java", "-jar", "/app.jar"]