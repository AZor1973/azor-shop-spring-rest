create table products
(
    id         bigserial primary key,
    title      varchar(255)  not null,
    price      numeric(8, 2) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table categories
(
    id    bigserial primary key,
    title varchar(255) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table products_categories
(
    product_id  bigint not null references products (id),
    category_id bigint not null references categories (id),
    created_at  timestamp default current_timestamp,
    updated_at  timestamp default current_timestamp,
    primary key (product_id, category_id)
);

insert into products (title, price)
values ('Milk', 50.20),
       ('Bread', 20.20),
       ('Cheese', 490.20),
       ('Apple', 90.00),
       ('Orange', 90.00),
       ('Fish', 290.00),
       ('Meat', 350.00),
       ('Cabbage', 20.00),
       ('Carrot', 18.00),
       ('Onion', 32.00),
       ('Candies', 450.00);

create table orders
(
    id          bigserial primary key,
    username    varchar(255)  not null,
    full_name    varchar(255)  not null,
    total_price numeric(8, 2) not null,
    address     varchar(255),
    phone       varchar(255),
    order_status varchar(20) not null,
    created_at  timestamp default current_timestamp,
    updated_at  timestamp default current_timestamp
);

create table order_items
(
    id                bigserial primary key,
    product_id        bigint        not null references products (id),
    order_id          bigint        not null references orders (id),
    quantity          int           not null,
    price_per_product numeric(8, 2) not null,
    price             numeric(8, 2) not null,
    created_at        timestamp default current_timestamp,
    updated_at        timestamp default current_timestamp
);









