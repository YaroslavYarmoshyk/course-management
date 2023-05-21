INSERT INTO role (name)
VALUES ('ADMIN'),
       ('INSTRUCTOR'),
       ('STUDENT')
ON CONFLICT (name)
DO NOTHING;