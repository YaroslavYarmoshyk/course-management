INSERT INTO "user" (id, first_name, last_name, email, "password", phone, status)
VALUES (1, 'John', 'Smith', 'john-smith@gmail.com', '$2a$10$2DXHDU2uYMrlIiRHVb2CletM6kMjhLmvHzgyfOHQlV2SrLzE17hSK', '+380974309331', 'A'),
       (2, 'Marry', 'Poppins', 'poppins@yahoo.com', '$2a$10$Zd/kQRuvnJ2br8dN5OyWt.LskBDAHL1UpZoRdbdOz7QcmNPJYJdYe', '+380971668744', 'A'),
       (3, 'Tyrion', 'Lannister', 'goldlannister@gmail.com', '$2a$10$hbhWFqEqpikKav8uAGP7WOzTUxAz..TJkVnloLn0bS6vbw4aGqD/m', '+380971205151', 'A'),
       (4, 'Maria', 'Montesory', 'maria-montesory@gmail.com', '$2a$12$thFntc9W.Qwx5BZAs0xl6.vO7jbU6bCO3yASgxisRttIWX5rj9RpW', '+380971203332', 'A'),
       (5, 'Thor', 'Odinson', 'thor-odinson96@gmail.com', '$2a$12$LHFNdCdyogUckK8FTdgFKeX0UFC35U5UM/uVxo8A1oBtpykr0JyIm', '+380971204444', 'A')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_role (user_id, role_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 3)
ON CONFLICT (user_id, role_id) DO NOTHING;