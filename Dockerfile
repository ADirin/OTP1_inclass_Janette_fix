# --- Stage 1: Build JAR with Maven ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and source files
COPY pom.xml .
COPY src ./src

# Build the jar skipping unit tests
RUN mvn package -DskipTests

# --- Stage 2: Minimal Runtime Environment ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install dependencies required for X11 forwarding and JavaFX rendering
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    libgl1 \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*

# Download and extract JavaFX Linux SDK
RUN mkdir -p /javafx-sdk \
    && wget -O javafx.zip https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip \
    && unzip javafx.zip -d /javafx-sdk \
    && mv /javafx-sdk/javafx-sdk-21.0.2/lib /javafx-sdk/lib \
    && rm -rf /javafx-sdk/javafx-sdk-21.0.2 javafx.zip

# Copy compiled executable JAR from builder stage
COPY --from=builder /app/target/temp.jar /app/temp.jar

# Configure Display variable for X11 forwarding
ENV DISPLAY=host.docker.internal:0.0

# Launch JAR with JavaFX module path enabled
CMD ["java", "--module-path", "/javafx-sdk/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "temp.jar"]