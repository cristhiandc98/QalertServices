use qalert_bd;

drop procedure if exists sp_insert_user;
DELIMITER ;;
CREATE PROCEDURE `sp_insert_user`(
	vi_username 			varchar(50)
	,vi_password 			varchar(500)
	,ni_device_id 			int
	,vi_verification_code 	char(3)
	,vi_email 				varchar(50)
	,vi_full_name 			varchar(50)
	,ni_document_type_id 	int
	,vi_document 			varchar(20)
)
sp:BEGIN  

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Insert a user
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_insert_user ('USUARIO@GMAIL.COM', 'CLAVE123', 153, '333', 'USUARIO@GMAIL.COM', 'NOMBRE UNO DOS TRES', 1, '12345678')
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
	
	declare n_id int;
	declare n_validate_email_id int;
	declare d_current_datetime datetime default current_timestamp();
	declare d_verification_code_expiration_datetime datetime;
    
	
	-- ******************************************************************************
	-- *****************************************Verify if verification code is valid
	-- ******************************************************************************
	begin 
		SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
		select t.expirate_datetime, t.validate_email_id
		into d_verification_code_expiration_datetime, n_validate_email_id
		from tmp_validate_email t
		where t.email = vi_email
			and t.verification_code = vi_verification_code;

		delete from tmp_validate_email where email = vi_email;
		
		if(d_verification_code_expiration_datetime is not null) then
			if d_verification_code_expiration_datetime < d_current_datetime then
				select 'Código de verificación ha expirado, por favor, intente otra vez.'	as user_mssg, '0' as status;
				leave sp;
			end if;
		else 
			select 'Código de verificación inválido.'	as user_mssg, '0' as status;
			leave sp;
		end if;
	end;
	
	-- ******************************************************************************
	-- ******************************************Verify that the user is not repeated
	-- ******************************************************************************
    if (exists( select 1
				from user u
				where u.username = vi_username
					and u.status_id = 1)) then
		select 'El nombre de usuario ya está registrado en otro dispositivo.'	as user_mssg, '0' as status;
		leave sp;
	end if;
	
	
	-- ******************************************************************************
	-- -- **********************************************************Begin transaction
	-- ******************************************************************************
	insert into user(username, password, device_id)
	values(vi_username, vi_password, ni_device_id);
	
	set n_id = LAST_INSERT_ID();
	
	insert into person(person_id
		,full_name, email
		,document_type_id, document
		,status_type_id, status_id)
	values(n_id
		,vi_full_name, vi_email
		,ni_document_type_id, vi_document
		,1, 1);
		
	insert into profile(user_id, name, is_principal)
	values(n_id, 'Yo', 1);
	
    commit;
    
	select '¡Usuario registrado exitosamente!'	as user_mssg, '1' as status;
		
END ;;
DELIMITER ;

grant execute on procedure sp_insert_user to 'qalert_app'@'%';



