📚 Library Management System

A simple Library Management System built with Spring Boot (backend) and JavaFX (frontend).
Users can add, view, update, delete, and search books in a library collection.

This project demonstrates full-stack development in Java, using Spring Boot REST APIs and a JavaFX desktop UI.


🧠 Features
✅ Backend (Spring Boot)

REST API for CRUD operations on books:

POST /api/books → Add a book

GET /api/books → Get all books (supports pagination)

PUT /api/books/{id} → Update a book

DELETE /api/books/{id} → Delete a book

GET /api/books/search?keyword= → Search by title or author

Uses Spring Data JPA with an H2 in-memory database

Automatically creates database schema on startup

CORS enabled for frontend access

✅ Frontend (JavaFX)

User-friendly interface built with JavaFX + FXML

Features:

📖 TableView to display books

📝 Add, Edit, and Delete books

🔍 Search books by title or author

🔄 Refresh and Pagination

Connects to backend using RestTemplate or HttpClient

Designed with Scene Builder


🧩 Tech Stack
Component	Technology
Frontend (UI)	JavaFX 21
Backend (API)	Spring Boot 3.3.0
Database	H2 (in-memory)
ORM	Spring Data JPA
Build Tool	Maven
Language	Java 17

library-management-system/
│
├── library-backend/              # Spring Boot backend
│   ├── src/main/java/com/example/library/
│   │   ├── model/                # Book entity
│   │   ├── repository/           # JPA Repository
│   │   ├── service/              # Business logic
│   │   ├── controller/           # REST API endpoints
│   │   └── LibraryBackendApplication.java
│   └── src/main/resources/
│       └── application.properties
│
├── library-frontend/             # JavaFX frontend
│   ├── src/main/java/com/example/libraryfrontend/
│   │   ├── controller/           # JavaFX controllers
│   │   ├── model/                # Book model
│   │   ├── service/              # HTTP service calls
│   │   └── MainApp.java          # Application entry point
│   ├── src/main/resources/
│   │   └── views/                # FXML UI files
│   └── pom.xml
│
└── README.md                     # This file


🧰 Prerequisites

Before running the application, make sure you have installed:

Java JDK 17+

Maven 3.8+

Scene Builder
 (optional for UI editing)

An IDE such as IntelliJ IDEA or VS Code with Java support


🚀 Running the Application
🖥️ Step 1: Run the Backend (Spring Boot)

Open the library-backend folder in your IDE or terminal.

Build the project:

mvn clean install

mvn spring-boot:run

Once started, the backend will run on:
http://localhost:8080

You can test the API endpoints using:
Postman

Browser

or curl commands like:
curl http://localhost:8080/api/books

Access the H2 database console:
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:librarydb
Username: sa
Password: (leave blank)


💻 Step 2: Run the Frontend (JavaFX)

Open the library-frontend folder in your IDE.

Ensure the backend is running (http://localhost:8080).

Build and run the JavaFX app:

mvn clean javafx:run
The JavaFX window will open, showing:

TableView of books

Text fields for Title, Author, ISBN, and Published Date

Buttons for Add, Update, Delete, Refresh, and Search

🔍 Bonus Features Implemented

✅ Search books by title or author (via /api/books/search)
✅ Pagination in backend and JavaFX TableView
✅ Built with Scene Builder for a polished UI

🧪 Example API Test (using curl)

Add a new book

curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/json" \
-d '{"title":"Effective Java","author":"Joshua Bloch","isbn":"9780134685991","publishedDate":"2018"}'

Get all books
curl http://localhost:8080/api/books

Update a book

curl -X PUT http://localhost:8080/api/books/1 \
-H "Content-Type: application/json" \
-d '{"title":"Clean Code","author":"Robert C. Martin","isbn":"9780132350884","publishedDate":"2008"}'

Delete a book
curl -X DELETE http://localhost:8080/api/books/1

🧑‍💻 Developer Notes
The backend auto-creates schema and data in memory (H2), so all records are cleared on restart.

You can switch to MySQL or PostgreSQL by editing application.properties.

To modify the UI layout, open the FXML file in Scene Builder.

