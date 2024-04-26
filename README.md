Chat Server System

Overview

This chat server system enables users to join a chat room, send messages, and access chat history. This solution focuses on server-side development, utilizing WebSocket for real-time communication and a RESTful API for message handling.

Features

User authentication (basic username/password).
Persistent storage of chat messages in a database.
Real-time and RESTful communication for sending and receiving messages.
WebSocket support for real-time interaction.
Technologies Used

Spring Boot
Spring Security
Spring Data JPA
WebSocket
H2 Database
Maven
Setup and Installation

Prerequisites

JDK 1.8 or later
Maven 3.2+
Steps to Setup

Clone the repository
git clone [your-repository-url]
cd [repository-name]
Build the application
mvn clean install
Run the application
mvn spring-boot:run
The application starts running at http://localhost:8080.

Testing

Using Postman Use Postman to interact with the server. Import the Postman collection provided in the repository to test various endpoints.

Using the Provided HTML Page After starting the application, open the loginAndChat.html page located in the src/main/resources folder in a web browser to test the login and chat functionality. This page allows you to connect to the WebSocket, authenticate, send messages, and view chat history in real-time.

API Endpoints

POST /auth/login - Authenticate users.
POST /chat/send - Send a message to the chat room.
GET /chat/history - Retrieve chat history.
WebSocket

Connect to WebSocket at ws://localhost:8080/ws.
Subscribe to /topic/public to receive messages.
Send messages via /app/chat.send.
Database

Access the in-memory H2 database console at http://localhost:8080/h2-console.
JDBC URL: jdbc:h2:mem:testdb
Username: sa
password: password
