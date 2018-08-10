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

create table lib_order (
	id bigserial not null primary key,
	created_at timestamp not null,
	customer_id bigint not null,
	total decimal(5,2) not null,
	current_status varchar(20) not null,
	constraint fk_order_customer foreign key(customer_id) references lib_user(id)
);

create table lib_order_item (
	order_id bigint not null,
	book_id bigint not null,
	quantity int not null,
	price decimal(5,2) not null,
	primary key(order_id, book_id),
	constraint fk_order_item_order foreign key(order_id) references lib_order(id),
	constraint fk_order_item_book foreign key(book_id) references lib_book(id)
);

create table lib_order_history (
	order_id bigint not null,
	status varchar(20) not null,
	created_at timestamp not null,
	primary key(order_id, status),
	constraint fk_order_history_order foreign key(order_id) references lib_order(id)
);

create table lib_log_audit (
	id bigserial not null primary key,
	created_at timestamp not null,
	user_id bigint not null,
	action varchar(15) not null,
	element varchar(30) not null,
	constraint fk_logaudit_user foreign key(user_id) references lib_user(id)
);