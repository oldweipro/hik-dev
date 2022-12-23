FROM openjdk:11
WORKDIR /opt/hik-dev
COPY target/hik-dev-0.1.0.jar ./
EXPOSE 8923
ENTRYPOINT ["java", "-jar", "hik-dev-0.1.0.jar"]