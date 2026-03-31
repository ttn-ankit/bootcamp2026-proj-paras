FROM openjdk:22-jdk
COPY target/e-commerce.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]