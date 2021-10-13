# CricketScoreBoard
Live Cricket Match Scoreboard” is a web-based application developed on Spring Framework to get the live update of the scoreboard. The idea is to provide user to get update of the cricket match scoreboard even when the user is not watching the match live. 
## Build With
1 Spring Boot - 2.5.2\
2 JPA(Java Persistance API)\
3 Lombok - 2.9.2\
4 Swagger\
5 Flyway\
6 Docker\
7 Rabbitmq - 3.1.3\
8 Mysql - 8\
9 Gradle\
10 Grafana\
11 Prometheus

## Testing Password and Username
Rahul
rahul1234
## System Design
This is the working model of the CricketScoreBoard Api
![](https://github.com/vaibhavmaithani15/CricketScoreBoard/blob/main/src/main/resources/images/system.png)




## Getting Started
This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.
## Installation 
Copy and paste these commands inside the docker file.
#### MySql Docker Image
To run mysql docker image
~~~
$ docker run -d --rm --name mysql -e MYSQL_ROOT_PASSWORD=dummypassword -e MYSQL_DATABASE=scoreboard -e MYSQL_USER=rahul -e MYSQL_PASSWORD=abc123 -p 3308:3306 mysql:5.7

~~~
#### Prometheus Docker Image
To run prometheus docker image
~~~

$ docker pull prom/prometheus
 
$ docker run -d --rm --name=prometheus -p 9090:9090 -v C:/Users/"Vaibhav Maithani"/Desktop/Project_new/CricketScoreBoard/monitoring/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus

~~~

#### Rabbitmq Docker Image
RabbitMQ is a messaging broker - an intermediary for messaging. It gives your applications a common platform to send and receive messages, and your messages a safe place to live until received.

##### To run rabbitmq docker image
~~~
$ docker run -d --rm --name rabbitmq -p 15672:15672 -p 5672:5672 rabbitmq:3-management
 ~~~
##### Port address of rabbitmq
~~~
$ http://localhost:15672
~~~
##### Username and Password of rabbitmq 
~~~
Username:- guest
Password:- guest
~~~

## PKCS12 Keystore Generation 
Run these command to generate a TLS(Transport Layer Security) certificate self signed for security of transfer data over the network from http to https 
##### To generate PKCS12 security certificate (self signed)
~~~
$ keytool -genkeypair -alias scoreboard -keyalg RSA -keysize 4096 -storetype PKCS12 -keystore scoreboard.p12 -validity 3650 -storepass xyz123 -ext SAN=dns:testa.abc.com,ip:1.1.1.1
keytool -genkeypair -keystore <keystore> -dname "CN=test, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -keypass <keypwd> -storepass <storepass> -keyalg RSA -alias unknown -ext SAN=dns:test.abc.com,ip:1.1.1.1
~~~
##### To view PKCS12 security certificate (self signed)
~~~
$ keytool -list -v -keystore scoreboard.p12
~~~

## Database Diagram
This is the representation of database table to understand the tables inside the database.  
![](https://github.com/vaibhavmaithani15/CricketScoreBoard/blob/main/src/main/resources/images/Database.png)

## Docker command to create image of application
docker build . -f ./docker/Dockerfile -t vaibhav15/smartcontact:latest

## Docker command to run the docker compose file
docker-compose -f ./docker/docker-compose.yml up



## Docker command to push docker image to docker hub
docker tag vaibhav15/smartcontact:latest vaibhav15/smartcontact:1.0.0
docker push vaibhav15/smartcontact:1.0.0






