drop table if exists users, mpa, films_users, friendship, genre, film_genre, films cascade;

create table mpa(
id integer not null primary key,
mpa_name varchar
);

create table films(
id integer GENERATED BY DEFAULT AS IDENTITY primary key,
name varchar(255) not null,
description varchar(200),
release_date date,
duration int,
mpa_id int references mpa(id)
);

create table users(
id integer GENERATED BY DEFAULT AS IDENTITY primary key,
name varchar(100),
email varchar(100) not null,
login varchar(20) not null unique,
birthday date
);

create table friendship(
user_id integer not null references users(id) on delete cascade,
friend_id integer not null references users(id) on delete cascade
);

create table genre(
id integer not null primary key,
genre_name varchar
);

create table film_genre(
film_id integer not null references films(id) on delete cascade,
genre_id integer not null references genre(id) on delete cascade
);

create table films_users(
film_id integer not null references films(id) on delete cascade,
user_id integer not null references users(id) on delete cascade,
primary key (film_id, user_id)
);