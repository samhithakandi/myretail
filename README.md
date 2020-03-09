# MyRetail

MyRetail is a rest api based on spring boot for getting aggregated product information using sources such as external api and mongo database. The api also has ability to update pricing for a product in mongo db.

## Configuration

Mongo db connection url is an instance running temporarily in Mongo DB Atlas Cloud. API url for product information is available via [https://redsky.target.com](https://redsky.target.com)

## Run locally

It is a spring boot application with maven. The port is currently set to 7879. It can be started in multiple ways.

#### Java cmd line

```bash
java -jar target/myretail-0.0.1-SNAPSHOT.jar
```

#### Maven plugin

```bash
mvn spring-boot:run
```

## Swagger API

The swagger ui link below lists out all the endpoint and their request & response contracts.
[http://localhost:7879/swagger-ui.html](http://localhost:7879/swagger-ui.html)

## Testing

The project has unit tests for service methods which have the core business logic.
To test the api as a whole, swagger ui can be used.

## License
[MIT](https://choosealicense.com/licenses/mit/)