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

INSERT INTO user_course (user_id, course_code, status, enrollment_date)
VALUES (2, 22324, 'S', NOW()),
       (3, 22324, 'S', NOW()),
       (3, 34432, 'S', NOW()),
       (3, 56548, 'S', NOW()),
       (2, 76552, 'S', NOW()),
       (2, 56548, 'S', NOW()),
       (4, 99831, 'S', NOW()),
       (5, 56548, 'S', NOW())
ON CONFLICT (user_id, course_code) DO NOTHING;

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

INSERT INTO file (id, file_name, file_type, file_content)
VALUES (3, 'Lesson - Data Structures and Algorithms', 1, 'Dear Students,

Congratulations on completing our lesson on "Data Structures and Algorithms"! To reinforce your understanding of data structures and algorithmic concepts, please complete the following homework assignment. Take your time to work through the problems and provide clear and concise solutions.

Data Structures:
a) Implement a linked list data structure in your preferred programming language. Include operations for insertion, deletion, and traversal.
b) Design and implement a stack data structure using an array or a linked list. Demonstrate the stack''s functionality by solving a classic problem, such as checking balanced parentheses.
c) Create a binary search tree (BST) and implement the necessary operations, including insertion, deletion, and searching. Test the BST using different inputs and analyze its time complexity.

Algorithms:
a) Implement a sorting algorithm, such as bubble sort, selection sort, or insertion sort. Compare the algorithm''s time complexity with other sorting algorithms and evaluate its performance.
b) Solve the classic problem of finding the maximum subarray sum using an efficient algorithm, such as Kadane''s algorithm. Analyze the algorithm''s time complexity and provide an explanation of how it works.
c) Implement a graph traversal algorithm, such as breadth-first search (BFS) or depth-first search (DFS). Apply the algorithm to a given graph and demonstrate its traversal order.

Algorithm Analysis:
a) Analyze the time and space complexity of an algorithm you have implemented or encountered. Provide a step-by-step breakdown of the algorithm and derive its Big O notation.
b) Compare the performance of different search algorithms, such as linear search, binary search, and hash-based search. Consider various scenarios and analyze their time complexity.
c) Investigate and explain the concept of algorithmic efficiency. Discuss the trade-offs between time complexity and space complexity, and provide examples of algorithms that exemplify these trade-offs.

Problem-Solving:
a) Solve a classic algorithmic problem, such as finding the nth Fibonacci number or calculating the factorial of a number, using a recursive approach. Analyze the time complexity of your solution.
b) Solve a problem using dynamic programming. Provide a step-by-step breakdown of the problem and explain how the dynamic programming approach improves the algorithm''s efficiency.
c) Implement a classic data structure, such as a priority queue or a hash table, and use it to solve a problem that requires efficient data management and retrieval.

Please provide your code implementations, analysis, and explanations for the programming tasks. For the theoretical questions, provide well-reasoned answers and explanations.

Please submit your completed homework at the beginning of our next class. If you have any questions or need assistance, don''t hesitate to reach out to me.

Good luck with your data structures and algorithms tasks!')
ON CONFLICT (id) DO NOTHING;

INSERT INTO lesson_content (id, lesson_part, file_id, lesson_id)
VALUES (1, 4, 3, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO lesson_mark (id, mark, mark_submission_date, lesson_id, student_id, instructor_id)
VALUES (1, 5, NOW(), 1, 3, 2),
       (2, 4, NOW(), 2, 3, 2),
       (3, 5, NOW(), 2, 3, 2),
       (4, 5, NOW(), 3, 3, 2),
       (5, 5, NOW(), 4, 3, 2),
       (6, 4, NOW(), 5, 3, 2),
       (7, 4, NOW(), 6, 3, 2),
       (8, 4, NOW(), 7, 3, 2),
       (9, 4, NOW(), 8, 3, 2),
       (10, 4, NOW(), 9, 3, 2),
       (11, 4, NOW(), 10, 3, 2),
       (12, 4, NOW(), 16, 3, 2),
       (13, 4, NOW(), 17, 3, 2),
       (14, 4, NOW(), 18, 3, 2),
       (15, 4, NOW(), 19, 3, 2),
       (16, 3, NOW(), 20, 3, 2),
       (17, 5, NOW(), 11, 4, 2),
       (18, 5, NOW(), 12, 4, 2),
       (19, 5, NOW(), 11, 4, 2),
       (20, 5, NOW(), 13, 4, 2),
       (21, 5, NOW(), 14, 4, 2)
ON CONFLICT DO NOTHING;

INSERT INTO course_feedback (id, feedback, feedback_submission_date, course_code, student_id, instructor_id)
VALUES (1, 'Good job!', NOW(), 22324, 3, 2),
       (2, 'Awesome job actually!', NOW(), 22324, 3, 2)
ON CONFLICT DO NOTHING;
