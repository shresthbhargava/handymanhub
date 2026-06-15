# ── Stage 1: Build ────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy pom.xml first — download dependencies separately
# This layer is cached. If pom.xml doesn't change,
# Maven won't re-download dependencies on every build
COPY pom.xml .
RUN mvn dependency:go-offline -q || mvn dependency:go-offline -q

# Now copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Run ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root user — security best practice
# Never run apps as root inside containers
RUN addgroup --system handyman && \
    adduser --system --ingroup handyman handyman

# Create logs directory with correct permissions
RUN mkdir -p /app/logs && chown -R handyman:handyman /app

# Copy only the built jar from Stage 1
COPY --from=builder /build/target/handymanhub-0.0.1-SNAPSHOT.jar app.jar
RUN chown handyman:handyman app.jar

# Switch to non-root user
USER handyman

# Expose port
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]