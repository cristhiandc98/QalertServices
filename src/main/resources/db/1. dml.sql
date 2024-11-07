use qalert_bd;

drop procedure if exists sp_insert_user;
drop procedure if exists sp_save_verification_code;
drop procedure if exists sp_login;
drop procedure if exists sp_update_password;
drop procedure if exists sp_list_app_settings;
drop procedure if exists sp_get_terms_and_conditions;
drop procedure if exists sp_insert_service_log;

-- ***********************************************************************
-- ********************************************************************DDL
-- ***********************************************************************
drop table if exists profile;
drop table if exists person;
drop table if exists user;
drop table if exists document_type;
drop table if exists status;
drop table if exists status_type;
drop table if exists tmp_validate_email;
drop table if exists master;
drop table if exists log_service;

create table status_type(
	status_type_id int,
    name varchar(50),
    description varchar(100),
    constraint pk_status primary key (status_type_id)
);
create table status(
	status_id int,
	status_type_id int,
    name varchar(50),
    status bit DEFAULT b'1',
    constraint pk_status primary key (status_type_id, status_id),
    constraint fk_status__status foreign key(status_type_id) references status_type(status_type_id)
);

CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(500) NOT NULL,
  device_id int not null,
  `profiles_number` int DEFAULT '1',
  `is_authenticated` bit DEFAULT b'0',
  `is_premium` bit(1) DEFAULT b'0',
  `created_datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_update_datetime` datetime,
  `status_id` int default 1,
  `status_type_id` int default 2,
  constraint pk_user PRIMARY KEY (`user_id`),
  constraint fk_user__status foreign key(status_id, status_type_id) references status(status_type_id, status_id),
  UNIQUE KEY `uk_user__username` (`username`)
);
create index idx_user__login on user(username, user_id, password, device_id);

create table document_type(
	document_type_id int not null,
    name varchar(50) not null,
    constraint pk_document primary key(document_type_id)
);

create table person (
	person_id int not null,
	full_name varchar(50) NOT NULL,
	`email` varchar(50) NOT NULL,
    document_type_id int not null,
    document varchar(20) not null,
	`created_datetime` datetime DEFAULT CURRENT_TIMESTAMP,
	`last_update_datetime` datetime,
    `status_id` int default 1,
    `status_type_id` int default 2,
	constraint pk_person PRIMARY KEY (`person_id`),
	UNIQUE KEY uk_person__email (`email`),
    constraint fk_person__person foreign key(person_id) references user(user_id),
    constraint fk_person__document foreign key(document_type_id) references document_type(document_type_id),
    constraint fk_person__status foreign key(status_id, status_type_id) references status(status_type_id, status_id)
);

CREATE TABLE `tmp_validate_email` (
  `validate_email_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `verification_code` char(3) NOT NULL,
  `created_datetime` datetime,
  `expirate_datetime` datetime,
  constraint pk_tmp_validate_email PRIMARY KEY (`validate_email_id` asc)
);
create index idx_tmp_validate_email__login on tmp_validate_email(email);

CREATE TABLE `profile` (
  `profile_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `is_principal` bit(1) DEFAULT 0,
  `created_datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_update_datetime` datetime,
  `status_id` int,
  `status_type_id` int,
  constraint pk_profile PRIMARY KEY (`profile_id`),
  CONSTRAINT `fk_profile__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  constraint fk_profile__status foreign key(status_id, status_type_id) references status(status_type_id, status_id)
);

create table master(
	master_id int not null auto_increment,
    table_id int not null,
    field_id int not null,
    sequence int,
    created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
    status bit default 1,
    description varchar(500),
    value_int int,
    value_varchar varchar(40000),
    constraint pk_master primary key(master_id),
    constraint uk_master unique(table_id, field_id)
);

create table log_service(
	log_service_id bigint NOT NULL AUTO_INCREMENT,
    request_code varchar(20),
    key_ int,
    key_type int,
    method int,
    end_point varchar(200),
    http_status_code int,
    begin_date date,
    begin_time time,
    transcurred_time int,
    end_date date,
    end_time time,
    request_header text,
    request_body text,
    response_body text,
    error_ text,
	constraint pk_log_service_id PRIMARY KEY (log_service_id)
);

-- ***********************************************************************
-- ********************************************************************DML
-- ***********************************************************************
insert into master(table_id, field_id, description, status)
values(1, 0, 'App settings', 0);

insert into master(table_id, field_id, description, value_varchar)
values(1, 1, 'Terms and conditions', 'What is Lorem Ipsum? Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industrys standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.');

insert into master(table_id, field_id, description, value_int)
values(0, 0, 'Services settings', null)
	,(0, 1, 'Verification code expiration time in minutes.', 5);
    
insert into status_type(status_type_id, name)
values(1, 'General')
	, (2, 'User and person');

insert into status(status_id, status_type_id, name)
values(1, 1, 'Active')
	, (2, 1, 'Active');

insert into document_type(document_type_id, name)
values(1, 'DNI')
	, (2, 'Carnet de extranjería')
	, (3, 'Cédula de identidad');