drop procedure if exists sp_save_verification_code;
DELIMITER ;;
CREATE PROCEDURE `sp_save_verification_code`(
vi_username varchar(50)
,vi_email varchar(50)
,vi_verification_code varchar(3)
,is_change_device bit
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Generate a new verification code
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_save_verification_code ('USUARIO@GMAIL.COM', 'USUARIO@GMAIL.COM', '153', 1) --Generate only when the user does not exist.
    -- call sp_save_verification_code ('USUARIO@GMAIL.COM', 'USUARIO@GMAIL.COM', '153', 0) --Used when changing the password
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************

	declare d_expirate_datetime datetime;
    declare d_current_datetime datetime default current_timestamp();
    declare n_expiration_time int default (select m.value_int from master m where m.table_id = 0 and m.field_id = 1);   
	declare n_id int;
    

	-- ******************************************************************************
	-- -- ******************************************************************Clean tmp
	-- ******************************************************************************
    set d_expirate_datetime = DATE_ADD(d_current_datetime, INTERVAL -n_expiration_time minute);
    delete from tmp_validate_email
    where created_datetime < d_expirate_datetime;
    
    
	-- ******************************************************************************
	-- -- *************************************************************Validate email
	-- ******************************************************************************
    if is_change_device = 0 and exists( select 1
										from user u 
                                        where u.username = vi_username
											and u.status_id = 1
										) then
		
		select 'El correo proporcionado está registrado en otro dispositivo.' as user_mssg, '0' as status;
		leave sp;
    end if;
    
	-- ******************************************************************************
	-- *************************************************************begin transaction
	-- ******************************************************************************
	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
    set d_expirate_datetime = DATE_ADD(d_current_datetime, INTERVAL n_expiration_time minute);
    set n_id = (select t.validate_email_id from tmp_validate_email t where t.email = vi_email);
    
	IF(n_id is not null) then
		update tmp_validate_email 
		set verification_code = vi_verification_code
			,created_datetime = d_current_datetime
			,expirate_datetime = d_expirate_datetime
		where validate_email_id = n_id;
	else 
		insert into tmp_validate_email(email, verification_code, created_datetime, expirate_datetime)
		values(vi_email, vi_verification_code, d_current_datetime, d_expirate_datetime);
	end if;
    
	select concat('Se envió un código de verificación al correo: ',vi_email) as user_mssg, '1' as status;
    
END ;;
DELIMITER ;

grant execute on procedure sp_save_verification_code to 'qalert_app'@'%';



drop procedure if exists sp_login;
DELIMITER ;;
CREATE PROCEDURE `sp_login`(
vi_username 		varchar(40)
,ni_device_id		int
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Log in the user
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_login('ALT.V4-B77TI9Q@YOPMAIL.COM', 431)
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
	declare n_status_id__active		int default 1;

	select u.user_id
		,u.username
		,u.password
		,p.full_name
    from user u
		inner join person p on p.person_id = u.user_id
			and p.status_id = n_status_id__active
		-- inner join profile pf on pf.status_id = n_status_id__active
	where u.status_id = n_status_id__active
		and u.username = vi_username
        and u.device_id = ni_device_id
    ;
    
END ;;
DELIMITER ;

grant execute on procedure sp_login to 'qalert_app'@'%';


drop procedure if exists sp_update_password;
DELIMITER ;;
CREATE DEFINER=`qalertadmin`@`%` PROCEDURE `sp_update_password`(
vi_username 				varchar(50)
,vi_verification_code		int
,vi_password				varchar(500)
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Update a user
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_update_password('CRDC98CRDC@GMAIL.COM', 153, 'XXX');
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
	declare n_status_id__active		int default 1;
    
    -- Verification code variables
    declare n_validate_email_id int;
	declare d_current_datetime datetime default current_timestamp();
	declare d_verification_code_expiration_datetime datetime;

	-- ******************************************************************************
	-- -- ***************************************Verify if verification code is valid
	-- ******************************************************************************
	begin 
		SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
		select t.expirate_datetime, t.validate_email_id
		into d_verification_code_expiration_datetime, n_validate_email_id
		from tmp_validate_email t
		where t.email = vi_username
			and t.verification_code = vi_verification_code;

		-- delete from tmp_validate_email where email = vi_username;
		
		if(d_verification_code_expiration_datetime is not null) then
			if d_verification_code_expiration_datetime < d_current_datetime then
				select 'Código de verificación ha expirado, por favor, intente otra vez.'	as user_mssg, '0' as status;
				leave sp;
			end if;
		else 
			select 'Código de verificación inválido.'	as user_mssg, '0' as status;
			leave sp;
		end if;
        
        update user 
        set password = case when vi_password is null then password else vi_password end
			,device_id = vi_verification_code
        where username = vi_username
			and status_id = n_status_id__active
        ;
        
		select '¡Contraseña actualizada exitosamente!'	as user_mssg, '1' as status;
	end;
    
END ;;
DELIMITER ;

GRANT execute on procedure sp_update_password   to 'qalert_app'@'%';




drop procedure if exists sp_list_app_settings;
DELIMITER ;;
CREATE PROCEDURE sp_list_app_settings()
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	list app settings 
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_list_app_settings();
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
	
	select *
    from master
    where table_id = 1
		and status = 1;
END ;;
DELIMITER ;

GRANT execute on procedure sp_list_app_settings   to 'qalert_app'@'%';




drop procedure if exists sp_get_terms_and_conditions;
DELIMITER ;;
CREATE PROCEDURE sp_get_terms_and_conditions()
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Get terms and conditions
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_get_terms_and_conditions();
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
	
	select *
    from master
    where table_id = 1
		and field_id = 1
		and status = 1;
END ;;
DELIMITER ;

GRANT execute on procedure sp_get_terms_and_conditions   to 'qalert_app'@'%';




drop procedure if exists sp_insert_service_log;
DELIMITER ;;
CREATE PROCEDURE sp_insert_service_log(
    request_code varchar(20),
    key_ int,
    key_type int,
    end_point varchar(200),
    http_status_code int,
    begin_datetime datetime,
    request_header text,
    request_body text,
    response_body text,
    error_ text
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Insert a service log
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
    declare current_datetime datetime default current_timestamp();
	
	INSERT INTO `qalert_bd`.`service_log`
		(`request_code`,
		`key_`,
		`key_type`,
		`end_point`,
		`http_status_code`,
		`begin_date`,
		`begin_time`,
		`transcurred_time`,
		`end_date`,
		`end_time`,
		`request_header`,
		`request_body`,
		`response_body`,
		`error_`)
		VALUES 
		(service_log_id,
		request_code,
		key_,
		key_type,
		end_point,
		http_status_code,
		date(begin_datetime),
		time(begin_datetime),
        cast(TIMESTAMPDIFF(MICROSECOND, begin_datetime, current_datetime) / 1000 as UNSIGNED),
		date(current_datetime),
		time(current_datetime),
		end_date,
		end_time,
		request_header,
		request_body,
		response_body,
		error_);

END ;;
DELIMITER ;

GRANT execute on procedure sp_insert_service_log   to 'qalert_app'@'%';
