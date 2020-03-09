FROM adoptopenjdk/openjdk11:ubi

RUN mkdir -p /app

COPY ./target/myretail-*.jar /app/app.jar

WORKDIR /app

# Expose ports.
EXPOSE 7879

CMD ["java", "-jar", "app.jar"]