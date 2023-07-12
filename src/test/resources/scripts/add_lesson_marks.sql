INSERT INTO lesson_mark (mark, mark_submission_date, lesson_id, student_id, instructor_id)
VALUES (5, NOW(), 1, 3, 2),
       (4, NOW(), 2, 3, 2),
       (5, NOW(), 2, 3, 2)
ON CONFLICT DO NOTHING;
