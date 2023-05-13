
# docker build . -t jadsonjs/ci-workflows-trader:v1.0.0

# build #
FROM gradle:8.1.1-jdk17 as builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build

# docker push jadsonjs/ci-workflows-trader:v1.0.0

# execution #
FROM eclipse-temurin:17.0.7_7-jdk
COPY --from=builder /app/build/libs/ci-workflows-trader*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]