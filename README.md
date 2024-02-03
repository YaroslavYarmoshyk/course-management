# Course management system

Welcome to the Online Learning Management System README! This application is designed to provide a seamless and efficient platform for managing courses, instructors, and students. Below are the key functionalities and features of the system.


## Features
- User Registration: Users can register in the system with a unique username and password.
- Authentication and Authorization: Secure authentication and authorization mechanisms to control access based on user roles.
- User Roles: Three roles are supported - Admin, Instructor, and Student.
- Admin Management: Admin users have the ability to manage all information in the system.
- Role Assignment: Admin can assign roles to new users.
- Course Management: Admin can assign instructors to courses, ensuring that each course has at least one instructor.
- Student Enrollment: Students can enroll in up to 5 courses simultaneously.
- Lesson Structure: Each course comprises a minimum of 5 lessons.
- Homework Submission: Students can upload text files for homework.
- Grading System: Instructors can assign marks for each lesson and provide final feedback for the course.
- Instructor Dashboard: Instructors can view a list of their courses, as well as a list of students per course.
- Student Dashboard: Students can see a list of their enrolled courses, along with details about lessons and grades.
- Passing Criteria: To pass a course, a student must achieve a minimum of 80% for the final grade.

## Roles
- **Admin**
  - ***Predefined admin user with full system management rights.***
- **Instructor**
  - ***Manages courses, assigns grades, and provides feedback.***
  - ***Views the list of their courses and students per course.***
- **Student**
  - ***Enrolls in courses, submits homework, and checks grades.***
  - ***Views the list of their courses and lessons per course.***

## Endpoints
### Authentication
- **Register User**
  - Endpoint: [POST] **_/api/v1/authentication/register_**
  - Description: Allows users to register in the system
  - Request Body:
```json
{
  "firstName": "Your First Name",
  "lastName": "Your Last Name",
  "email": "your-email@gmail.com",
  "phone": "+380974323051",
  "password": "glory-to-Ukr@ine"
}
```
- Response Body:
```json
{
  "token": "valid JWT token"
}
```
- **Confirm email**
  - Endpoint: [GET] **_/api/v1/authentication/confirm-email_**
  - Parameter: token - provided via email
  - Description: Allows users to activate their accounts
  - Response body: 
```json
{
  "id": 9,
  "firstName": "Your First Name",
  "lastName": "Your Last Name",
  "email": "your-email@gmail.com",
  "phone": "+380974323051",
  "status": "ACTIVE",
  "roles": []
}
```
- **Login:**
    - Endpoint: [GET] **_/api/v1/authentication/login_**
    - Description: Authenticates users and provides an access token
    - Request body:
```json
{
  "email": "your-email@gmail.com",
  "password": "your password"
}
```
- Response body:
```json
{
  "id": 9,
  "firstName": "Your First Name",
  "lastName": "Your Last Name",
  "email": "your-email@gmail.com",
  "phone": "+380974323051",
  "status": "ACTIVE",
  "roles": []
}
```
- **Reset password request**
  - Endpoint: [POST] **_/api/v1/reset-password/request_**
  - Description: Allows users to request password reset
  - Request body: "your-email@gmail.com"
  - Response body: "Reset password email request was successfully sent"
- **Reset password confirmation**
  - Endpoint: [GET] **_/api/v1/reset-password/confirm_**
  - Description: Confirm user reset password request
  - Parameter: token - provided via email
  - Response body: "Reset password request was successfully confirmed"
- **Reset password**
  - Endpoint: [POST] **_/api/v1/reset-password_**
  - Description: Allows users to reset their password
  - Request body:
```json
{
  "email": "your-email@gmail.com",
  "password": "your new password"
}
```
- Response body: 
```json
{
  "token": "valid JWT token"
}
```
- **Current user**
  - Endpoint: [GET] **_/api/v1/users/me_**
  - Description: Allows user to get actual information, including roles and status
  - Response body:
```json
{
  "id": 9,
  "firstName": "Your First Name",
  "lastName": "Your Last Name",
  "email": "your-email@gmail.com",
  "phone": "+380974323051",
  "status": "ACTIVE",
  "roles": []
}
```
### Admin Operations
- **Get all users**
  - Endpoint: [GET] **_/api/v1/admin/users_**
  - Authorization: Bearer admin token
  - Description: Allows admin to list all users in the system
  - Response body:
