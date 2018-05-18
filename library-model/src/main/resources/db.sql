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