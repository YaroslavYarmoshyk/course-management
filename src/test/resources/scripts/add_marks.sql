INSERT INTO "user" (id, first_name, last_name, email, "password", phone, status)
VALUES (1, 'John', 'Smith', 'john-smith@gmail.com', '$2a$10$5QZzwN9B/XXH1P7lfYsKvOwY6X/lwwjM0w4q0817m3if57EHepBWG', '+380974309331', 'A'),
       (2, 'Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$mRT9wLAgSvgGOVgRY/upAufIksU0BMc7RLEpxjyH94.1tqwXP6ap2', '+380971668744', 'A'),
       (3, 'Tyrion', 'Lannister', 'goldlannister@gmail.com', '$2a$10$kli/cvW93SWTkaCZSz.CQ.1XXDKreExXMWhf44dFrr.RT3n2RjtlO', '+380971205151', 'A'),
       (4, 'Maria', 'Montesory', 'maria-montesory@gmail.com', '$2a$10$1J15vwdmXwI/8PPwmt4Fke0ScmVRnU6DtmgPJsgWY5giLnr9CybZC', '+380971203332', 'A'),
       (5, 'Thor', 'Odinson', 'thor-odinson96@gmail.com', '$2a$10$i2q37ki4R4RQZuGfzvudP..DcKLQIjLx2UAVKyCo..jhIzuMS6xPO', '+380971204444', 'A')
ON CONFLICT (email) DO NOTHING;

INSERT INTO course (code, subject, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming')
ON CONFLICT (code) DO NOTHING;

INSERT INTO lesson (id, course_code, title, description)
VALUES (1, 22324, 'Introduction to Algebra', 'Basic concepts and operations in algebra'),
       (2, 22324, 'Geometry Fundamentals', 'Fundamentals of geometric shapes and properties'),
       (3, 22324, 'Calculus Basics', 'Introduction to differentiation and integration'),
       (4, 22324, 'Statistics Fundamentals', 'Basic concepts in statistics and data analysis'),
       (5, 22324, 'Probability Theory', 'Introduction to probability theory and applications'),
       (6, 34432, 'Ancient Civilizations', 'Study of ancient cultures and civilizations'),
       (7, 34432, 'Middle Ages and Renaissance', 'Examination of the Middle Ages and the Renaissance period'),
       (8, 34432, 'Age of Exploration', 'Exploration and expansion of European powers'),
       (9, 34432, 'Colonialism and Imperialism', 'Impact of colonialism and imperialism on global history'),
       (10, 34432, 'World Wars and Contemporary History', 'Study of major world wars and contemporary events'),
       (11, 99831, 'Introduction to Poetry', 'Exploring different forms and techniques in poetry'),
       (12, 99831, 'Shakespearean Plays', 'Analysis and interpretation of selected plays by William Shakespeare'),
       (13, 99831, 'Modern Literature', 'Examination of influential works in modern literature'),
       (14, 99831, 'Literary Criticism', 'Introduction to critical analysis and interpretation of literary works'),
       (15, 99831, 'Comparative Literature', 'Study of literary works from different cultures and traditions'),
       (16, 56548, 'Mechanics', 'Study of motion, forces, and energy'),
       (17, 56548, 'Thermodynamics', 'Concepts of heat, temperature, and energy transfer'),
       (18, 56548, 'Electromagnetism', 'Understanding electricity, magnetism, and their interactions'),
       (19, 56548, 'Optics', 'Study of light, reflection, refraction, and optics phenomena'),
       (20, 56548, 'Quantum Mechanics', 'Introduction to quantum physics and its principles'),
       (21, 56548, 'Relativity Theory', 'Concepts of special and general relativity'),
       (22, 76552, 'Artificial Intelligence', 'Exploring algorithms and techniques for AI applications'),
       (23, 76552, 'Web Development', 'Building dynamic websites using HTML, CSS, and JavaScript'),
       (24, 76552, 'Database Systems', 'Understanding relational databases and SQL'),
       (25, 76552, 'Cyber-security', 'Introduction to computer security and protection against cyber threats'),
       (26, 76552, 'Data Structures and Algorithms', 'Understanding fundamental data structures and algorithm design')
ON CONFLICT (id) DO NOTHING;

INSERT INTO lesson_mark (id, mark, mark_submission_date, lesson_id, student_id, instructor_id)
VALUES (1, 5, NOW(), 1, 3, 2),
       (2, 4, NOW(), 2, 3, 2),
       (3, 5, NOW(), 2, 3, 2)
ON CONFLICT DO NOTHING;