```json
[
    {
        "id": 3,
        "firstName": "Maria",
        "lastName": "Montessori",
        "email": "maria-montesory@gmail.com",
        "phone": "+380973305151",
        "status": "ACTIVE",
        "roles": [
            "INSTRUCTOR"
        ]
    },
    {
        "id": 10,
        "firstName": "Yaroslav",
        "lastName": "Yarmoshyk",
        "email": "yarmoshyk9q@gmail.com",
        "phone": "+380974309051",
        "status": "INACTIVE",
        "roles": []
    },
    {
        "id": 4,
        "firstName": "Vivian",
        "lastName": "Paley",
        "email": "vivian-paley@yahoo.com",
        "phone": "+3809733445451",
        "status": "ACTIVE",
        "roles": [
            "INSTRUCTOR"
        ]
    },
    {
        "id": 9,
        "firstName": "Yaroslav",
        "lastName": "Yarmoshyk",
        "email": "yarmoshyk@gmail.com",
        "phone": "+380974239051",
        "status": "ACTIVE",
        "roles": [
            "STUDENT"
        ]
    },
    {
        "id": 2,
        "firstName": "Marry",
        "lastName": "Poppins",
        "email": "poppins@yahoo.com",
        "phone": "+380971668744",
        "status": "ACTIVE",
        "roles": [
            "INSTRUCTOR"
        ]
    },
    {
        "id": 1,
        "firstName": "John",
        "lastName": "Smith",
        "email": "john-smith@gmail.com",
        "phone": "+380974309331",
        "status": "ACTIVE",
        "roles": [
            "ADMIN"
        ]
    },
    {
        "id": 5,
        "firstName": "Tyrion",
        "lastName": "Lannister",
        "email": "goldlannister@gmail.com",
        "phone": "+380971205151",
        "status": "ACTIVE",
        "roles": [
            "STUDENT"
        ]
    }
]
```
- **Get user by ID**
  - Endpoint: [GET] **_/api/v1/admin/users/{user-id}_**
  - Authorization: Bearer admin token
  - Description: Allows admin to get information about particular user
  - Response body:
```json
{
    "id": 4,
    "firstName": "Vivian",
    "lastName": "Paley",
    "email": "vivian-paley@yahoo.com",
    "phone": "+3809733445451",
    "status": "ACTIVE",
    "roles": [
        "INSTRUCTOR"
    ]
}
```
- **Get user courses**
  - Endpoint: [GET] **_/api/v1/admin/users/{user-id}/courses_**
  - Authorization: Bearer admin token
  - Description: Allows admin to get user courses
  - Response body:
```json
[
    {
        "code": 99831,
        "subject": "Literature",
        "description": "Study of classical literature",
        "status": "STARTED"
    }
]
```
- **Get detailed user courses**
  - Endpoint: [GET] **_/api/v1/admin/users/{user-id}/courses/{course-code}_**
  - Authorization: Bearer admin token
  - Description: Allows admin to get detailed information about user course
  - Response body:
```json
{
    "courseCode": 99831,
    "subject": "Literature",
    "description": "Study of classical literature",
    "courseMark": {
        "lessonMarks": {},
        "markValue": null,
        "mark": null
    },
    "courseFeedback": [],
    "status": "STARTED",
    "enrollmentDate": "2024-01-26 19:32:02",
    "accomplishmentDate": null
}
```
- **Get user lessons per course**
  - Endpoint: [GET] **_/api/v1/admin/users/{user-id}/courses/{course-code}/lessons_**
  - Authorization: Bearer admin token
  - Description: Allows admin to get detailed information about user course
  - Response body:
