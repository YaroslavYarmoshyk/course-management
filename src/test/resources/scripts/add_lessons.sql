INSERT INTO "user" (id, first_name, last_name, email, "password", phone, status)
VALUES (1, 'John', 'Smith', 'john-smith@gmail.com', '$2a$10$2DXHDU2uYMrlIiRHVb2CletM6kMjhLmvHzgyfOHQlV2SrLzE17hSK',
        '+380974309331', 'A'),
       (2, 'Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$Zd/kQRuvnJ2br8dN5OyWt.LskBDAHL1UpZoRdbdOz7QcmNPJYJdYe',
        '+380971668744', 'A'),
       (3, 'Tyrion', 'Lannister', 'goldlannister@gmail.com',
        '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m', '+380971205151',
        'A')
ON CONFLICT (email) DO NOTHING;

INSERT INTO course (code, subject, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming')
ON CONFLICT (code) DO NOTHING;

INSERT INTO user_course (user_id, course_code, status, enrollment_date)
VALUES (2, 22324, 'S', NOW()),
       (3, 22324, 'S', NOW()),
       (3, 34432, 'S', NOW()),
       (3, 56548, 'S', NOW()),
       (2, 76552, 'S', NOW())
ON CONFLICT (user_id, course_code) DO NOTHING;

INSERT INTO lesson (id, course_code, title, description)
VALUES (1, 22324, 'Introduction to Algebra', 'Basic concepts and operations in algebra'),
       (2, 22324, 'Geometry Fundamentals', 'Fundamentals of geometric shapes and properties'),
       (2, 22324, 'Calculus Basics', 'Introduction to differentiation and integration'),
       (3, 22324, 'Statistics Fundamentals', 'Basic concepts in statistics and data analysis'),
       (4, 22324, 'Probability Theory', 'Introduction to probability theory and applications'),
       (5, 34432, 'Ancient Civilizations', 'Study of ancient cultures and civilizations'),
       (6, 34432, 'Middle Ages and Renaissance', 'Examination of the Middle Ages and the Renaissance period'),
       (7, 34432, 'Age of Exploration', 'Exploration and expansion of European powers'),
       (8, 34432, 'Colonialism and Imperialism', 'Impact of colonialism and imperialism on global history'),
       (9, 34432, 'World Wars and Contemporary History', 'Study of major world wars and contemporary events'),
       (10, 99831, 'Introduction to Poetry', 'Exploring different forms and techniques in poetry'),
       (11, 99831, 'Shakespearean Plays', 'Analysis and interpretation of selected plays by William Shakespeare'),
       (12, 99831, 'Modern Literature', 'Examination of influential works in modern literature'),
       (13, 99831, 'Literary Criticism', 'Introduction to critical analysis and interpretation of literary works'),
       (14, 99831, 'Comparative Literature', 'Study of literary works from different cultures and traditions'),
       (15, 56548, 'Mechanics', 'Study of motion, forces, and energy'),
       (16, 56548, 'Thermodynamics', 'Concepts of heat, temperature, and energy transfer'),
       (17, 56548, 'Electromagnetism', 'Understanding electricity, magnetism, and their interactions'),
       (18, 56548, 'Optics', 'Study of light, reflection, refraction, and optics phenomena'),
       (19, 56548, 'Quantum Mechanics', 'Introduction to quantum physics and its principles'),
       (20, 56548, 'Relativity Theory', 'Concepts of special and general relativity'),
       (21, 76552, 'Artificial Intelligence', 'Exploring algorithms and techniques for AI applications'),
       (22, 76552, 'Web Development', 'Building dynamic websites using HTML, CSS, and JavaScript'),
       (23, 76552, 'Database Systems', 'Understanding relational databases and SQL'),
       (24, 76552, 'Cyber-security', 'Introduction to computer security and protection against cyber threats'),
       (25, 76552, 'Data Structures and Algorithms',
        'Understanding fundamental data structures and algorithm design')
ON CONFLICT (id) DO NOTHING;
