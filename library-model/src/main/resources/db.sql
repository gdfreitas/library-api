create table lib_category (
  id bigserial not null primary key,
  name varchar(25) not null unique
);

create table lib_author (
  id bigserial not null primary key,
  name varchar(40) not null
);
create index idx_author_name on lib_author(name);