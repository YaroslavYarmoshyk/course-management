INSERT INTO user_course (user_id, course_code, status, enrollment_date)
VALUES (2, 22324, 'S', NOW()),
       (3, 34432, 'S', NOW()),
       (3, 56548, 'S', NOW()),
       (2, 76552, 'S', NOW())
ON CONFLICT (user_id, course_code) DO NOTHING;
