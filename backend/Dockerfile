FROM maven:3.9-eclipse-temurin-17

WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/workspace/target/library-management-system-0.0.1-SNAPSHOT.jar"]
