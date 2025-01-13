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
		,document_type_id, document)
	values(n_id
		,vi_full_name, vi_email
		,ni_document_type_id, vi_document);
		
	insert into profile(user_id, name, is_principal, status_id)
	values(n_id, vi_full_name, 1, 3);
	
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
	declare n_user_status_id__active		int default 2;
	declare n_profile_status_id__active		int default 3;
	declare n_user_id						int;
    declare v_username						varchar(50);
	declare	v_password						varchar(500);

	select u.user_id
		, u.username
		, u.password
	into n_user_id
		, v_username
        , v_password
    from user u
		inner join person p on p.person_id = u.user_id
			and p.status_id = n_user_status_id__active
	where u.status_id = n_user_status_id__active
		and u.username = vi_username
        and u.device_id = ni_device_id
    ;
    
    select n_user_id  as user_id
		, v_username  as username
        , v_password  as password;
        
	select p.profile_id
		, p.name
		, p.is_principal
    from profile p 
    where p.status_id = n_profile_status_id__active
		and p.user_id = n_user_id
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
    
	declare n_status_id__active		int default 2;
    
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

		delete from tmp_validate_email where email = vi_username;
		
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




drop procedure if exists sp_insert_log_service;
DELIMITER ;;
CREATE PROCEDURE sp_insert_log_service(
    request_code varchar(20),
    profile_id int,
    method varchar(10),
    end_point varchar(200),
    http_status_code int,
    begin_datetime datetime,
    end_datetime datetime,
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
	
	INSERT INTO `qalert_bd`.`log_service`
		(`request_code`,
		`profile_id`,
		`method`,        
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
		(request_code,
		profile_id,
		method,
		end_point,
		http_status_code,
		date(begin_datetime),
		time(begin_datetime),
        cast(TIMESTAMPDIFF(MICROSECOND, begin_datetime, end_datetime) / 1000 as UNSIGNED),
		date(end_datetime),
		time(end_datetime),
		request_header,
		request_body,
		response_body,
		error_);

END ;;
DELIMITER ;

GRANT execute on procedure sp_insert_log_service   to 'qalert_app'@'%';



CREATE  OR REPLACE VIEW vw_status AS

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-12-05
	-- Objetivo: 	view status
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- 				select * from vw_status;
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
	select st.status_type_id
		, st.name				as status_type_name
		, st.description		as status_type_description
		, s.status_id
		, s.name				as status_name
		, s.status				as status
	from status_type st
		inner join status s on s.status_type_id = st.status_type_id;
    
GRANT select on vw_status to 'qalert_app'@'%';




drop procedure if exists sp_insert_and_get_additives_from_plain_text;
DELIMITER ;;
CREATE PROCEDURE `sp_insert_and_get_additives_from_plain_text`(
    ni_profile_id int
	,vi_data varchar(9000)
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Insert a row in "tmp_scan_header" and "tmp_scan_detail" and return additives
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- 				call sp_insert_and_get_additives_from_plain_text(1, 'RIBOFLAVINAS');
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
    declare d_current_datetime 			datetime default current_timestamp();
    declare n_data_size					int default LENGTH(vi_data);
    
    declare n_status_id__active			int default 4;
    declare toxicity_level_id__harmless	int default 1;
    declare toxicity_level_id__medium	int default 2;
    declare toxicity_level_id__harmful  int default 3;
    
    
    -- save all additives without filter
    CREATE TEMPORARY TABLE additive_found_tmp(
	 	  additive_id 			int
		  , additive_group_id	int 
 		  , toxicity_level_id 	int 
 		  , name 				varchar(50) 
 		  , code 				varchar(10) 
 		  , description 		varchar(500)
 		  , code_characters_number int
 		  , name_characters_number int
 		  , converted_code 		varchar(20)
 		  , converted_name 		varchar(50)
		  , is_match_by_name  	bit
    );
    
    CREATE TEMPORARY TABLE additive_found_by_name_tmp select * from additive_found_tmp;
	
	CREATE TEMPORARY TABLE additive_found_by_code_tmp select * from additive_found_tmp;
	
	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
        
        
	delete from tmp_scan_header where profile_id = ni_profile_id;
	delete from tmp_scan_detail where profile_id = ni_profile_id;

    
    -- ****************************************************************
    -- ********************insert data to avoid searching for it again
    -- ****************************************************************
    insert into additive_found_tmp
    select a.additive_id
		, a.additive_group_id
		, a.toxicity_level_id
		, a.name
		, a.code
		, a.description
		, a.code_characters_number
		, a.name_characters_number
		, a.converted_code
		, a.converted_name
		, case when vi_data like a.converted_name then 1 else 0 end as is_match_by_name -- flag to determine if encountred by name or code
	from additive a
	where status_id = n_status_id__active
		and (vi_data like a.converted_name or vi_data like a.converted_code); -- Find by name or code. This is required by business rules.
    
    
    -- ****************************************************************
    -- ***************************************************Get by name
    -- ****************************************************************
    insert into additive_found_by_name_tmp
    with cte_additive_found_by_name as (
			select af.*
				, row_number() over(partition by af.additive_group_id order by af.name_characters_number desc) as additive_position
            from additive_found_tmp af
            where af.is_match_by_name = 1
		)
    select a.additive_id
		, a.additive_group_id
		, a.toxicity_level_id
		, a.name
		, a.code
		, a.description
		, a.code_characters_number
		, a.name_characters_number
		, a.converted_code
		, a.converted_name
		, a.is_match_by_name
	from cte_additive_found_by_name a
	where a.additive_position = 1;
	
    
    -- ****************************************************************
    -- ***************************************************Get by code
    -- ****************************************************************
    insert into additive_found_by_code_tmp
    with cte_additive_found_by_code as (
			select af.*
				, row_number() over(partition by af.additive_group_id order by af.code_characters_number desc) as additive_position
            from additive_found_tmp af
            where af.is_match_by_name = 0
		)
    select c.additive_id
		, c.additive_group_id
		, c.toxicity_level_id
		, c.name
		, c.code
		, c.description
		, c.code_characters_number
		, c.name_characters_number
		, c.converted_code
		, c.converted_name
		, c.is_match_by_name
	from cte_additive_found_by_code c
	where c.additive_position = 1
		and not exists(select 1
					   from additive_found_by_name_tmp n
                       where n.additive_group_id = c.additive_group_id);
    
    
    -- ****************************************************************
    -- **********************************************insert header tmp
    -- ****************************************************************
    insert into tmp_scan_header(profile_id
		, data
        , harmless_additives_number
        , medium_additives_number
        , harmful_additives_number)
    select ni_profile_id
		, vi_data
		, count(case when toxicity_level_id = 1 then toxicity_level_id__harmless end)
		, count(case when toxicity_level_id = 2 then toxicity_level_id__medium end)
        , count(case when toxicity_level_id = 3 then toxicity_level_id__harmful end) 
    from(
		select *
		from additive_found_by_name_tmp
		union all
		select *
		from additive_found_by_code_tmp
		) additive_found;
    
    
    -- ****************************************************************
    -- **********************************************insert detail tmp
    -- ****************************************************************        
	insert into tmp_scan_detail(profile_id
		, additive_id
        , aditive_name_or_code)
	select ni_profile_id
		, additive_found.additive_id
		, case when additive_found.is_match_by_name is not null  then additive_found.name else additive_found.code end
    from(
		select *
		from additive_found_by_name_tmp
		union all
		select *
		from additive_found_by_code_tmp
		) additive_found;
        
	DROP TEMPORARY TABLE IF EXISTS additive_found_tmp;
	DROP TEMPORARY TABLE IF EXISTS additive_found_by_name_tmp;
	DROP TEMPORARY TABLE IF EXISTS additive_found_by_code_tmp;
    
    call sp_get_additives_report(ni_profile_id, 0);

END ;;
DELIMITER ;

GRANT execute on procedure sp_insert_and_get_additives_from_plain_text   to 'qalert_app'@'%';




drop procedure if exists sp_get_additives_report;
DELIMITER ;;
CREATE PROCEDURE `sp_get_additives_report`(
    ni_profile_id 	int
	,ni_report_type int
)
sp:BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-12-19
	-- Objetivo: 	Get report of additives
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
    -- 				ni_report_type: 
	-- 						1, Report last scan
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- 				call sp_get_additives_report(1, 1);
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
    declare d_begin_date   date;
    declare d_end_date	   date;
    declare d_current_date date;
    
    if ni_report_type = 0 then
    
		select t.toxicity_level_id
			, t.name as toxicity_level
			, count(a.toxicity_level_id) as total
		from tmp_scan_detail d 
			inner join additive a on a.additive_id = d.additive_id
				and d.profile_id = ni_profile_id
			right join toxicity_level t on t.toxicity_level_id = a.toxicity_level_id
		group by  t.toxicity_level_id
		;
		
		select d.additive_id
			, d.aditive_name_or_code as name
			, a.toxicity_level_id
			, 1 total
		from tmp_scan_detail d 
			inner join additive a on a.additive_id = d.additive_id
		where d.profile_id = ni_profile_id
        ;
        
	else 
		set d_current_date  = current_date();
		set d_end_date 		= d_current_date;
            
		if ni_report_type = 1 then
			set d_begin_date = d_current_date;
		elseif ni_report_type = 2 then
			set d_begin_date = DATE_SUB(d_current_date, INTERVAL 7 DAY);
		elseif ni_report_type = 3 then
			set d_begin_date = DATE_SUB(d_current_date, INTERVAL 30 DAY);
		elseif ni_report_type = 4 then
			set d_begin_date = DATE_SUB(d_current_date, INTERVAL 90 DAY);
        end if;
    
		-- ***********************************************************
        -- **************************************************header
        -- ***********************************************************
        select t.toxicity_level_id
			, t.name as toxicity_level
			, count(a.toxicity_level_id) as total
		from scan_detail d 
			inner join scan_header h on h.scan_header_id = d.scan_header_id
				and h.profile_id = ni_profile_id
				and h.created_date between d_begin_date and d_end_date
			inner join additive a on a.additive_id = d.additive_id
			right join toxicity_level t on t.toxicity_level_id = a.toxicity_level_id
		group by  t.toxicity_level_id
		;
        
        select d.additive_id
			, a.name
			, a.toxicity_level_id
			, count(1) total
		from scan_detail d 
			inner join additive a on a.additive_id = d.additive_id
		where exists(   select 1 
						from scan_header h 
						where h.profile_id = ni_profile_id
							and h.created_date between d_begin_date and d_end_date
                            and h.scan_header_id = d.scan_header_id
					  )
		group by d.additive_id, a.name, a.toxicity_level_id
		order by a.toxicity_level_id asc, a.name
		;
        
	end if;
    
END ;;
DELIMITER ;

GRANT execute on procedure sp_get_additives_report   to 'qalert_app'@'%';






drop procedure if exists sp_insert_scan;
DELIMITER ;;
CREATE PROCEDURE `sp_insert_scan`(
	ni_profile_id 			int,
    vi_product_name			varchar(100),
    vi_image_path			varchar(20)
)
sp:BEGIN  

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Insert a scan from tmp
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- 				call sp_insert_scan (1, 'testing');
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
	declare d_current_date datetime default current_timestamp();
    
	insert into scan_header(profile_id,
		data, 
        harmless_additives_number,
        medium_additives_number,
        harmful_additives_number,
        product_name,
        created_date,
        created_time,
        image_path
        )
	select x.profile_id,
		data,
        harmless_additives_number,
        medium_additives_number,
        harmful_additives_number,
        vi_product_name,
        d_current_date,
        d_current_date,
        vi_image_path
	from tmp_scan_header x 
	where x.profile_id = ni_profile_id;
    
    delete from tmp_scan_header
    where profile_id = ni_profile_id;
    
    insert into scan_detail(scan_header_id,
		additive_id,
        created_date,
        created_time)
    select LAST_INSERT_ID(),
		x.additive_id,
        d_current_date,
        d_current_date
    from tmp_scan_detail x 
	where x.profile_id = ni_profile_id;
    
    delete from tmp_scan_detail
    where profile_id = ni_profile_id;
		
END ;;
DELIMITER ;

GRANT execute on procedure sp_insert_scan   to 'qalert_app'@'%';



drop procedure if exists sp_get_scan_list;
DELIMITER ;;
CREATE PROCEDURE `sp_get_scan_list`(
	ni_profile_id 			int
)
sp:BEGIN  

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2024-09-03
	-- Objetivo: 	Get scan list
	-- ------------------------------------------------------------
	-- Descripción de parámetros:
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- 				call sp_get_scan_list (1);
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
	declare d_end_date 		date default current_date();
    declare d_begin_date 	date default DATE_SUB(d_end_date, INTERVAL 30 DAY);
    
	select x.scan_header_id
		, x.product_name
        , x.image_path
    from scan_header x
	where x.profile_id = ni_profile_id
		and x.created_date between d_begin_date and d_end_date;
		
END ;;
DELIMITER ;

GRANT execute on procedure sp_get_scan_list   to 'qalert_app'@'%';