```json
[
    {
        "lessonId": 11,
        "title": "Introduction to Poetry",
        "description": "Exploring different forms and techniques in poetry",
        "lessonContent": [
            {
                "id": 11,
                "lessonPart": "PRACTICE",
                "fileId": 11
            }
        ],
        "markValue": null,
        "mark": null
    },
    {
        "lessonId": 12,
        "title": "Shakespearean Plays",
        "description": "Analysis and interpretation of selected plays by William Shakespeare",
        "lessonContent": [
            {
                "id": 12,
                "lessonPart": "PRACTICE",
                "fileId": 12
            }
        ],
        "markValue": null,
        "mark": null
    },
    {
        "lessonId": 13,
        "title": "Modern Literature",
        "description": "Examination of influential works in modern literature",
        "lessonContent": [
            {
                "id": 13,
                "lessonPart": "PRACTICE",
                "fileId": 13
            }
        ],
        "markValue": null,
        "mark": null
    },
    {
        "lessonId": 14,
        "title": "Literary Criticism",
        "description": "Introduction to critical analysis and interpretation of literary works",
        "lessonContent": [
            {
                "id": 14,
                "lessonPart": "PRACTICE",
                "fileId": 14
            }
        ],
        "markValue": null,
        "mark": null
    },
    {
        "lessonId": 15,
        "title": "Comparative Literature",
        "description": "Study of literary works from different cultures and traditions",
        "lessonContent": [
            {
                "id": 15,
                "lessonPart": "PRACTICE",
                "fileId": 15
            }
        ],
        "markValue": null,
        "mark": null
    }
]
```
- **Assign role to user**
  - Endpoint: [POST] **_/api/v1/admin/assign-role_**
  - Authorization: Bearer admin token
  - Description: Allows admin to assign a role to user
  - Request body:
```json
{
  "userId": 9,
  "roles": ["STUDENT"]
}
```
  - Response body:
```json
{
    "id": 9,
    "firstName": "Yaroslav",
    "lastName": "Yarmoshyk",
    "email": "yarmoshyk@gmail.com",
    "phone": "+380974323051",
    "status": "ACTIVE",
    "roles": [
        "STUDENT"
    ]
}
```
- **Assign instructor to course**
    - Endpoint: [POST] **_/api/v1/course-management/assign-instructor_**
    - Authorization: Bearer admin token
    - Description: Allows admin to assign an instructor to a course
    - Request body:
```json
{
  "instructorId": 2,
  "courseCode": 22324
}
```
   - Response body:
```json
{
    "code": 22324,
    "subject": "Mathematics",
    "instructors": [
        {
            "id": 2,
            "firstName": "Marry",
            "lastName": "Poppins",
            "email": "poppins@yahoo.com",
            "phone": "+380971668744"
        }
    ],
    "students": []
}
```
- **Get user course final mark**
    - Endpoint: [GET] **_/api/v1/admin/users/{user-id}/courses/{course-code}/final-mark_**
    - Authorization: Bearer admin token
    - Description: Allows admin to get final user course mark
    - Response body:
```json
{
    "courseCode": 22324,
    "studentId": 5,
    "lessonMarks": {
        "1": 4.33,
        "2": 4.67,
        "3": 3.67,
        "4": 4.25,
        "5": 5.00
    },
    "markValue": 4.38,
    "mark": "ABOVE_AVERAGE"
}
```
### Instructor Operations
- **Get instructor courses**
    - Endpoint: [GET] **_/api/v1/courses
    - Authorization: Bearer instructor token
    - Description: Allows instructor to get their courses
    - Response body:
```json
[
    {
        "code": 76552,
        "subject": "Computer Science",
        "description": "Introduction to computer programming",
        "status": "STARTED"
    },
    {
        "code": 22324,
        "subject": "Mathematics",
        "description": "Introductory course on mathematics",
        "status": "STARTED"
    }
]
```
- **Get students per course**
    - Endpoint: [GET] **_/api/v1/courses/{course-code}/students_**
    - Authorization: Bearer instructor token
    - Description: Allows instructor to get their students
    - Response body:
```json
[
    {
        "id": 5,
        "firstName": "Tyrion",
        "lastName": "Lannister",
        "email": "goldlannister@gmail.com",
        "phone": "+380971205151"
    }
]
```
- **Mark student lesson**
    - Endpoint: [POST] **_/api/v1/lessons/assign-mark_**
    - Authorization: Bearer instructor token
    - Description: Allows instructor to mark student lesson
    - Request body:
```json
{
    "instructorId": 2,
    "studentId": 5,
    "lessonId": 5,
    "mark": "EXCELLENT"
}
```
   - Response body:
