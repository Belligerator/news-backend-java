# Spring Boot backend for the News Mobile App

## About

Welcome to the News App Backend project! This repository serves as the backend component for the [News App](https://github.com/Belligerator/news-app), a Flutter application designed to deliver the latest news to users. The backend is built to provide data and functionality to support the app's features.

Backend is built using [Spring Boot] (https://spring.io/projects/spring-boot). It is a REST API that provides endpoints for the app to consume. For data storage, the backend uses [MySQL](https://www.mysql.com/) and interacts with it using [JPA](https://spring.io/projects/spring-data-jpa) and [Hibernate](https://hibernate.org/orm/).

This project is made as a sample project and implements some of the common features found in a typical backend application. List of features:

- CRUD operations
- Authentication and Authorization (JWT, basic auth, username/password login)
- Firebase Cloud Messaging (FCM) push notifications (via firebase-admin)
- Sending emails
- Exporting data to Excel file
- File uploads and downloads
- Dockerization
- Cron jobs
- Sentry error logging
- Serving static files
- Exception handling
- Logging to file and managing log files

## Demo

A demo of the backend can be found at https://news-java.belligerator.cz/api/. The demo is running on a Docker container.

For that purpose I have created a docker image for the backend. The image can be found at https://hub.docker.com/r/belligerator/news-backend-java. The image is built using the Dockerfile in the root directory of this repository.

On the server, there is docker-compose file that is used to run the backend and MySQL database. The file can be found at https://github.com/Belligerator/news-docker.

### Mobile application

Mobile application is built using Flutter (v3.7.1) and source codes can be found at https://github.com/Belligerator/news-app. It is connected to the demo backend and can be downloaded at https://belligerator.cz/downloads/ (`news-<version>.apk`). It is available for Android only.

## Getting Started

### Prerequisites

For running the backend locally, you will need the following:

- [Java](https://www.oracle.com/java/technologies/downloads/#java11) (v11)
- [MySQL](https://www.mysql.com/) (v5.7)
- [Docker](https://www.docker.com/) (v20) - not required, it was used for creating the image and running the demo from the server.

### Installation

1. Clone the repo [news-backend-java](https://github.com/Belligerator/news-backend-java)
   ```sh
   git clone git@github.com:Belligerator/news-backend-java.git
    ```

2. Install all dependencies
    ```sh
    ./mvnw dependency:resolve
    ```

3. Add environment variables on your machine or Configuration in IntelliJ IDEA. You will need these variables:

- MYSQL_HOST=localhost
- MYSQL_DATABASE=news
- MYSQL_USER=user
- MYSQL_PASSWORD=<password-to-db>
- SENTRY_DSN=<sentry-dsn>
- SERVER_URL=http://localhost:3000
- JWT_SECRET=<jwt-secret>
- BACKEND_API_KEY=<backend-api-key>
- BACKEND_API_SECRET=<backend-api-secret>


4. Create database schema `news` in database. In application.properties, there is a property `spring.jpa.hibernate.ddl-auto` set to `none`. Set it to `create-drop` and run the app. This will create the schema and tables in the database. After that, set the property back to `none`.


5. Run the app
    ```sh
    ./mvnw spring-boot:run
    ```
   The app should be running on `http://localhost:3000/`. Or you can run it from IntelliJ IDEA directly. Port is set to 3000, so it could be same as in NestJS example server. However, you can change default port in `application.properties`, variable `server.port`. For example, you can set it to 8080 (default Tomcat server port) and then the app will be running on `http://localhost:8080/`.


6. In the root directory, there is a `news.postman_collection.json` file that contains a Postman collection with sample requests. You can import it into Postman and use it to test the endpoints.

## Documentation

Documentation for the endpoints can be found at https://news.belligerator.cz/api/swagger.
