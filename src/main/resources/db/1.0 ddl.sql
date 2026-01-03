use qalert_bd;

create table status_type(
	status_type_id int,
    name varchar(50),
    description varchar(100),
    constraint pk_status primary key (status_type_id)
);


GRANT SELECT, INSERT, UPDATE ON qalert_bd.status_type           TO 'qalert_app'@'localhost';

create table status(
	status_id int NOT NULL AUTO_INCREMENT,
	status_type_id int,
    name varchar(50),
    status bit DEFAULT b'1',
    constraint pk_status primary key (status_id),
    constraint fk_status__status_type foreign key(status_type_id) references status_type(status_type_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.status                TO 'qalert_app'@'localhost';

  CREATE TABLE subscription (
    subscription_id INTEGER NOT NULL AUTO_INCREMENT,
    amount DECIMAL(5, 2),
    subscription_months INT,
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    status BIT(1) DEFAULT b'1',
    CONSTRAINT pk_subscription PRIMARY KEY (subscription_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.subscription                TO 'qalert_app'@'localhost';

CREATE TABLE user (
  user_id bigint NOT NULL AUTO_INCREMENT,
  username varchar(50) NOT NULL,
  password varchar(500) NOT NULL,
  device_id int not null,
  profiles_number int DEFAULT '1',
  is_authenticated bit DEFAULT b'0',
  subscription_id INTEGER DEFAULT NULL,
  created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
  last_update_datetime datetime,
  status_id int default 2,
  constraint pk_user PRIMARY KEY (user_id),
  CONSTRAINT fk_user__subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id),
  constraint fk_user__status foreign key(status_id) references status(status_id),
  UNIQUE KEY uk_user__username (username)
);

ALTER TABLE user
MODIFY username VARCHAR(40) not null;

create index idx_user__login on user(username, user_id, password, device_id);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.user                  TO 'qalert_app'@'localhost';

create table document_type(
	document_type_id int not null,
    name varchar(50) not null,
    constraint pk_document primary key(document_type_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.document_type         TO 'qalert_app'@'localhost';

create table person (
	person_id bigint not null,
	full_name varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
    document_type_id int not null,
    document varchar(20) not null,
	created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
	last_update_datetime datetime,
    status_id int default 2,
	constraint pk_person PRIMARY KEY (person_id),
	UNIQUE KEY uk_person__email (email),
    constraint fk_person__person foreign key(person_id) references user(user_id),
    constraint fk_person__document foreign key(document_type_id) references document_type(document_type_id),
    constraint fk_person__status foreign key(status_id) references status(status_id)
);

ALTER TABLE person
MODIFY email VARCHAR(40) not null;

ALTER TABLE person
MODIFY full_name VARCHAR(40) not null;

GRANT SELECT, INSERT, UPDATE ON qalert_bd.person                TO 'qalert_app'@'localhost';

CREATE TABLE tmp_validate_email (
  validate_email_id int NOT NULL AUTO_INCREMENT,
  email varchar(50) NOT NULL,
  verification_code char(3) NOT NULL,
  created_datetime datetime,
  expirate_datetime datetime,
  constraint pk_tmp_validate_email PRIMARY KEY (validate_email_id asc)
);
create index idx_tmp_validate_email__login on tmp_validate_email(email);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.tmp_validate_email    TO 'qalert_app'@'localhost';

CREATE TABLE profile (
  profile_id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  name varchar(50) NOT NULL,
  birthdate DATE,
  image_path VARCHAR(500),
  is_principal bit(1) DEFAULT 0,
  created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
  last_update_datetime datetime,
  status_id int,
  constraint pk_profile PRIMARY KEY (profile_id),
  CONSTRAINT fk_profile__user FOREIGN KEY (user_id) REFERENCES user (user_id),
  constraint fk_profile__status foreign key(status_id) references status(status_id)
);

ALTER TABLE profile
MODIFY name VARCHAR(20) not null;

GRANT SELECT, INSERT, UPDATE ON qalert_bd.profile               TO 'qalert_app'@'localhost';

create table master(
	master_id int not null auto_increment,
    table_id int not null,
    field_id int not null,
    sequence int,
    created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
    status bit default 1,
    description varchar(500),
    value_int int,
    value_varchar MEDIUMTEXT,
    constraint pk_master primary key(master_id),
    constraint uk_master unique(table_id, field_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.master                TO 'qalert_app'@'localhost';



-- ************************************************************************************************
-- *************************************************************************************** log
-- ************************************************************************************************
drop table if exists endpoint;
create table endpoint(
	endpoint_id 			int NOT NULL AUTO_INCREMENT,
    endpoint_name			varchar(50),
    method					varchar(10),
    description				varchar(200),
    status					bit default 1,
    created_datetime 		datetime DEFAULT CURRENT_TIMESTAMP,
	constraint pk_endpoint primary key(endpoint_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.endpoint          TO 'qalert_app'@'localhost';
create table log_service(
	  log_service_id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint,
    profile_id bigint,
    endpoint_id int not null,
    http_status_code int,
    begin_date date,
    begin_time time,
    transcurred_time int,
    end_date date,
    end_time time,
    request_header text,
    request_body text,
    response_body MEDIUMTEXT,
    error_ text,
	constraint pk_log_service_id PRIMARY KEY (log_service_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.log_service           TO 'qalert_app'@'localhost';



CREATE TABLE toxicity_level (
  toxicity_level_id int NOT NULL AUTO_INCREMENT,
  name varchar(30) NOT NULL,
  level int NOT NULL,
  color char(7) NOT NULL,
  constraint pk_toxicity_level PRIMARY KEY (toxicity_level_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.toxicity_level        TO 'qalert_app'@'localhost';

CREATE TABLE additive_group (
  additive_group_id int ,
  toxicity_level_id int ,
  name varchar(50) ,
  code varchar(10) ,
  function_id int DEFAULT NULL,
  status_id int DEFAULT 4,
  constraint pk_additive_group PRIMARY KEY (additive_group_id),
  CONSTRAINT fk_additive_group__toxicity_level FOREIGN KEY (toxicity_level_id) REFERENCES toxicity_level (toxicity_level_id),
  CONSTRAINT fk_additive_group__status FOREIGN KEY (status_id) REFERENCES status (status_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.additive_group        TO 'qalert_app'@'localhost';

CREATE TABLE additive (
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
  constraint pk_additive PRIMARY KEY (additive_id),
  CONSTRAINT fk_additive__additive_group FOREIGN KEY (additive_group_id) REFERENCES additive_group (additive_group_id),
  CONSTRAINT fk_aditivo__toxicity_level FOREIGN KEY (toxicity_level_id) REFERENCES toxicity_level (toxicity_level_id),
  CONSTRAINT fk_aditivo__status FOREIGN KEY (status_id) REFERENCES status (status_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.additive              TO 'qalert_app'@'localhost';



CREATE TABLE tmp_scan_header (
  user_id bigint,
  data varchar(9000) DEFAULT NULL,
  harmless_additives_number int,
  medium_additives_number int,
  harmful_additives_number int,
   created_datetime datetime DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_tmp_scan_header__user FOREIGN KEY (user_id) REFERENCES user (user_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.tmp_scan_header       TO 'qalert_app'@'localhost';



CREATE TABLE tmp_scan_detail (
  user_id bigint,
  additive_id int,
  aditive_name_or_code varchar(50),
  CONSTRAINT fk_tmp_scan_detail__user FOREIGN KEY (user_id) REFERENCES user (user_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.tmp_scan_detail           TO 'qalert_app'@'localhost';



CREATE TABLE scan_header (
  scan_header_id INT AUTO_INCREMENT,
  profile_id bigint,
  product_name VARCHAR(100),
  data VARCHAR(9000) DEFAULT NULL,
  harmless_additives_number INT,
  medium_additives_number INT,
  harmful_additives_number INT,
  created_date DATE not null,
  created_time TIME not null,
  CONSTRAINT pk_scan_header PRIMARY KEY (scan_header_id),
  CONSTRAINT fk_scan_header__profile FOREIGN KEY (profile_id) REFERENCES profile (profile_id)
);

alter table scan_header add image_path varchar(200);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.scan_header           TO 'qalert_app'@'localhost';

CREATE TABLE scan_detail (
  scan_detail_id int AUTO_INCREMENT,
  scan_header_id int,
  additive_id int,
   created_date DATE not null,
  created_time TIME not null,
   constraint pk_scan_detail primary key(scan_detail_id),
  CONSTRAINT fk_scan_detail__scan_header FOREIGN KEY (scan_header_id) REFERENCES scan_header (scan_header_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.scan_detail           TO 'qalert_app'@'localhost';



  ALTER TABLE log_service 
  ADD CONSTRAINT fk_log_service__profile 
  FOREIGN KEY (profile_id) REFERENCES profile(profile_id);




CREATE TABLE suggestions_type (
  suggestions_type_id INT NOT NULL AUTO_INCREMENT,
  description VARCHAR(20) NOT NULL,
  PRIMARY KEY (suggestions_type_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.suggestions_type           TO 'qalert_app'@'localhost';

CREATE TABLE suggestions (
  id_suggestions BIGINT NOT NULL AUTO_INCREMENT,
  suggestions_type_id INT DEFAULT NULL,
  user_id BIGINT DEFAULT NULL,
  suggestion VARCHAR(900) DEFAULT NULL,
  PRIMARY KEY (id_suggestions),
  KEY fk_suggestions__suggestions_type (suggestions_type_id),
  KEY fk_suggestions__user (user_id),
  CONSTRAINT fk_suggestions__suggestions_type FOREIGN KEY (suggestions_type_id) REFERENCES suggestions_type (suggestions_type_id),
  CONSTRAINT fk_suggestions__user FOREIGN KEY (user_id) REFERENCES user (user_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.suggestions          TO 'qalert_app'@'localhost';



-- ************************************************************************************************
-- ************************************************************************************************
-- ************************************************************************************************
create table additive_function(
	  additive_function_id 	int,
    additive_function_name 	varchar(50),
    status					bit default 1,
    created_datetime 		datetime DEFAULT CURRENT_TIMESTAMP,
	constraint pk_additive_function primary key(additive_function_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.additive_function          TO 'qalert_app'@'localhost';



-- ************************************************************************************************
-- *************************************************************************************** aliment
-- ************************************************************************************************
create table aliment_category(
	aliment_category_id 	int,
    aliment_category_name 	varchar(100),
    image_name				varchar(100),
    status					bit default 1,
    created_datetime 		datetime DEFAULT CURRENT_TIMESTAMP,
	constraint pk_additive_function primary key(aliment_category_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.aliment_category          TO 'qalert_app'@'localhost';



create table aliment(
	aliment_id 				int NOT NULL AUTO_INCREMENT,
    aliment_category_id		int,
    aliment_name 			varchar(100),
    letter					char(1),
    description				varchar(1000),
    status					bit default 1,
    created_datetime 		datetime DEFAULT CURRENT_TIMESTAMP,
	constraint pk_aliment primary key(aliment_id),
	CONSTRAINT fk_aliment__category FOREIGN KEY (aliment_category_id) REFERENCES aliment_category(aliment_category_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.aliment          TO 'qalert_app'@'localhost';

CREATE TABLE user_subscription (
    user_subscription_id BIGINT NOT NULL AUTO_INCREMENT,
    subscription_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    expiration_months DATETIME,
    status BIT(1) DEFAULT b'1',
    CONSTRAINT pk_user_subscription PRIMARY KEY (user_subscription_id),
    CONSTRAINT fk_user_subscription__subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id),
    CONSTRAINT fk_user_subscription__user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

GRANT SELECT, INSERT, UPDATE ON qalert_bd.user_subscription                TO 'qalert_app'@'localhost';