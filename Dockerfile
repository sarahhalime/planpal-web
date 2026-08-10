# Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package

# Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/planpal-web.jar ./planpal-web.jar

# Only the scrubbed demo dataset is baked in: made-up trips and demo accounts with
# no real password hashes, photos or credentials. The local PlanPalDatabase folder
# is deliberately never copied. Anything the app writes lives in the container and
# is lost on redeploy, which is fine for a demo but is the first thing to change if
# this ever holds real accounts.
COPY demo-data ./PlanPalDatabase

# The host supplies PORT; the app falls back to 8080 locally.
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "planpal-web.jar"]
