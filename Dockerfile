FROM openjdk:11
WORKDIR /opt/hik-dev
EXPOSE 8923
ENTRYPOINT ["java", "-jar", "target/hik-dev-0.1.0.jar"]