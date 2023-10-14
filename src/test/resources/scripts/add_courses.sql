INSERT INTO course (code, subject, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming'),
       (65432, 'Biology', 'Introduction to Genetics and Evolution'),
       (34568, 'Economics', 'Microeconomics: Principles and Applications'),
       (67891, 'Environmental Science', 'Environmental Sustainability and Conservation')
ON CONFLICT (code) DO NOTHING;