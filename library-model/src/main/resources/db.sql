create table lib_category (
	id bigserial not null primary key,
	name varchar(25) not null unique
);