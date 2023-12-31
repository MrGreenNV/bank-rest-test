<div style="text-align: center;">

# **Account Service**
</div>

## **RESTful API для управления банковскими счетами.**
____
### Описание сервиса
Этот проект представляет собой пример реализации RESTful API для управления банковскими счетами, 
включая создание счетов, внесение денег, снятие денег и перевод средств между счетами. 
API взаимодействует с бэкенд-сервисом, который использует встроенную базу данных для 
хранения данных о счетах.

Используемые технологии

* Java 17
* Spring Boot 3
* H2 databases
* Liquibase
* Lombok
* Mockito
* JUnit
* OpenAPI (Swagger)
* Docker
____
### Функциональности

- Управление: Создание, обновление, просмотр, удаление и деактивация банковских счетов.
- Пополнение: Пополнение баланса банковского счета.
- Снятие: Уменьшение баланса банковского счета.
- Перевод: Взаимодействие между банковскими счетами по средствам перевода средств между ними.  
____

### Установка и настройка для запуска

#### Требования

Для запуска и использования этого API вам потребуется:

- Java (рекомендуется использовать Spring Boot)
- Maven (для сборки проекта)
- Браузер или инструмент для выполнения HTTP-запросов (например, Postman)

##### 1. Клонируйте репозиторий:
```
git clone https://github.com/MrGreenNV/bank-rest-test.git
```
##### 2. Перейдите в директорию проекта:
```
cd bank-rest-test
```
##### 3. Соберите проект с помощью Maven:
```
mvn clean install
```
##### 4. Запустите backend-сервис:
```
java -jar target/bankservice-0.0.1-SNAPSHOT.jar
```

### Установка и настройка для запуска в Docker Compose

#### Требования

Для запуска и использования этого API вам потребуется:

- Docker Compose
- Браузер или инструмент для выполнения HTTP-запросов (например, Postman)

##### 1. Клонируйте репозиторий:
```
git clone https://github.com/MrGreenNV/bank-rest-test.git
```
##### 2. Перейдите в директорию проекта:
```
cd bank-rest-test
```
##### 3. Для запуска проекта в Docker воспользуйтесь следующей командой:
```
docker compose up --build -d
```

##### После запуска, микросервис будет доступен по адресу: http://localhost:8181/

----
### API-endpoints
Документация OpenAPI (Swagger) будет доступна после запуска проекта по ссылке: http://localhost:8181/swagger-ui/index.html#/
____
### Тестирование
Сервис включает модульные тесты для проверки его функциональности. Вы можете запустить тесты с помощью сборщика Maven:
```
mvn test
```
____
### Вклад и обратная связь
Если вы хотите внести свой вклад в развитие сервиса или обнаружили проблему, пожалуйста, создайте issue в репозитории проекта или отправьте pull request с вашими предложениями.
____
### Документация проекта
Подробную документацию проекта вы можете найти, перейдя по ссылке:
https://mrgreennv.github.io/bank-rest-test