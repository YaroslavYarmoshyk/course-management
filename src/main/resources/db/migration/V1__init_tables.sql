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
    credits     NUMERIC,
    course_code BIGINT NOT NULL,
    homework    BYTEA,
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
    feedback_date TIMESTAMP(0),
    course_code   BIGINT NOT NULL,
    student_id    BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course (code),
    FOREIGN KEY (student_id) REFERENCES "user" (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS grade
(
    id                   BIGSERIAL PRIMARY KEY,
    mark                 NUMERIC(38, 2) CHECK (grade.mark >= 0 AND grade.mark <= 5),
    mark_submission_date TIMESTAMP(0),
    lesson_id            BIGINT NOT NULL,
    student_id           BIGINT NOT NULL,
    instructor_id        BIGINT NOT NULL,
    FOREIGN KEY (lesson_id) REFERENCES lesson (id),
    FOREIGN KEY (student_id) REFERENCES "user" (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id),
    UNIQUE (student_id, lesson_id)
);

CREATE TABLE IF NOT EXISTS homework_submission
(
    id            BIGSERIAL PRIMARY KEY,
    file_name     TEXT,
    uploaded_date TIMESTAMP(0),
    lesson_id     BIGINT NOT NULL,
    student_id    BIGINT NOT NULL,
    homework      BYTEA,
    FOREIGN KEY (lesson_id) REFERENCES lesson (id),
    FOREIGN KEY (student_id) REFERENCES "user" (id)
);
