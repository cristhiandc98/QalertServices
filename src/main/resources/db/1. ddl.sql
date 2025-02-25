use qalert_bd;

-- ***********************************************************************
-- ****************************************************************VIEWS
-- ***********************************************************************
drop VIEW if exists vw_status;


-- ***********************************************************************
-- *******************************************************************SP
-- ***********************************************************************
drop procedure if exists sp_insert_user;
drop procedure if exists sp_save_verification_code;
drop procedure if exists sp_login;
drop procedure if exists sp_update_password;
drop procedure if exists sp_list_app_settings;
drop procedure if exists sp_get_terms_and_conditions;
drop procedure if exists sp_insert_log_service;

-- ***********************************************************************
-- ****************************************************************TABLES
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
drop table if exists additive;
drop table if exists additive_group;
drop table if exists toxicity_level;
drop table if exists tmp_scan_header;
drop table if exists tmp_scan_detail;

create table status_type(
	status_type_id int,
    name varchar(50),
    description varchar(100),
    constraint pk_status primary key (status_type_id)
);
create table status(
	status_id int NOT NULL AUTO_INCREMENT,
	status_type_id int,
    name varchar(50),
    status bit DEFAULT b'1',
    constraint pk_status primary key (status_id),
    constraint fk_status__status_type foreign key(status_type_id) references status_type(status_type_id)
);

CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(500) NOT NULL,
  device_id int not null,
  `profiles_number` int DEFAULT '1',
  `is_authenticated` bit DEFAULT b'0',
  `is_premium` int DEFAULT '0',
  `created_datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_update_datetime` datetime,
  `status_id` int default 2,
  constraint pk_user PRIMARY KEY (`user_id`),
  constraint fk_user__status foreign key(status_id) references status(status_id),
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
    `status_id` int default 2,
	constraint pk_person PRIMARY KEY (`person_id`),
	UNIQUE KEY uk_person__email (`email`),
    constraint fk_person__person foreign key(person_id) references user(user_id),
    constraint fk_person__document foreign key(document_type_id) references document_type(document_type_id),
    constraint fk_person__status foreign key(status_id) references status(status_id)
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
  constraint pk_profile PRIMARY KEY (`profile_id`),
  CONSTRAINT `fk_profile__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  constraint fk_profile__status foreign key(status_id) references status(status_id)
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
    profile_id int,
    method varchar(10),
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

CREATE TABLE `toxicity_level` (
  `toxicity_level_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `level` int NOT NULL,
  `color` char(7) NOT NULL,
  constraint pk_toxicity_level PRIMARY KEY (`toxicity_level_id`)
);

CREATE TABLE `additive_group` (
  `additive_group_id` int ,
  `toxicity_level_id` int ,
  `name` varchar(50) ,
  `code` varchar(10) ,
  `function_id` int DEFAULT NULL,
  `status_id` int DEFAULT 4,
  constraint pk_additive_group PRIMARY KEY (`additive_group_id`),
  CONSTRAINT `fk_additive_group__toxicity_level` FOREIGN KEY (`toxicity_level_id`) REFERENCES `toxicity_level` (`toxicity_level_id`),
  CONSTRAINT `fk_additive_group__status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`)
);

CREATE TABLE `additive` (
  additive_id int  AUTO_INCREMENT,
  additive_group_id int ,
  toxicity_level_id int ,
  name varchar(50) ,
  code varchar(10) ,
  description varchar(500) DEFAULT NULL,
  status_id int DEFAULT 4,
  code_characters_number int DEFAULT NULL,
  name_characters_number int DEFAULT NULL,
  converted_code varchar(20) DEFAULT NULL,
  converted_name varchar(50) DEFAULT NULL,
  converted_name2 varchar(50) DEFAULT NULL,
  constraint pk_additive PRIMARY KEY (`additive_id`),
  CONSTRAINT `fk_additive__additive_group` FOREIGN KEY (`additive_group_id`) REFERENCES `additive_group` (`additive_group_id`),
  CONSTRAINT `fk_aditivo__toxicity_level` FOREIGN KEY (`toxicity_level_id`) REFERENCES `toxicity_level` (`toxicity_level_id`),
  CONSTRAINT `fk_aditivo__status` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`)
);

CREATE TABLE `tmp_scan_header` (
  `profile_id` int,
  `data` varchar(9000) DEFAULT NULL,
  `harmless_additives_number` int,
  `medium_additives_number` int,
  `harmful_additives_number` int,
   created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_tmp_scan_header__profile` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`profile_id`)
);

CREATE TABLE `tmp_scan_detail` (
  `profile_id` int,
  `additive_id` int,
  `aditive_name_or_code` varchar(50),
  CONSTRAINT `fk_tmp_scan_detail__profile` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`profile_id`)
);

CREATE TABLE `scan_header` (
  `scan_header_id` INT AUTO_INCREMENT,
  `profile_id` INT,
  `product_name` VARCHAR(100),
  `data` VARCHAR(9000) DEFAULT NULL,
  `harmless_additives_number` INT,
  `medium_additives_number` INT,
  `harmful_additives_number` INT,
  `created_date` DATE not null,
  `created_time` TIME not null,
  CONSTRAINT `pk_scan_header` PRIMARY KEY (`scan_header_id`),
  CONSTRAINT `fk_scan_header__profile` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`profile_id`)
);

alter table scan_header add image_path varchar(200);

CREATE TABLE `scan_detail` (
  `scan_detail_id` int AUTO_INCREMENT,
  `scan_header_id` int,
  `additive_id` int,
   `created_date` DATE not null,
  `created_time` TIME not null,
   constraint pk_scan_detail primary key(scan_detail_id),
  CONSTRAINT `fk_scan_detail__scan_header` FOREIGN KEY (`scan_header_id`) REFERENCES `scan_header` (`scan_header_id`)
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
	, (2, 'User and person')
    , (3, 'Profile')
    , (4, 'Additive');

insert into status(status_id, status_type_id, name)
values(1, 1, 'Active')
	, (2, 2, 'Active')
    , (3, 3, 'Active')
    , (4, 4, 'Active');

insert into document_type(document_type_id, name)
values(1, 'DNI')
	, (2, 'Carnet de extranjería')
	, (3, 'Cédula de identidad');

insert into toxicity_level(name, level, color)
values('Inofensivo', 1, '#00913f')
	, ('Medio', 2, '#FFFF00')
    , ('Nocivo', 3, '#ff0000');