# Инструкция по запуску проекта Microservices

Структура проекта:
- user-service — микросервис для управления пользователями
- gateway-service — API Gateway с маршрутизацией и circuit breaker
- eureka-server — сервис регистрации и discovery
- config-server — централизованный сервер конфигурации
- config-repo — локальный git репозиторий с конфигурациями для сервисов

Конфигурации:
- User Service: application.yml берётся из config-server
- Gateway Service: application.yml берётся из config-server
- Config Server: читает конфигурации из локального git: file:///D:/config-repo
- Eureka Server: порт 8761

microservices-project/
├── config-server/
│   ├── src/main/java/com/example/configserver/ConfigServerApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── eureka-server/
│   ├── src/main/java/com/example/eurekaserver/EurekaServerApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── user-service/
│   ├── src/main/java/com/example/userservice/UserServiceApplication.java
│   ├── src/main/java/com/example/userservice/config/OpenApiConfig.java
│   ├── src/main/java/com/example/userservice/controller/UserController.java
│   ├── src/main/java/com/example/userservice/dto/UserDto.java
│   ├── src/main/java/com/example/userservice/entity/User.java
│   ├── src/main/java/com/example/userservice/exception/GlobalExceptionHandler.java
│   ├── src/main/java/com/example/userservice/mapper/UserMapper.java
│   ├── src/main/java/com/example/userservice/repository/UserRepository.java
│   ├── src/main/java/com/example/userservice/service/IUserService.java
│   ├── src/main/java/com/example/userservice/service/UserNotFoundException.java
│   ├── src/main/java/com/example/userservice/service/UserService.java
│   ├── src/test/java/com/example/userservice/controller/UserControllerTest.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/
│   ├── src/main/java/com/example/notificationservice/NotificationServiceApplication.java
│   ├── src/main/java/com/example/notificationservice/controller/NotificationController.java
│   ├── src/main/java/com/example/notificationservice/dto/NotificationDto.java
│   ├── src/main/java/com/example/notificationservice/entity/Notification.java
│   ├── src/main/java/com/example/notificationservice/repository/NotificationRepository.java
│   ├── src/main/java/com/example/notificationservice/service/NotificationService.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── gateway-service/
│   ├── src/main/java/com/example/gatewayservice/GatewayServiceApplication.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
└── docker-compose.yml

---

## Шаг 0: Клонирование репозиториев

Клонируйте каждый сервис в отдельную папку.

- config-server: https://github.com/AlexsandrSolomkin/config-server/tree/aleksandrSolomkin_v0.6
- eureka-server: https://github.com/AlexsandrSolomkin/eureka-server/tree/aleksandrSolomkin_v0.6
- user-service: https://github.com/AlexsandrSolomkin/Intensive_Java_DataBase/tree/aleksandrSolomkin_v0.6
- notification-service: https://github.com/AlexsandrSolomkin/notification_service/tree/aleksandrSolomkin_v0.6
- gateway-service: https://github.com/AlexsandrSolomkin/gateway-service/tree/aleksandrSolomkin_v0.6

Создать папку file:///D:/config-repo

Создать в ней файлы:
- application.yml:

  spring:
  datasource:
  url: jdbc:h2:mem:testdb
  driver-class-name: org.h2.Driver
  username: sa
  password:
  jpa:
  hibernate:
  ddl-auto: update
  show-sql: true

- notification-service.yml:
  server:
  port: 8082

  spring:
  datasource:
  url: jdbc:h2:mem:notificationDb
  driver-class-name: org.h2.Driver
  username: sa
  password:
  jpa:
  hibernate:
  ddl-auto: update
  show-sql: true

- some-service.yml (для других сервисов):

  server:
  port: 8081

  spring:
  datasource:
  url: jdbc:h2:mem:serviceDb
  driver-class-name: org.h2.Driver
  username: sa
  password:
  jpa:
  hibernate:
  ddl-auto: update
  show-sql: true

---
Создать внешний docker-compose.yml:

version: "3.9"

services:

postgres:
image: postgres:15
container_name: postgres
environment:
POSTGRES_DB: users_db
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres123
ports:
- "5432:5432"
volumes:
- postgres-data:/var/lib/postgresql/data
networks:
- microservices-network

zookeeper:
image: wurstmeister/zookeeper:3.4.6
container_name: zookeeper
ports:
- "2181:2181"
networks:
- microservices-network

kafka:
image: wurstmeister/kafka:2.13-2.8.0
container_name: kafka
depends_on:
- zookeeper
ports:
- "9092:9092"
environment:
KAFKA_BROKER_ID: 1
KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
networks:
- microservices-network

eureka-server:
container_name: eureka-server
build:
context: ./eureka-server
ports:
- "8761:8761"
networks:
- microservices-network

config-server:
container_name: config-server
build:
context: ./config-server
ports:
- "8888:8888"
volumes:
- D:/config-repo:/D:/config-repo
depends_on:
- eureka-server
networks:
- microservices-network

user-service:
container_name: user-service
build:
context: ./user-service
ports:
- "8080:8080"
depends_on:
- postgres
- kafka
- eureka-server
- config-server
networks:
- microservices-network

notification-service:
container_name: notification-service
build:
context: ./notification-service
ports:
- "8082:8082"
depends_on:
- kafka
- eureka-server
- config-server
networks:
- microservices-network

api-gateway:
container_name: api-gateway
build:
context: ./gateway-service
ports:
- "8081:8081"
depends_on:
- eureka-server
- config-server
networks:
- microservices-network

networks:
microservices-network:
driver: bridge

volumes:
postgres-data:

---

ПОРЯДОК ЗАПУСКА:

## Шаг 1: Запустить Eureka Server:
cd eureka-server
mvn clean spring-boot:run
Порт: 8761
URL интерфейса: http://localhost:8761

## Шаг 2: Запустить Config Server:
cd config-server
mvn clean spring-boot:run
Порт: 8888
Проверка: http://localhost:8888/user-service/default

## Шаг 3: Запустить User Service:
cd user-service
mvn clean spring-boot:run
Порт: 8080
URL для тестирования:
- GET http://localhost:8080/users
- POST http://localhost:8080/users

## Шаг 4: Запуск Notification Service
cd notification-service
mvn clean spring-boot:run
Порт: 8082
Проверка:
- GET http://localhost:8082/notifications
- POST http://localhost:8082/notifications

## Шаг 5: Запустить API Gateway:
cd gateway-service
mvn clean spring-boot:run
Порт: 8081
Проверка маршрутов:
GET http://localhost:8081/users          → user-service
GET http://localhost:8081/notifications  → notification-service
Тестирование fallback:
- Остановите любой сервис
- Gateway должен вернуть сообщение "Сервис временно недоступен. Попробуйте позже."

## Шаг 6: Проверка работы:
- Откройте Eureka Server: http://localhost:8761 — убедитесь, что сервисы зарегистрированы
- Используйте Postman или curl для тестирования маршрутов через gateway
- Остановите сервисы по очереди и проверьте fallback.

Полезные команды Maven:
- Сборка проекта: mvn clean install
- Форсированный апдейт зависимостей: mvn clean install -U
- Запуск конкретного модуля: mvn spring-boot:run -pl user-service

---

Конец инструкции.
