create table users
(
    id         bigserial primary key,
    firstname  varchar(80) not null,
    lastname   varchar(80) not null,
    username   varchar(36) not null unique,
    password   varchar(80) not null,
    email      varchar(50) unique,
    phone      varchar(20) not null,
    status     varchar(20) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    account_non_expired boolean not null,
    account_non_locked boolean not null,
    credentials_non_expired boolean not null,
    enabled boolean not null
);

create table roles
(
    id         bigserial primary key,
    name       varchar(50) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table users_roles
(
    user_id    bigint not null references users (id),
    role_id    bigint not null references roles (id),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    primary key (user_id, role_id)
);







