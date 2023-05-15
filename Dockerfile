
# docker build . -t jadsonjs/ci-workflows-trader:v1.0.0 --build-arg GITHUB_TOKEN=xxxxxxxxxx

# build #
FROM gradle:8.1.1-jdk17 as builder
ARG GITHUB_TOKEN
WORKDIR /app
COPY . .
RUN ./gradlew -Dgithub.token=$GITHUB_TOKEN clean build

# docker container run -d -p 8080:8080 -e github.token=xxxx --name ci-workflows-trader jadsonjs/ci-workflows-trader:v1.0.0

# execution #
FROM eclipse-temurin:17.0.7_7-jdk
COPY --from=builder /app/build/libs/ci-workflows-trader*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]


# docker push jadsonjs/ci-workflows-trader:v1.0.0