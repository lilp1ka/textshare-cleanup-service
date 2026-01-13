FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# network utils
RUN apt-get update && \
    apt-get install -y netcat-openbsd curl iputils-ping dnsutils && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/cleanup-0.0.1-SNAPSHOT.jar app.jar

#entrypoint script
RUN echo '#!/bin/bash\n\
set -e\n\
echo "Testing network connectivity..."\n\
echo "Resolving redis hostname:"\n\
getent hosts redis || echo "Failed to resolve redis"\n\
echo "Pinging redis:"\n\
ping -c 3 redis || echo "Ping failed"\n\
echo "Testing Redis port:"\n\
nc -zv redis 6379 || echo "Port test failed"\n\
echo "Attempting telnet to Redis:"\n\
timeout 5 bash -c "</dev/tcp/redis/6379" && echo "TCP connection successful" || echo "TCP connection failed"\n\
echo "Starting application..."\n\
exec java -jar app.jar\n\
' > /entrypoint.sh && chmod +x /entrypoint.sh

EXPOSE 8000
ENTRYPOINT ["/entrypoint.sh"]