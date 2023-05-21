CREATE TABLE IF NOT EXISTS role
(
    id   BIGINT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "user"
(
    id         BIGINT PRIMARY KEY,
    first_name TEXT,
    last_name  TEXT,
    email      TEXT UNIQUE NOT NULL,
    phone      TEXT UNIQUE,
    status     TEXT CHECK (status = 'ACTIVE' OR status = 'INACTIVE')
);

CREATE TABLE IF NOT EXISTS confirmation_token
(
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    "type"          TEXT,
    token           TEXT,
    expiration_date TIMESTAMP,
    activated       BOOLEAN,
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
    id          BIGINT PRIMARY KEY,
    course_code BIGINT,
    title       TEXT NOT NULL,
    credits     NUMERIC,
    description TEXT,
    homework    BYTEA
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
    user_id     BIGINT NOT NULL,
    course_code BIGINT NOT NULL,
    PRIMARY KEY (user_id, course_code),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (course_code) REFERENCES course (code)
);

CREATE TABLE IF NOT EXISTS user_mark
(
    user_id   BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    mark      INT CHECK (user_mark.mark >= 0 AND user_mark.mark <= 5),
    PRIMARY KEY (user_id, lesson_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (lesson_id) REFERENCES lesson (id)
);

CREATE SEQUENCE IF NOT EXISTS role_id_seq INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE IF NOT EXISTS user_id_seq INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE IF NOT EXISTS token_id_seq INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE IF NOT EXISTS lesson_id_seq INCREMENT 1 START 1 MINVALUE 1;
