
# --- !Ups
create table phones
(
	id bigserial,
	title varchar(100),
	number varchar(100)
);

create unique index phones_id_uindex
	on phones (id);

alter table phones
	add constraint phones_pk
		primary key (id);


# --- !Downs

drop table "phones" if exists;