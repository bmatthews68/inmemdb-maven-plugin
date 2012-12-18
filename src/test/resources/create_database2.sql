create table users (
	username_txt varchar(16) not empty,
	password_txt varchar(50) not empty,
	name_txt varchar(50),
	primary key (username_txt)
);