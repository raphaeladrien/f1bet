FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS build

LABEL maintainer="Sporty Group"

ENV APP_HOME=/app \
    GRADLE_OPTS=-Dorg.gradle.daemon=false

WORKDIR $APP_HOME

COPY gradle gradle
COPY ["gradlew", "build.gradle", "settings.gradle", "$APP_HOME/"]

COPY src src
COPY db db

RUN chmod +x gradlew

RUN ./gradlew clean build --parallel

# Production image
FROM mcr.microsoft.com/openjdk/jdk:21-distroless AS f1bet

LABEL maintainer="Sporty Group"

COPY --from=build /app/build/libs/f1bet.jar .
COPY --from=build /app/db ./db

CMD ["-jar", "f1bet.jar"]

