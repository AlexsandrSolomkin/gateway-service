# Инструкция по запуску проекта Microservices

Проект состоит из следующих микросервисов:

1. config-server          - Spring Cloud Config Server
2. eureka-server          - Spring Cloud Eureka Server
3. user-service           - Сервис управления пользователями
4. gateway-service        - API Gateway с Circuit Breaker и Service Discovery

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

Клонируйте каждый сервис в отдельную папку. Замените ссылки на репозитории на актуальные.

- config-server: https://github.com/AlexsandrSolomkin/config-server
- eureka-server: https://github.com/AlexsandrSolomkin/eureka-server
- user-service: https://github.com/AlexsandrSolomkin/Intensive_Java_DataBase/tree/aleksandrSolomkin_v0.5
- gateway-service: https://github.com/AlexsandrSolomkin/gateway-service

---

## Шаг 1: Настройка Config Server

1. Перейдите в папку config-server.
2. Откройте файл `application.yml` и укажите путь к Git-репозиторию с конфигурациями (если используется Git) или локальный путь к папкам с конфигами.
3. Соберите проект:

mvn clean install

4. Запустите сервис:

mvn spring-boot:run

Config Server будет доступен на http://localhost:8888

---

## Шаг 2: Настройка Eureka Server

1. Перейдите в папку eureka-server.
2. Соберите проект:

mvn clean install

3. Запустите сервис:

mvn spring-boot:run

Eureka Server будет доступен на http://localhost:8761. Здесь будут регистрироваться все сервисы.

---

## Шаг 3: Настройка User Service

1. Перейдите в папку user-service.
2. Откройте `application.yml` и укажите:

- URL к вашей базе PostgreSQL
- Имя пользователя и пароль

Пример:

spring:
datasource:
url: jdbc:postgresql://localhost:5432/user_service_db
username: user_service_user
password: <ваш пароль>

3. Соберите проект:

mvn clean install

4. Запустите сервис:

mvn spring-boot:run

User Service будет доступен на http://localhost:8080

---

## Шаг 4: Настройка Gateway Service

1. Перейдите в папку gateway-service.
2. Убедитесь, что в `application.yml` настроены:

- Сервис discovery через Eureka
- Настройки маршрутов к user-service

3. Соберите проект:

mvn clean install

4. Запустите сервис:

mvn spring-boot:run

Gateway будет доступен на http://localhost:8081 (или порт из `application.yml`).

---

## Шаг 5: Проверка работы

1. Перейдите в Eureka Dashboard (http://localhost:8761) и убедитесь, что все сервисы зарегистрированы.
2. Используйте API Gateway для доступа к User Service, например:

GET http://localhost:8081/users
POST http://localhost:8081/users

3. Для проверки Circuit Breaker можно временно остановить User Service и проверить, что Gateway корректно обрабатывает ошибки.

---

## Дополнительно

- Swagger UI User Service: http://localhost:8080/swagger-ui.html
- Для добавления новых микросервисов добавляйте их в Eureka и настраивайте маршруты в Gateway.
- Все настройки можно хранить в Config Server, чтобы менять порты, URL и креды без пересборки сервисов.

---

Конец инструкции.