CREATE TABLE IF NOT EXISTS role
(
    id        BIGSERIAL PRIMARY KEY,
    role_name TEXT UNIQUE NOT NULL CHECK (role_name = 'ADMIN' OR role_name = 'INSTRUCTOR' OR role_name = 'STUDENT')
);

CREATE TABLE IF NOT EXISTS "user"
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT,
    last_name  TEXT,
    email      TEXT UNIQUE NOT NULL,
    "password" TEXT,
    phone      TEXT,
    status     TEXT        NOT NULL CHECK (status = 'A' OR status = 'I')
);

CREATE TABLE IF NOT EXISTS confirmation_token
(
    id              BIGSERIAL PRIMARY KEY,
    "type"          INT,
    token           TEXT,
    expiration_date TIMESTAMP(0),
    status          TEXT   NOT NULL CHECK (status = 'A' OR status = 'N'),
    user_id         BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS course
(
    code        BIGINT PRIMARY KEY,
    subject     TEXT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS lesson
(
    id          BIGSERIAL PRIMARY KEY,
    title       TEXT   NOT NULL,
    description TEXT,
    course_code BIGINT NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course (code)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE IF NOT EXISTS user_course
(
    user_id             BIGINT NOT NULL,
    course_code         BIGINT NOT NULL,
    status              TEXT,
    enrollment_date     TIMESTAMP(0),
    accomplishment_date TIMESTAMP(0),
    PRIMARY KEY (user_id, course_code),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (course_code) REFERENCES course (code),
    UNIQUE (user_id, course_code)
);

CREATE TABLE IF NOT EXISTS course_feedback
(
    id            BIGSERIAL PRIMARY KEY,
    feedback      TEXT,
    feedback_submission_date TIMESTAMP(0),
    course_code   BIGINT NOT NULL,
    student_id    BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course (code),
    FOREIGN KEY (student_id) REFERENCES "user" (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS lesson_mark
(
    id                   BIGSERIAL PRIMARY KEY,
    mark                 NUMERIC(38, 2) CHECK (lesson_mark.mark >= 0 AND lesson_mark.mark <= 5),
    mark_submission_date TIMESTAMP(0),
    lesson_id            BIGINT NOT NULL,
    student_id           BIGINT NOT NULL,
    instructor_id        BIGINT NOT NULL,
    FOREIGN KEY (lesson_id) REFERENCES lesson (id),
    FOREIGN KEY (student_id) REFERENCES "user" (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS file
(
    id           BIGSERIAL PRIMARY KEY,
    file_name    TEXT  NOT NULL,
    file_type    INT   NOT NULL,
    file_content BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS homework
(
    id            BIGSERIAL PRIMARY KEY,
    uploaded_date TIMESTAMP(0),
    file_id       BIGINT NOT NULL,
    lesson_id     BIGINT NOT NULL,
    student_id    BIGINT NOT NULL,
    FOREIGN KEY (file_id) REFERENCES file (id),
    FOREIGN KEY (lesson_id) REFERENCES lesson (id),
    FOREIGN KEY (student_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS lesson_content
(
    id          BIGSERIAL PRIMARY KEY,
    lesson_part INT,
    file_id     BIGINT NOT NULL,
    lesson_id   BIGINT NOT NULL,
    FOREIGN KEY (file_id) REFERENCES file (id),
    FOREIGN KEY (lesson_id) REFERENCES lesson (id)
);

INSERT INTO "role" (role_name)
VALUES ('ADMIN'),
       ('INSTRUCTOR'),
       ('STUDENT');

INSERT INTO "user" (first_name, last_name, email, "password", phone, status)
VALUES ('John', 'Smith', 'john-smith@gmail.com', '$2a$10$2DXHDU2uYMrlIiRHVb2CletM6kMjhLmvHzgyfOHQlV2SrLzE17hSK', '+380974309331', 'A'),
       ('Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$Zd/kQRuvnJ2br8dN5OyWt.LskBDAHL1UpZoRdbdOz7QcmNPJYJdYe', '+380971668744', 'A'),
       ('Tyrion', 'Lannister', 'goldlannister@gmail.com', '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m', '+380971205151', 'A');

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (1, 3);

INSERT INTO course (code, subject, description)
VALUES (22324, 'Mathematics', 'Introductory course on mathematics'),
       (34432, 'History', 'Overview of world history'),
       (99831, 'Literature', 'Study of classical literature'),
       (56548, 'Physics', 'Fundamentals of physics'),
       (76552, 'Computer Science', 'Introduction to computer programming');

INSERT INTO lesson (course_code, title, description)
VALUES (22324, 'Introduction to Algebra', 'Basic concepts and operations in algebra'),
       (22324, 'Geometry Fundamentals', 'Fundamentals of geometric shapes and properties'),
       (22324, 'Calculus Basics', 'Introduction to differentiation and integration'),
       (22324, 'Statistics Fundamentals', 'Basic concepts in statistics and data analysis'),
       (22324, 'Probability Theory', 'Introduction to probability theory and applications'),
       (34432, 'Ancient Civilizations', 'Study of ancient cultures and civilizations'),
       (34432, 'Middle Ages and Renaissance', 'Examination of the Middle Ages and the Renaissance period'),
       (34432, 'Age of Exploration', 'Exploration and expansion of European powers'),
       (34432, 'Colonialism and Imperialism', 'Impact of colonialism and imperialism on global history'),
       (34432, 'World Wars and Contemporary History', 'Study of major world wars and contemporary events'),
       (99831, 'Introduction to Poetry', 'Exploring different forms and techniques in poetry'),
       (99831, 'Shakespearean Plays', 'Analysis and interpretation of selected plays by William Shakespeare'),
       (99831, 'Modern Literature', 'Examination of influential works in modern literature'),
       (99831, 'Literary Criticism', 'Introduction to critical analysis and interpretation of literary works'),
       (99831, 'Comparative Literature', 'Study of literary works from different cultures and traditions'),
       (56548, 'Mechanics', 'Study of motion, forces, and energy'),
       (56548, 'Thermodynamics', 'Concepts of heat, temperature, and energy transfer'),
       (56548, 'Electromagnetism', 'Understanding electricity, magnetism, and their interactions'),
       (56548, 'Optics', 'Study of light, reflection, refraction, and optics phenomena'),
       (56548, 'Quantum Mechanics', 'Introduction to quantum physics and its principles'),
       (56548, 'Relativity Theory', 'Concepts of special and general relativity'),
       (76552, 'Artificial Intelligence', 'Exploring algorithms and techniques for AI applications'),
       (76552, 'Web Development', 'Building dynamic websites using HTML, CSS, and JavaScript'),
       (76552, 'Database Systems', 'Understanding relational databases and SQL'),
       (76552, 'Cyber-security', 'Introduction to computer security and protection against cyber threats'),
       (76552, 'Data Structures and Algorithms', 'Understanding fundamental data structures and algorithm design');
