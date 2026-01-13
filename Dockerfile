# 1) Build stage
FROM gradle:8.4-jdk21 AS build
WORKDIR /app

# Build arg to optionally refresh dependencies
ARG GRADLE_REFRESH=false

# Copy build files first (cache-friendly)
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Warm dependency cache (refresh only if requested)
RUN if [ "$GRADLE_REFRESH" = "true" ]; then \
  echo "Refreshing Gradle dependencies..." && \
  gradle --no-daemon dependencies --refresh-dependencies || true ; \
  else \
  echo "Using cached Gradle dependencies..." && \
  gradle --no-daemon dependencies || true ; \
  fi

# Source copy + build (limit memory for t2.micro)
COPY src ./src
RUN GRADLE_OPTS="-Xmx512m" gradle --no-daemon bootJar -x test

# 2) Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
