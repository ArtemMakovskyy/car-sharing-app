# 🚗"Car Sharing Service API" !

### 👓Project description

*With the REST Car Sharing Service API, you can rent cars and pay for them with your credit card through Stripe. All records are kept in the application and you can get data about cars and payments at any time. The car sharing administration has the ability to find out who returned the car on time and who did not. The system has an alert service via Telegram. After registering in Telegram, you can ask a question to the service administrator and receive a notification about your rental.*

---

### 🖥️Technologies and tools used

- Programming Language: Java (version 17)
- Application Framework: Spring Boot (version 3.1.2)
- Dependency Management: Apache Maven
- REST
- Security
  - Spring Security
  - JSON Web Tokens (JWT)
- Spring Data JPA
- Spring MVC
- JSON Libraries:
  - Jackson Data Type for JSR310 (for handling dates in JSON)
  - Telegrambots (version 6.8.0) for Telegram integration
  - Stripe Java Payment (version 22.13.0) for Stripe integration
- Validation Libraries:
  - Spring Boot Starter Validation
  - Hibernate Validator (part of Spring Boot Starter Validation)
- Authentication and Security Libraries:
  - Spring Boot Starter Security
  - Spring Security Test (for security testing)
- Database Libraries:
  - Spring Boot Starter Data JPA
  - H2 Database (temporary database for development)
  - MySQL Connector (driver for MySQL)
- Database Migration Libraries:
  - Liquibase Core
  - Liquibase Migration Maven Plugin (version 4.23.1)
- Testing Libraries:
  - JUnit Jupiter
  - Testcontainers (for containerized testing)
  - Mockito
- API Documentation Tools:
  - Springdoc OpenAPI Starter Webmvc UI (version 2.1.0)
- JWT Processing Libraries:
  - jjwt-api
  - jjwt-impl (included in runtime scope)
  - jjwt-jackson (included in runtime scope)
- Additional Tools and Libraries:
  - Lombok (for reducing boilerplate code)
  - MapStruct (for convenient object mapping)
  - Spring Boot DevTools (for development convenience)
  - Maven Checkstyle Plugin (for code style checking)
---

### 🧭Describing functionalities of controllers

1. Authentication Controller:
  - POST: /register - register a new user
  - POST: /login - get JWT tokens

2. Users Controller: Managing authentication and user registration
  - PUT: /users/{id}/role - update user role
  - GET: /users/me - get my profile info
  - PUT/PATCH: /users/me - update profile info

3. Cars Controller: Managing car inventory (CRUD for Cars)
  - POST: /cars - add a new car
  - GET: /cars - get a list of cars
  - GET: /cars/<id> - get car's detailed information
  - PUT/PATCH: /cars/<id> - update car (also manage inventory)
  - DELETE: /cars/<id> - delete car

4. Rentals Controller: Managing users' car rentals
  - POST: /rentals - add a new rental (decrease car inventory by 1)
  - GET: /rentals/?user_id=...&is_active=... - get rentals by user ID and whether the rental is still active or not
  - GET: /rentals/<id> - get specific rental
  - POST: /rentals/return 

5. Payments Controller (Stripe): Facilitates payments for car rentals through the platform. Interacts with Stripe API.
   Use stripe-java library.
  - GET: /payments/?user_id=... - get payments
  - POST: /payments/ - create payment session
  - GET: /payments/success/ - check successful Stripe payments (Endpoint for stripe redirection)
  - GET: /payments/cancel/ - return payment paused message (Endpoint for stripe redirection)

6. Notifications Service (Telegram):
  - Notifications about new rentals created, overdue rentals, and successful payments
  - Other services interact with it to send notifications to car sharing service administrators.
  - Uses Telegram API, Telegram Chats, and Bots.
  

### ▶️How to set up and start the project

- **Soft requirements**
    - Java Development Kit (JDK) version 11 or higher.
    - Maven Version: 4.0.0
    - Git
    - MySQL
    - Docker
    - PostMan
- **Instalation**
    - Clone the repository from github:
  ```shell
  git clone git@github.com:https://github.com/ArtemMakovskyy/car-sharing-app
   ```
    - Start the Docker
    - Configure the database parameters in the .env file
    - Open a terminal and navigate to the root directory of your project
    - Into the terminal use command to build the container and start project.
  ```shell
    docker-compose build
    docker-compose up
   ```
    - First way to use the Car Sharing Service API it is SWAGGER

   ```shell
     http://localhost:8080/api/swagger-ui/index.html#/
   ```
    - Second way to use the BookStoreApi it is PostMan
---

