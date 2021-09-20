# retail-api

The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller.

Tech Stack :
- Spring Boot
- Spring data Cassandra
- Kafka
- AOP
- prometheus
- Grafana
- Spring webflux
- DataStax Cassandra
- Unit testing
- Java 8
- Lombok
- Swagger
- Caching


For running the application in your local machine. Please follow the below steps:
- Install docker
- Install Kafka
- Install java 8
- IntelliJ for IDE

After installation:
After installing Kafka go to Kafka folder and run the below two commands:
start zookeeper - 	bin/zookeeper-server-start.sh config/zookeeper.properties
start Kafka server - bin/kafka-server-start.sh config/server.properties

Navigate to the project folder and execute the below command in the terminal.
-> docker-compose up and 
- Run "TargetProductApplication" class or ./gradlew bootrun(in Terminal) to start the application


#Endpoints:

#GET
http://localhost:8080/products/13860428
#UPDATE
http://localhost:8080/products/13860428

Body:
{
"id": 13860428,
"name": "The Big Lebowski (Blu-ray)",
"current_price": {
"value": 15130,
"currency_code": "USD"
}
}
#METRICS
http://localhost:8080/actuator/metrics
#HEALTH
http://localhost:8080/actuator/health

