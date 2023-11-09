create table cat_breed
(
  id                bigserial primary key,
  name              text not null unique,
  description       text not null,
  link              text not null
);

create table cat
(
    id                bigserial primary key,
    breed_id          bigint references cat_breed(id) not null,
    name              text not null,
    favorite_spot     text not null
);
