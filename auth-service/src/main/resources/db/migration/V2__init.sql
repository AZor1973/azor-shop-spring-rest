insert into users (firstname, lastname, username, password, email, phone, status, account_non_expired, account_non_locked, credentials_non_expired, enabled)
values ('Иван', 'Иванов', 'user', '$2y$10$wF0UQYJE.T8QDRg4.aeliOty3lV.KEzNYBRquzVnVdCo0MeZKzGFq', 'azor@mail.ru',
        12345678, 'CREATED', true, true, true, true),
       ('Егор', 'Егоров', 'admin', '$2y$10$Z7oMadhQp5WpZrMg9JHboeOYSvkI7T2IQKVTsktr4A2Yi/PqLotGm', 'rosasor@yandex.com',
        87654321, 'CREATED', true, true, true, true),
       ('Андрей', 'Андреев', 'manager', '$2y$10$x/HTdPhin1CnHd/xmaKW..2hXWugcnFBvZ7trbDAIufFyd3q5cDJ6', 'andreyzorin1973@gmail.com',
        43215678, 'CREATED', true, true, true, true);

insert into roles (name)
values ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_MANAGER');

insert into users_roles (user_id, role_id)
values (1, 1), (2, 2), (3, 3);