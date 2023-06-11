INSERT INTO "role" (role_name)
VALUES ('ADMIN'),
       ('INSTRUCTOR'),
       ('STUDENT');

INSERT INTO "user" (first_name, last_name, email, "password", phone, status)
VALUES ('John', 'Smith', 'john-smith@gmail.com', '$2a$10$2DXHDU2uYMrlIiRHVb2CletM6kMjhLmvHzgyfOHQlV2SrLzE17hSK',
        '+380974309331', 'A'),
       ('Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$Zd/kQRuvnJ2br8dN5OyWt.LskBDAHL1UpZoRdbdOz7QcmNPJYJdYe',
        '+380971668744', 'A'),
       ('Maria', 'Montessori', 'maria-montesory@gmail.com',
        '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m', '+380973305151', 'A'),
       ('Vivian', 'Paley', 'vivian-paley@yahoo.com', '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m',
        '+3809733445451', 'A'),
       ('Tyrion', 'Lannister', 'goldlannister@gmail.com',
        '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m', '+380971205151', 'A');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 3);

INSERT INTO course (code, title, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming');

INSERT INTO user_course (user_id, course_code, status)
VALUES (2, 22324, 'S'),
       (3, 34432, 'S'),
       (4, 99831, 'S'),
       (3, 56548, 'S'),
       (2, 76552, 'S');

INSERT INTO lesson (course_code, title, description, credits)
VALUES (22324, 'Introduction to Algebra', 'Basic concepts and operations in algebra', 3),
       (22324, 'Geometry Fundamentals', 'Fundamentals of geometric shapes and properties', 2.5),
       (22324, 'Calculus Basics', 'Introduction to differentiation and integration', 4),
       (22324, 'Statistics Fundamentals', 'Basic concepts in statistics and data analysis', 2.5),
       (22324, 'Probability Theory', 'Introduction to probability theory and applications', 3.5),
       (34432, 'Ancient Civilizations', 'Study of ancient cultures and civilizations', 3.5),
       (34432, 'Middle Ages and Renaissance', 'Examination of the Middle Ages and the Renaissance period', 4),
       (34432, 'Age of Exploration', 'Exploration and expansion of European powers', 2),
       (34432, 'Colonialism and Imperialism', 'Impact of colonialism and imperialism on global history', 3.5),
       (34432, 'World Wars and Contemporary History', 'Study of major world wars and contemporary events', 5),
       (99831, 'Introduction to Poetry', 'Exploring different forms and techniques in poetry', 2.5),
       (99831, 'Shakespearean Plays', 'Analysis and interpretation of selected plays by William Shakespeare', 3),
       (99831, 'Modern Literature', 'Examination of influential works in modern literature', 2.5),
       (99831, 'Literary Criticism', 'Introduction to critical analysis and interpretation of literary works', 3),
       (99831, 'Comparative Literature', 'Study of literary works from different cultures and traditions', 2.5),
       (56548, 'Mechanics', 'Study of motion, forces, and energy', 3.5),
       (56548, 'Thermodynamics', 'Concepts of heat, temperature, and energy transfer', 3),
       (56548, 'Electromagnetism', 'Understanding electricity, magnetism, and their interactions', 4),
       (56548, 'Optics', 'Study of light, reflection, refraction, and optics phenomena', 3.5),
       (56548, 'Quantum Mechanics', 'Introduction to quantum physics and its principles', 2),
       (56548, 'Relativity Theory', 'Concepts of special and general relativity', 2),
       (76552, 'Artificial Intelligence', 'Exploring algorithms and techniques for AI applications', 4),
       (76552, 'Web Development', 'Building dynamic websites using HTML, CSS, and JavaScript', 3.5),
       (76552, 'Database Systems', 'Understanding relational databases and SQL', 3.5),
       (76552, 'Cyber-security', 'Introduction to computer security and protection against cyber threats', 1.5),
       (76552, 'Data Structures and Algorithms', 'Understanding fundamental data structures and algorithm design', 2.5);