```json
{
    "student": {
        "id": 5,
        "firstName": "Tyrion",
        "lastName": "Lannister",
        "email": "goldlannister@gmail.com",
        "phone": "+380971205151"
    },
    "lesson": {
        "id": 5,
        "title": "Probability Theory",
        "description": "Introduction to probability theory and applications"
    },
    "instructor": {
        "id": 2,
        "firstName": "Marry",
        "lastName": "Poppins",
        "email": "poppins@yahoo.com",
        "phone": "+380971668744"
    },
    "mark": "EXCELLENT",
    "markSubmissionDate": "2024-02-03 18:48:20"
}
```
- **Provide feedback to student lesson**
    - Endpoint: [POST] **_/api/v1/course-management/provide-feedback_**
    - Authorization: Bearer instructor token
    - Description: Allows instructor to provide feedback to student lesson
    - Request body:
```json
{
    "instructorId": 2,
    "studentId": 5,
    "courseCode": 22324,
    "feedback": "Awesome job!"
}
```
   - Response body:
```json
{
    "student": {
        "id": 5,
        "firstName": "Tyrion",
        "lastName": "Lannister",
        "email": "goldlannister@gmail.com",
        "phone": "+380971205151"
    },
    "instructor": {
        "id": 2,
        "firstName": "Marry",
        "lastName": "Poppins",
        "email": "poppins@yahoo.com",
        "phone": "+380971668744"
    },
    "courseCode": 22324,
    "feedback": "Awesome job!",
    "feedbackSubmissionDate": "2024-02-03 18:49:51"
}
```
### Student Operations
- **Enroll in courses**
    - Endpoint: [POST] **_/api/v1/course-management/enrollments_**
    - Authorization: Bearer student token
    - Description: Allows student to enroll in courses
    - Request body:
```json
{
  "studentId": 5,
  "courseCodes": [
      22324,
      34432,
      56548
  ]
}
```
   - Response body:
```json
{
    "studentId": 5,
    "studentCourses": [
        {
            "code": 56548,
            "subject": "Physics",
            "description": "Fundamentals of physics",
            "status": "STARTED"
        },
        {
            "code": 22324,
            "subject": "Mathematics",
            "description": "Introductory course on mathematics",
            "status": "STARTED"
        },
        {
            "code": 34432,
            "subject": "History",
            "description": "Overview of world history",
            "status": "STARTED"
        }
    ]
}
```
- **Upload homework**
    - Endpoint: [POST] **_/api/v1/homework/upload_**
    - Authorization: Bearer student token
    - Description: Allows student to upload homework
    - Request parameters: file, lessonId
- **Get student courses**
    - Endpoint: [GET] api/v1/courses
    - Authorization: Bearer student token
    - Description: Allows student to list their courses
    - Response body:
```json
[
    {
        "code": 34432,
        "subject": "History",
        "description": "Overview of world history",
        "status": "STARTED"
    },
    {
        "code": 22324,
        "subject": "Mathematics",
        "description": "Introductory course on mathematics",
        "status": "STARTED"
    },
    {
        "code": 56548,
        "subject": "Physics",
        "description": "Fundamentals of physics",
        "status": "STARTED"
    }
]
```
- **Get student course information**
    - Endpoint: [GET] **_/api/v1/courses/{course-code}_**
    - Authorization: Bearer student token
    - Description: Allows student to get detailed information about course
    - Response body:
```json
{
  "courseCode": 22324,
  "subject": "Mathematics",
  "description": "Introductory course on mathematics",
  "courseMark": {
    "lessonMarks": {
      "1": 4.33,
      "2": 4.67,
      "3": 3.67,
      "4": 4.25,
      "5": 5.00
    },
    "markValue": 4.38,
    "mark": "ABOVE_AVERAGE"
  },
  "courseFeedback": [],
  "status": "COMPLETED",
  "enrollmentDate": "2024-02-03 18:52:53",
  "accomplishmentDate": "2024-02-03 19:01:15"
}
```
- **Get student lessons**
    - Endpoint: [GET] **_/api/v1/courses/{course-code}/lessons_**
    - Authorization: Bearer student token
    - Description: Allows student to get their lessons
    - Response body:
