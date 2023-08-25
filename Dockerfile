FROM openjdk:11-jdk
WORKDIR /home/maven/project
COPY . .
ENV SERVER_URL=http://localhost:3000
ENV JWT_SECRET=jwt_secret_to_change
ENV BACKEND_API_KEY=api_username_to_change
ENV BACKEND_API_SECRET=api_secret_to_change
RUN sh -c './mvnw package'
EXPOSE 3000
ENTRYPOINT ["java","-jar","target/skoda-backend-0.1.0.jar"]
