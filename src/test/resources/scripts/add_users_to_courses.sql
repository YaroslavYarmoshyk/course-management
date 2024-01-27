INSERT INTO "user" (id, first_name, last_name, email, "password", phone, status)
VALUES (1, 'John', 'Smith', 'john-smith@gmail.com', '$2a$10$5QZzwN9B/XXH1P7lfYsKvOwY6X/lwwjM0w4q0817m3if57EHepBWG', '+380974309331', 'A'),
       (2, 'Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$mRT9wLAgSvgGOVgRY/upAufIksU0BMc7RLEpxjyH94.1tqwXP6ap2', '+380971668744', 'A'),
       (3, 'Tyrion', 'Lannister', 'goldlannister@gmail.com', '$2a$10$kli/cvW93SWTkaCZSz.CQ.1XXDKreExXMWhf44dFrr.RT3n2RjtlO', '+380971205151', 'A'),
       (4, 'Maria', 'Montesory', 'maria-montesory@gmail.com', '$2a$10$1J15vwdmXwI/8PPwmt4Fke0ScmVRnU6DtmgPJsgWY5giLnr9CybZC', '+380971203332', 'A'),
       (5, 'Thor', 'Odinson', 'thor-odinson96@gmail.com', '$2a$10$i2q37ki4R4RQZuGfzvudP..DcKLQIjLx2UAVKyCo..jhIzuMS6xPO', '+380971204444', 'A')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (1, 3)
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO course (code, subject, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming')
ON CONFLICT (code) DO NOTHING;

INSERT INTO user_course (user_id, course_code, status, enrollment_date)
VALUES (2, 22324, 'S', NOW()),
       (3, 34432, 'S', NOW()),
       (3, 56548, 'S', NOW()),
       (2, 76552, 'S', NOW()),
       (2, 56548, 'S', NOW()),
       (5, 56548, 'S', NOW())
ON CONFLICT (user_id, course_code) DO NOTHING;
