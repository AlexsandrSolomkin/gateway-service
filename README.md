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
│   └── pom.xml
└── gateway-service/
├── src/main/java/com/example/gatewayservice/GatewayServiceApplication.java
├── src/main/resources/application.yml
└── pom.xml

---

## Шаг 0: Клонирование репозиториев

Клонируйте каждый сервис в отдельную папку.

- config-server: https://github.com/AlexsandrSolomkin/config-server
- eureka-server: https://github.com/AlexsandrSolomkin/eureka-server
- user-service: https://github.com/AlexsandrSolomkin/Intensive_Java_DataBase/tree/aleksandrSolomkin_v0.5
- gateway-service: https://github.com/AlexsandrSolomkin/gateway-service

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

- some-service.yml:

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

## Шаг 4: Запустить API Gateway:
cd gateway-service
mvn clean spring-boot:run
Порт: 8081
Пример маршрута через Gateway: GET http://localhost:8081/users
Fallback: если user-service недоступен, ответ — "Сервис пользователей временно недоступен. Попробуйте позже."

## Шаг 5: Проверка работы:
- Откройте Eureka Server: http://localhost:8761 — убедитесь, что сервисы зарегистрированы
- Используйте Postman или curl для тестирования маршрутов через gateway
- Остановите user-service и проверьте работу fallback через gateway (http://localhost:8081/users)

Полезные команды Maven:
- Сборка проекта: mvn clean install
- Форсированный апдейт зависимостей: mvn clean install -U
- Запуск конкретного модуля: mvn spring-boot:run -pl user-service

---

Конец инструкции.
