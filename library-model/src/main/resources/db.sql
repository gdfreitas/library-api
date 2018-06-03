create table lib_category (
	id bigserial not null primary key,
	name varchar(25) not null unique
);

create table lib_author (
	id bigserial not null primary key,
	name varchar(40) not null
);
create index idx_author_name on lib_author(name);

create table lib_user (
	id bigserial not null primary key,
	created_at timestamp not null,
	name varchar(40) not null,
	email varchar(70) not null unique,
	password varchar(100) not null,
	type varchar(20) not null
);

create table lib_user_role (
	user_id bigint not null,
	role varchar(30) not null,
	primary key(user_id, role),
	constraint fk_user_roles_user foreign key(user_id) references lib_user(id)
);

insert into lib_user (created_at, name, email, password, type) values(current_timestamp, 'Admin', 'adm@domain.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'EMPLOYEE');
insert into lib_user_role (user_id, role) values((select id from lib_user where email = 'adm@domain.com'), 'EMPLOYEE');
insert into lib_user_role (user_id, role) values((select id from lib_user where email = 'adm@domain.com'), 'ADMINISTRATOR');

create table lib_book (
	id bigserial not null primary key,
	title varchar(150) not null,
	description text not null,
	category_id	bigint not null,
	price decimal(5,2) not null,
	constraint fk_book_category foreign key(category_id) references lib_category(id)
);

create table lib_book_author (
	book_id bigint not null,
	author_id bigint not null,
	primary key (book_id, author_id),
	constraint fk_book_author_book foreign key(book_id) references lib_book(id),
	constraint fk_book_author_author foreign key(author_id) references lib_author(id)
);