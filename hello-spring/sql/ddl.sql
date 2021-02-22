drop table if exists member cascade;
create table member(
    id bigint not null auto_increment,
    name varchar(255),
    primary key(id)
)