Spring Security with JWT Authentication
This project demonstrates the implementation of authentication and authorization using JSON Web Tokens (JWT) in a Spring Boot application with Spring Security.

Features
Secure the application endpoints based on user roles and permissions.
Authenticate users using JWT tokens.
Support stateless token-based authentication.
Generate JWT tokens upon successful login.
Validate and extract user information from JWT tokens during API requests.
Handle unauthorized and forbidden access with appropriate error responses.
Provide role-based access control to secure specific endpoints.
Implement token refresh functionality to extend token validity.

Technologies Used
Java
Spring Boot
Spring Security
JSON Web Tokens (JWT)
Spring Data JPA
PostgreSQL (or your preferred database)
Maven
Docker

Getting Started
Clone the repository: git clone https://github.com/YaroslavYarmoshyk/course-management.git
Run docker compose: docker compose up -d

API Endpoints
The application provides the following API endpoints for authentication and authorization:

/api/verification/register - User registration endpoint.
/api/verification/authenticate - User login endpoint to obtain JWT token.

User Information
To test the application and access its protected resources, you can use the following sample user accounts:

John Smith
Email: john-smith@gmail.com
Password: passw@rd-1
Phone: +380974309331

Mary Poppins
Email: poppins@yahoo.com
Password: passw@rd-2
Phone: +380971668744

Tyrion Lannister
Email: goldlannister@gmail.com
Password: passw@rd-3
Phone: +380971205151