```json
[
  {
    "lessonId": 1,
    "title": "Introduction to Algebra",
    "description": "Basic concepts and operations in algebra",
    "lessonContent": [
      {
        "id": 1,
        "lessonPart": "PRACTICE",
        "fileId": 1
      }
    ],
    "markValue": 4.33,
    "mark": "ABOVE_AVERAGE"
  },
  {
    "lessonId": 2,
    "title": "Geometry Fundamentals",
    "description": "Fundamentals of geometric shapes and properties",
    "lessonContent": [
      {
        "id": 2,
        "lessonPart": "PRACTICE",
        "fileId": 2
      }
    ],
    "markValue": 4.67,
    "mark": "EXCELLENT"
  },
  {
    "lessonId": 3,
    "title": "Calculus Basics",
    "description": "Introduction to differentiation and integration",
    "lessonContent": [
      {
        "id": 3,
        "lessonPart": "PRACTICE",
        "fileId": 3
      }
    ],
    "markValue": 3.67,
    "mark": "ABOVE_AVERAGE"
  },
  {
    "lessonId": 4,
    "title": "Statistics Fundamentals",
    "description": "Basic concepts in statistics and data analysis",
    "lessonContent": [
      {
        "id": 4,
        "lessonPart": "PRACTICE",
        "fileId": 4
      }
    ],
    "markValue": 4.25,
    "mark": "ABOVE_AVERAGE"
  },
  {
    "lessonId": 5,
    "title": "Probability Theory",
    "description": "Introduction to probability theory and applications",
    "lessonContent": [
      {
        "id": 5,
        "lessonPart": "PRACTICE",
        "fileId": 5
      }
    ],
    "markValue": 5.00,
    "mark": "EXCELLENT"
  }
]
```
- **Download homework file**
    - Endpoint: [GET] **_/api/v1/homework/download/{file-id}_**
    - Authorization: Bearer student token
    - Description: Allows student to download their homework
- **Complete student course**
    - Endpoint: [POST] **_/api/v1/course-management/complete_**
    - Authorization: Bearer student token
    - Description: Allows student to complete a course, if all lessons are graded and final mark is above 80% from max grade
    - Request body:
```json
{
    "studentId": 5,
    "courseCode": 22324
}
```
   - Response body:
```json
{
    "courseCode": 22324,
    "subject": "Mathematics",
    "description": "Introductory course on mathematics",
    "courseMark": {
        "lessonMarks": {
            "1": 4.33,
            "2": 4.67,
            "3": 3.67,
            "4": 4.25,
            "5": 5.00
        },
        "markValue": 4.38,
        "mark": "ABOVE_AVERAGE"
    },
    "courseFeedback": [],
    "status": "COMPLETED",
    "enrollmentDate": "2024-02-03 18:52:53",
    "accomplishmentDate": "2024-02-03 19:01:15"
}
```
- **Get final student course mark**
    - Endpoint: [GET] **_/api/v1/courses/22324/final-mark_**
    - Authorization: Bearer student token
    - Description: Allows student to get final course mark
    - Response body:
```json
{
    "courseCode": 22324,
    "studentId": 5,
    "lessonMarks": {
        "1": 4.33,
        "2": 4.67,
        "3": 3.67,
        "4": 4.25,
        "5": 5.00
    },
    "markValue": 4.38,
    "mark": "ABOVE_AVERAGE"
}
```
Technologies Used
Java
Spring Boot
Spring Security
JSON Web Tokens (JWT)
Spring Data JPA
PostgreSQL (or your preferred database)
Maven
Docker

## Pre-defined users
- **Admin**
  - First Name: John
  - Last Name: Smith
  - Email: john-smith@gmail.com
  - Password: passw@rd-1
  - Phone: +380974309331
- **Instructor**
    - First Name: Mary
    - Last Name: Poppins
    - Email: poppins@yahoo.com
    - Password: passw@rd-2
    - Phone: +380971668744
- **Student**
    - First Name: Tyrion
    - Last Name: Lannister
    - Email: goldlannister@gmail.com
    - Password: passw@rd-3
    - Phone: +380971205151

## Usage
1. Clone the repository: git clone https://github.com/YaroslavYarmoshyk/course-management.git
2. Run docker compose: docker compose up -d
3. The application is available by address: http://localhost:8080
