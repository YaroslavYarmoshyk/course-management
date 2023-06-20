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
    user_id         BIGINT NOT NULL,
    "type"          INT,
    token           TEXT,
    expiration_date TIMESTAMP,
    status          TEXT   NOT NULL CHECK (status = 'A' OR status = 'N'),
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS course
(
    code        BIGINT PRIMARY KEY,
    title       TEXT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS lesson
(
    id          BIGSERIAL PRIMARY KEY,
    course_code BIGINT,
    title       TEXT NOT NULL,
    description TEXT,
    credits     NUMERIC,
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
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    course_code BIGINT NOT NULL,
    status      TEXT,
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (course_code) REFERENCES course (code)
);

CREATE TABLE IF NOT EXISTS course_feedback
(
    id             BIGSERIAL PRIMARY KEY,
    user_course_id BIGINT NOT NULL,
    feedback       TEXT,
    "date"         TIMESTAMP,
    instructor_id  BIGINT,
    FOREIGN KEY (user_course_id) REFERENCES user_course (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS user_lesson
(
    id              BIGSERIAL PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    lesson_id       BIGINT NOT NULL,
    instructor_id   BIGINT,
    mark            NUMERIC(38, 2) CHECK (user_lesson.mark >= 0 AND user_lesson.mark <= 5),
    mark_applied_at TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES "user" (id),
    FOREIGN KEY (lesson_id) REFERENCES lesson (id),
    FOREIGN KEY (instructor_id) REFERENCES "user" (id),
    UNIQUE (student_id, lesson_id)
);

CREATE TABLE IF NOT EXISTS homework_submission
(
    id             BIGSERIAL PRIMARY KEY,
    user_lesson_id BIGINT NOT NULL,
    file_name      TEXT,
    uploaded_date  TIMESTAMP,
    homework       BYTEA,
    FOREIGN KEY (user_lesson_id) REFERENCES user_lesson (id)
);
