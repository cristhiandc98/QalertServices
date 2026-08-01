USE qalert_bd;

drop procedure if exists sp_insert_user;

DELIMITER //

CREATE PROCEDURE sp_insert_user(
	vi_username 			varchar(40)
	,vi_password 			varchar(500)
	,ni_device_id 			int
	,vi_verification_code 	char(3)
	,vi_email 				varchar(40)
	,vi_full_name 			varchar(40)
	,ni_document_type_id 	int
	,vi_document 			varchar(20)
)
sp:BEGIN
-- ______________________________________________________________________________

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
	
	declare n_id BIGINT;
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
		
        IF d_verification_code_expiration_datetime IS NOT NULL THEN
            IF d_verification_code_expiration_datetime < d_current_datetime THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Código de verificación ha expirado, por favor, intente otra vez.',
                        MYSQL_ERRNO = 50001;
            END IF;
        ELSE
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Código de verificación inválido.',
                    MYSQL_ERRNO = 50001;
        END IF;
    END;
	
	-- ******************************************************************************
	-- ******************************************Verify that the user is not repeated
	-- ******************************************************************************
    IF EXISTS (SELECT 1
               FROM user u
               WHERE u.username = vi_username
                 AND u.status_id = 1) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El nombre de usuario ya está registrado en otro dispositivo.',
                MYSQL_ERRNO = 50001;
    END IF;
	
	
	-- ******************************************************************************
	-- -- **********************************************************Begin transaction
	-- ******************************************************************************
	insert into user(username, password, device_id)
	values(upper(vi_username), vi_password, ni_device_id);
	
	set n_id = LAST_INSERT_ID();
	
	insert into person(person_id
		,full_name, email
		,document_type_id, document)
	values(n_id
		,upper(vi_full_name), upper(vi_email)
		,ni_document_type_id, vi_document);
		
	insert into profile(user_id, name, is_principal, status_id)
	values(n_id,"YO", 1, 3);
	
    commit;
		
-- ______________________________________________________________________________
END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_insert_user to 'qalert_app'@'%';



drop procedure if exists sp_save_verification_code;

DELIMITER //

CREATE PROCEDURE sp_save_verification_code(
vi_username varchar(40)
,vi_email varchar(40)
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
		
-- ______________________________________________________________________________
END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_save_verification_code to 'qalert_app'@'%';

drop procedure if exists sp_login;

DELIMITER //

CREATE PROCEDURE sp_login(
vi_username 		varchar(40)
,ni_device_id		int
)
sp:BEGIN
-- ______________________________________________________________________________

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
	declare n_user_id						BIGINT;
  declare v_username						varchar(50);
	declare	v_password						varchar(500);
	DECLARE v_document_type_id				INT(50);
	DECLARE v_document				VARCHAR(50);
  declare v_full_name       varchar(40);
  declare v_subscription_id     int;

	-- ______________________________________________________________________________
	-- Obtener información del usuario activo con ese username y device_id
	SELECT 
		u.user_id,
		u.username,
		u.password,
		p.document_type_id,
		p.document,
    p.full_name,
    u.subscription_id
	INTO 
		n_user_id,
		v_username,
		v_password,
		v_document_type_id,
		v_document,
    v_full_name,
    v_subscription_id
	FROM user u
	INNER JOIN person p ON p.person_id = u.user_id
		AND p.status_id = n_user_status_id__active
	WHERE u.status_id = n_user_status_id__active
		AND u.username = vi_username
		AND u.device_id = ni_device_id;

	-- ______________________________________________________________________________
	-- Devolver información del usuario
	SELECT 
		n_user_id 			AS user_id,
		v_username 			AS email,
		v_password 			AS password,
		v_document_type_id 	AS document_type_id,
		v_document		AS document,
    v_full_name   as full_name,
    v_subscription_id as subscription_id;

	-- ______________________________________________________________________________
	-- Devolver los perfiles activos asociados al usuario
	select p.profile_id
		, p.name
		, p.is_principal
    , p.image_path
    from profile p 
    where p.status_id = n_profile_status_id__active
		and p.user_id = n_user_id;
		
-- ______________________________________________________________________________
END;
//
DELIMITER ;

grant execute on procedure qalert_bd.sp_login to 'qalert_app'@'%';



drop procedure if exists sp_update_password;
DELIMITER ;;
CREATE PROCEDURE sp_update_password(
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
		
        IF d_verification_code_expiration_datetime IS NOT NULL THEN
            IF d_verification_code_expiration_datetime < d_current_datetime THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'El código de verificación ya no es válido',
                        MYSQL_ERRNO = 50001;
            END IF;
        ELSE
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'El código de verificación ingresado no coincide',
                    MYSQL_ERRNO = 50001;
        END IF;
        
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

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_update_password TO 'qalert_app'@'%';




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

grant execute on procedure qalert_bd.sp_list_app_settings   to 'qalert_app'@'%';




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

grant execute on procedure qalert_bd.sp_get_terms_and_conditions   to 'qalert_app'@'%';



drop procedure if exists sp_insert_log_service;
DELIMITER ;;
CREATE PROCEDURE sp_insert_log_service(
  IN user_id BIGINT,
  IN profile_id BIGINT,
  IN endpoint varchar(200),
  IN method varchar(10),
  IN http_status_code int,
  IN begin_datetime datetime,
  IN end_datetime datetime,
  IN request_header text,
  IN request_body text,
  IN response_body MEDIUMTEXT,
  IN error_ text
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
	
    declare endpoint_id int;
    
    set endpoint_id =    (select x.endpoint_id
						  from endpoint x 
						  where x.endpoint_name = endpoint
              AND x.method = method
							and status = 1);
    
	INSERT INTO `log_service`
		(`user_id`,
		`profile_id`,    
		`endpoint_id`,
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
		(user_id, 
		profile_id,
		endpoint_id,
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
grant execute on procedure qalert_bd.sp_insert_log_service   to 'qalert_app'@'%';



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
    
GRANT select on qalert_bd.vw_status to 'qalert_app'@'%';




drop procedure if exists sp_insert_and_get_additives_from_plain_text;
DELIMITER //
CREATE PROCEDURE sp_insert_and_get_additives_from_plain_text(
    ni_user_id BIGINT
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
        
        
	delete from tmp_scan_header where user_id = ni_user_id;
	delete from tmp_scan_detail where user_id = ni_user_id;

    
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
    insert into tmp_scan_header(user_id
		, data
        , harmless_additives_number
        , medium_additives_number
        , harmful_additives_number)
    select ni_user_id
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
	insert into tmp_scan_detail(user_id
		, additive_id
        , aditive_name_or_code)
	select ni_user_id
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
    
    call sp_get_additives_report(ni_user_id, -1, 0);

END;
// DELIMITER ;
grant execute on procedure qalert_bd.sp_insert_and_get_additives_from_plain_text   to 'qalert_app'@'%';




DROP PROCEDURE IF EXISTS sp_insert_scan;
DELIMITER //

CREATE PROCEDURE sp_insert_scan(
	ni_user_id                  BIGINT,
    vi_profile_id_concatenated  VARCHAR(100),
    vi_separator                VARCHAR(1),
    vi_product_name             VARCHAR(100),
    vi_image_path               VARCHAR(200)
)
sp:BEGIN

    DECLARE d_current_date DATE DEFAULT CURRENT_DATE();
    DECLARE d_current_time time DEFAULT CURRENT_TIME();

    -- ============================================
    -- Crear tabla temporal con profiles
    -- ============================================

    DROP TEMPORARY TABLE IF EXISTS tmp_profile;
	CREATE TEMPORARY TABLE tmp_profile AS
	SELECT CAST(jt.value AS UNSIGNED) AS profile_id
	FROM JSON_TABLE(
		CONCAT('["', REPLACE(vi_profile_id_concatenated, vi_separator, '","'), '"]'),
		'$[*]' COLUMNS (value VARCHAR(20) PATH '$')
	) jt;

    -- ============================================
    -- Insertar header y guardar IDs generados
    -- ============================================

    DROP TEMPORARY TABLE IF EXISTS tmp_inserted_headers;

    CREATE TEMPORARY TABLE tmp_inserted_headers (
        scan_header_id BIGINT,
        profile_id BIGINT
    );

    INSERT INTO scan_header (
        profile_id,
        data,
        harmless_additives_number,
        medium_additives_number,
        harmful_additives_number,
        product_name,
        created_date,
        created_time,
        image_path
    )
    SELECT 
        p.profile_id,
        h.data,
        h.harmless_additives_number,
        h.medium_additives_number,
        h.harmful_additives_number,
        vi_product_name,
        d_current_date,
        d_current_time,
        vi_image_path
    FROM tmp_scan_header h
    JOIN tmp_profile p
    WHERE h.user_id = ni_user_id;

    -- Guardar los IDs recién insertados
    INSERT INTO tmp_inserted_headers
    SELECT 
        sh.scan_header_id,
        sh.profile_id
    FROM scan_header sh
    JOIN tmp_profile p 
        ON p.profile_id = sh.profile_id
    WHERE sh.created_date = d_current_date
		and sh.created_time = d_current_time;

    -- ============================================
    -- Insertar detalle usando los IDs correctos
    -- ============================================

    INSERT INTO scan_detail (
        scan_header_id,
        additive_id,
        created_date,
        created_time
    )
    SELECT
        th.scan_header_id,
        d.additive_id,
        d_current_date,
        d_current_time
    FROM tmp_inserted_headers th
    JOIN tmp_scan_detail d 
        ON d.user_id = ni_user_id;

    -- ============================================
    -- Limpiar temporales
    -- ============================================

    DELETE FROM tmp_scan_detail
    WHERE user_id = ni_user_id;
    
    DELETE FROM tmp_scan_header
    WHERE user_id = ni_user_id;


END;
//
DELIMITER ;

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_insert_scan 
TO 'qalert_app'@'%';




drop procedure if exists sp_get_scan_list;
DELIMITER //
CREATE PROCEDURE sp_get_scan_list(
	ni_profile_id BIGINT
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
		and x.created_date between d_begin_date and d_end_date
    and x.status = 1;
END;
//
DELIMITER ;
grant execute on procedure qalert_bd.sp_get_scan_list   to 'qalert_app'@'%';



DROP FUNCTION IF EXISTS fn_existing_user;
DELIMITER $$
CREATE FUNCTION fn_existing_user(p_username VARCHAR(50))
RETURNS TINYINT(1)
READS SQL DATA
BEGIN
    DECLARE exists_flag TINYINT(1);

    SELECT EXISTS (
        SELECT 1
        FROM `user`
        WHERE username = p_username
    ) INTO exists_flag;

    RETURN exists_flag;
END $$
DELIMITER ;
GRANT EXECUTE ON FUNCTION qalert_bd.fn_existing_user TO 'qalert_app'@'%';




DROP PROCEDURE IF EXISTS sp_insert_profile;
DELIMITER //

CREATE PROCEDURE sp_insert_profile(
    vi_user_id         BIGINT,
    vi_name            VARCHAR(50),
    vi_birthdate       DATE,
    vi_image_path      VARCHAR(500)
)
sp:BEGIN

    DECLARE n_subscription_id INT;
    DECLARE n_existing_profiles INT;
    DECLARE n_max_profiles INT;
    DECLARE v_descripcion_perfiles VARCHAR(255);
    DECLARE n_profile_name_exists INT;

    -- Obtener subscription_id
    SELECT subscription_id INTO n_subscription_id
    FROM `user`
    WHERE user_id = vi_user_id;

    -- Perfiles existentes (excepto eliminados)
    SELECT COUNT(*) INTO n_existing_profiles
    FROM profile
    WHERE user_id = vi_user_id AND status_id != 5;

    -- Validación nombre duplicado (solo activos)
    SELECT COUNT(*) INTO n_profile_name_exists
    FROM profile
    WHERE user_id = vi_user_id 
      AND name = UPPER(vi_name)
      AND status_id = 3;

    IF n_profile_name_exists > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El nombre indicado ya está asignado a otro perfil',
                MYSQL_ERRNO = 50001;
    END IF;

    -- Determinar máximo de perfiles según suscripción
    IF n_subscription_id IS NULL THEN
        -- Usuario SIN suscripción (gratuito)
        SELECT value_int, value_varchar
        INTO n_max_profiles, v_descripcion_perfiles
        FROM master
        WHERE table_id = 0 AND field_id = 2 AND status = 1;

    ELSE
        -- Usuario CON suscripción (premium)
        SELECT value_int, value_varchar
        INTO n_max_profiles, v_descripcion_perfiles
        FROM master
        WHERE table_id = 0 AND field_id = 3 AND status = 1;

    END IF;

    -- Validar límite de perfiles
    IF n_existing_profiles >= n_max_profiles THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El usuario alcanzó el límite de perfiles permitidos',
                MYSQL_ERRNO = 50001;
    END IF;

    -- Insertar perfil
    INSERT INTO profile (user_id, name, birthdate, image_path, is_principal, status_id)
    VALUES (
        vi_user_id,
        UPPER(vi_name),
        vi_birthdate,
        vi_image_path,
        0,
        3
    );

	SELECT LAST_INSERT_ID() AS profile_id; 
END//
DELIMITER ;

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_insert_profile TO 'qalert_app'@'%';

drop procedure if exists sp_fetch_profiles;
DELIMITER //
CREATE PROCEDURE sp_fetch_profiles(
    vi_user_id BIGINT
)
sp:BEGIN

    DECLARE v_subscription_id INT;
    DECLARE v_max_profiles INT;

    -- Obtener subscription del usuario
    SELECT subscription_id
    INTO v_subscription_id
    FROM user
    WHERE user_id = vi_user_id;

    -- Si NO tiene suscripción → límite desde master (id = 5)
    IF v_subscription_id IS NULL THEN

        SELECT value_int
        INTO v_max_profiles
        FROM master
        WHERE master_id = 5
          AND status = 1;

        SELECT 
            p.profile_id,
            p.user_id,
            UPPER(p.name) AS name,
            TIMESTAMPDIFF(YEAR, p.birthdate, CURDATE()) AS age,
            p.birthdate,
            p.image_path,
            p.is_principal
        FROM profile p
        WHERE p.user_id = vi_user_id
          AND p.status_id = 3
        ORDER BY p.created_datetime ASC
        LIMIT v_max_profiles;

    ELSE
        -- Tiene suscripción → mostrar TODOS los activos
        SELECT 
            p.profile_id,
            p.user_id,
            UPPER(p.name) AS name,
            TIMESTAMPDIFF(YEAR, p.birthdate, CURDATE()) AS age,
            p.birthdate,
            p.image_path,
            p.is_principal
        FROM profile p
        WHERE p.user_id = vi_user_id
          AND p.status_id = 3
        ORDER BY p.created_datetime ASC;
    END IF;

    
END;
//
DELIMITER ;

grant execute on procedure qalert_bd.sp_fetch_profiles   to 'qalert_app'@'%';

drop procedure if exists sp_update_password;
DELIMITER ;;
CREATE PROCEDURE sp_update_password(
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
  DECLARE n_user_exists           INT DEFAULT 0;

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
		
        IF d_verification_code_expiration_datetime IS NOT NULL THEN
            IF d_verification_code_expiration_datetime < d_current_datetime THEN
                SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'El código de verificación ya no es válido',
                        MYSQL_ERRNO = 50001;
            END IF;
        ELSE
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'El código de verificación ingresado no coincide',
                    MYSQL_ERRNO = 50001;
        END IF;

        SELECT COUNT(1)
        INTO n_user_exists
        FROM user
        WHERE username = vi_username
          AND status_id = n_status_id__active;
          
        IF n_user_exists = 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'El usuario no existe',
                    MYSQL_ERRNO = 50001;
        END IF;
        
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

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_update_password TO 'qalert_app'@'%';

drop procedure if exists sp_update_profile;

DELIMITER //

CREATE PROCEDURE sp_update_profile(
    vi_profile_id     BIGINT,
    vi_name           VARCHAR(50),
    vi_birthdate      DATE,
	vi_image_path     VARCHAR(500)
)
sp:BEGIN

    DECLARE n_profile_name_exists INT;

    -- Verificar si el nombre del perfil ya existe para otro perfil del mismo usuario (que no esté eliminado)
    SELECT COUNT(1)INTO n_profile_name_exists
    FROM profile
    WHERE name = vi_name 
      AND profile_id != vi_profile_id
      AND status_id = 3
      AND user_id = (
          SELECT user_id FROM profile WHERE profile_id = vi_profile_id AND status_id != 5
      );

    IF n_profile_name_exists > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El nombre indicado ya pertenece a otro perfil del usuario',
                MYSQL_ERRNO = 50001;
    END IF;

    UPDATE profile
    SET name = UPPER(vi_name),
		  birthdate = vi_birthdate,
          image_path = vi_image_path
    WHERE profile_id = vi_profile_id
      AND status_id = 3;

END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_update_profile    to 'qalert_app'@'%';

drop procedure if exists sp_delete_profile; 
DELIMITER //

CREATE PROCEDURE sp_delete_profile(
    vi_profile_id BIGINT
)
sp:BEGIN

	DECLARE n_status_id_eliminated INT DEFAULT 5;
	
	UPDATE profile
	SET status_id = n_status_id_eliminated
	WHERE profile_id = vi_profile_id
		and is_principal = 0;

END;
//

DELIMITER ;
grant execute on procedure qalert_bd.sp_delete_profile   to 'qalert_app'@'%';




drop procedure if exists sp_get_additives_report;
DELIMITER //

CREATE PROCEDURE sp_get_additives_report(
    ni_profile_id     BIGINT,
    ni_report_type    INT,
    ni_scan_header_id INT
)
sp:BEGIN

    DECLARE d_current_date DATE DEFAULT CURRENT_DATE();
    DECLARE d_begin_date   DATE;
    DECLARE d_end_date     DATE DEFAULT d_current_date;

    -- ============================================================
    -- CALCULAR RANGO SOLO SI NO ES -1 NI 0
    -- ============================================================

    IF ni_report_type > 0 THEN

        SET d_begin_date = CASE ni_report_type
            WHEN 1 THEN d_current_date
            WHEN 2 THEN DATE_SUB(d_current_date, INTERVAL 7 DAY)
            WHEN 3 THEN DATE_SUB(d_current_date, INTERVAL 30 DAY)
            WHEN 4 THEN DATE_SUB(d_current_date, INTERVAL 90 DAY)
        END;

    END IF;


    -- ============================================================
    -- SI ES REPORTE DESDE TABLA TEMPORAL
    -- ============================================================

    IF ni_report_type = -1 THEN

        -- *******************************************************
        -- HEADER (desde tmp)
        -- *******************************************************
        SELECT t.toxicity_level_id,
               t.name AS toxicity_level,
               COUNT(a.toxicity_level_id) AS total
        FROM tmp_scan_detail d
        INNER JOIN additive a ON a.additive_id = d.additive_id
        RIGHT JOIN toxicity_level t ON t.toxicity_level_id = a.toxicity_level_id
        GROUP BY t.toxicity_level_id, t.name;

        -- *******************************************************
        -- DETALLE (desde tmp)
        -- *******************************************************
        SELECT d.additive_id,
               a.name,
               a.toxicity_level_id,
               COUNT(1) total
        FROM tmp_scan_detail d
        INNER JOIN additive a ON a.additive_id = d.additive_id
        GROUP BY d.additive_id, a.name, a.toxicity_level_id
        ORDER BY a.toxicity_level_id ASC, a.name;

    ELSE

        -- ========================================================
        -- LÓGICA NORMAL (scan_header)
        -- ========================================================

        -- HEADER
        SELECT t.toxicity_level_id,
               t.name AS toxicity_level,
               COUNT(a.toxicity_level_id) AS total,
               h.product_name
        FROM scan_detail d
        INNER JOIN scan_header h ON h.scan_header_id = d.scan_header_id
            AND h.profile_id = ni_profile_id
            AND h.status = 1
            AND (
                    (ni_report_type = 0 AND h.scan_header_id = ni_scan_header_id)
                 OR (ni_report_type > 0 AND h.created_date BETWEEN d_begin_date AND d_end_date)
                )
        INNER JOIN additive a ON a.additive_id = d.additive_id
        RIGHT JOIN toxicity_level t ON t.toxicity_level_id = a.toxicity_level_id
        GROUP BY t.toxicity_level_id, t.name, h.product_name;

        -- DETALLE
        SELECT d.additive_id,
               a.name,
               a.toxicity_level_id,
               COUNT(1) total
        FROM scan_detail d
        INNER JOIN additive a ON a.additive_id = d.additive_id
        INNER JOIN scan_header h ON h.scan_header_id = d.scan_header_id
            AND h.profile_id = ni_profile_id
            AND h.status = 1
            AND (
                    (ni_report_type = 0 AND h.scan_header_id = ni_scan_header_id)
                 OR (ni_report_type > 0 AND h.created_date BETWEEN d_begin_date AND d_end_date)
                )
        GROUP BY d.additive_id, a.name, a.toxicity_level_id
        ORDER BY a.toxicity_level_id ASC, a.name;

    END IF;

END;
//
DELIMITER ;

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_get_additives_report TO 'qalert_app'@'%';




drop procedure if exists sp_insert_suggestions;
DELIMITER //

CREATE PROCEDURE sp_insert_suggestions(
ni_suggestions_type_id int,
ni_user_id BIGINT,
vi_suggestion varchar(900)
)
BEGIN
-- ______________________________________________________________________________

INSERT INTO suggestions(
suggestions_type_id,
user_id,
suggestion
) VALUES(
ni_suggestions_type_id,
ni_user_id,
vi_suggestion
);

-- ______________________________________________________________________________
END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_insert_suggestions to 'qalert_app'@'%';

drop procedure if exists sp_listar_additive;
DELIMITER //
CREATE PROCEDURE sp_listar_additive()
BEGIN
-- ______________________________________________________________________________

SELECT
additive_id,
additive_group_id,
toxicity_level_id,
name
FROM additive;
-- ______________________________________________________________________________
END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_listar_additive to 'qalert_app'@'%';



-- ************************************************************************************************
-- *************************************************************************************** aliment
-- ************************************************************************************************
DROP PROCEDURE IF EXISTS sp_get_aliment_list;
DELIMITER ;;
CREATE PROCEDURE sp_get_aliment_list(
	user_id bigint
)
-- *************************
-- Versión:		1.0
-- Autor: 		Cristhian Díaz
-- Fecha:  		2025-12-03
-- Objetivo: 	Get aliments
-- ------------------------------------------------------------
-- Descripción de parámetros:
-- ------------------------------------------------------------
-- Ejemplo de uso
-- 				call sp_get_aliment_list(1);
-- ------------------------------------------------------------
-- Log
-- Fecha			Autor		Cod. Mod.	Comentarios
-- 
-- *************************
sp:BEGIN
	
    if exists ( select 1
				from user x 
				where x.user_id = user_id
					and x.subscription_id is null) then
		 SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El usuario no tiene una cuenta premium', MYSQL_ERRNO = 50001;
	end if;
    
    
    select a.aliment_category_id
		, a.aliment_category_name
        , a.image_name
    from aliment_category a 
    where a.status = 1;
			
            
    select ac.aliment_category_id
        , ac.aliment_category_name
        , aliment_id
        , aliment_name
        , letter
        , description
    from aliment a
		inner join aliment_category ac on ac.aliment_category_id = a.aliment_category_id
			and ac.status = 1
	where a.status = 1;
    
END ;;
DELIMITER ;
GRANT EXECUTE ON PROCEDURE qalert_bd.sp_get_aliment_list TO 'qalert_app'@'%';



DROP PROCEDURE IF EXISTS sp_get_app_settings_list;
DELIMITER ;;
CREATE PROCEDURE sp_get_app_settings_list()
BEGIN
	
	select table_id
		, field_id
        , sequence
        , value_int
        , value_varchar
    from master
    where (table_id = 1 and field_id != 1 AND status = 1)
		or (table_id = 0 and field_id in (2, 3) AND status = 1);
END ;;
DELIMITER ;
GRANT EXECUTE ON PROCEDURE qalert_bd.sp_get_app_settings_list TO 'qalert_app'@'%';



DROP PROCEDURE IF EXISTS sp_update_scan_header;
DELIMITER //
CREATE PROCEDURE sp_update_scan_header (
    IN ni_operacion INT,
    IN ni_scan_header_id INT,
    IN vi_product_name   VARCHAR(100)
)
BEGIN

    IF ni_operacion = 1 THEN

        UPDATE scan_header
        SET product_name = vi_product_name
        WHERE scan_header_id = ni_scan_header_id;

    ELSEIF ni_operacion = 2 THEN

        UPDATE scan_header
        SET status_id = ni_status_id
        WHERE scan_header_id = ni_scan_header_id;

    END IF;
END //
DELIMITER ;
grant execute on procedure qalert_bd.sp_update_scan_header   to 'qalert_app'@'%';



drop procedure if exists sp_insert_payment;
DELIMITER ;;
CREATE PROCEDURE sp_insert_payment(
	ni_user_id				bigint,
    ni_subscription_id		int
)
BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2026-02-28
	-- Objetivo: 	insert a payment
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_insert_payment(1, 1);
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN 
		ROLLBACK; 
	END;
    START TRANSACTION;
    
    
	insert into payment(user_id)
    values(ni_user_id);
    
    
    SET @last_id = LAST_INSERT_ID();
    
    
    insert into payment_detail(payment_id, product_id, quantity, unit_price, total_discount, total_amount)
    select @last_id, p.product_id, s.subscription_months, p.price, s.discount, (s.subscription_months * p.price - s.discount)
    from subscription s
		inner join product p on p.product_id = s.product_id
			and p.product_status_id = 6
    where s.subscription_id = ni_subscription_id	
		and s.subscription_status = 1;
        
              
    if not exists(select 1
				  from payment_detail x 
				  where x.payment_id = @last_id) then
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Suscripción inválida.', MYSQL_ERRNO = 50001;
	else 
		UPDATE payment p
		SET p.payment_code = CONCAT('QALERT', DATE_FORMAT(current_date(), '%Y%m'), LPAD(RIGHT(@last_id, 5), 5, '0'))
			, p.amount = (select sum(x.total_amount)
						  from payment_detail x 
						  where x.payment_id = p.payment_id)
		WHERE p.payment_id = @last_id;
    end if;
	
    COMMIT;
    
    
    select p.payment_id
		, p.payment_code
		, p.amount
		, c.currency_code
    from payment p
		inner join currency c on c.currency_id = p.currency_id
    where p.payment_id = @last_id;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_insert_payment   to 'qalert_app'@'%';



drop procedure if exists sp_update_payment;
DELIMITER ;;
CREATE PROCEDURE sp_update_payment(
	ni_payment_id				bigint,
    vi_payment_order_id			char(32),
	vi_payment_status_name		varchar(20),
    vi_payment_error			text
)
BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2026-02-28
	-- Objetivo: 	update a payment
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_update_payment(4, 9);
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************  
    
    declare n_payment_status_id int default (select x.status_id
											from status x 
                                            where x.status_type_id = 6
												and x.status_code = vi_payment_status_name);
    
	UPDATE payment p
	SET p.payment_status_id = IFNULL(n_payment_status_id, payment_status_id)
		, payment_order_id = IFNULL(vi_payment_order_id, payment_order_id)
		, updated_datetime = CURRENT_TIMESTAMP()
        , payment_error = IFNULL(vi_payment_error, payment_error)
	WHERE p.payment_id = ni_payment_id;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_update_payment   to 'qalert_app'@'%';



drop procedure if exists sp_get_payment;
DELIMITER ;;
CREATE PROCEDURE sp_get_payment(
	vi_payment_code			char(17)
)
BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2026-02-28
	-- Objetivo: 	get a payment
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_get_payment('');
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************    
    
	select p.user_id
		, p.payment_id
		, p.payment_code
		, payment_order_id
		, p.amount
		, c.currency_code
        , s.status_id
        , s.status_code
        , s.name as status_name
    from payment p
		inner join currency c on c.currency_id = p.currency_id
        left join status s on s.status_type_id = 6
			and s.status_id = p.payment_status_id
    where p.payment_code = vi_payment_code
	limit 1;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_get_payment   to 'qalert_app'@'%';




drop procedure if exists sp_get_subscriptions;
DELIMITER ;;
CREATE PROCEDURE sp_get_subscriptions()
BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2026-02-28
	-- Objetivo: 	get a payment
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_get_subscriptions();
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************    
    
    with subscription_cte as (
		select s.subscription_id
			, s.subscription_months
			, p.price as price_without_discount
			, p.price - s.discount as price_with_discount
		from subscription s
			inner join product p on p.product_id = s.product_id
				and p.product_status_id = 6
		where s.subscription_status = 1
	)
    select x.subscription_id
		, x.subscription_months
        , x.price_without_discount
        , x.price_with_discount
		, ROUND(100 - (x.price_with_discount * 100) / x.price_without_discount, 2) as discount_percentage
    from subscription_cte x
    order by x.subscription_months asc;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_get_subscriptions   to 'qalert_app'@'%';



drop procedure if exists sp_update_user_subscription;
DELIMITER ;;
CREATE PROCEDURE sp_update_user_subscription(
	ni_user_id bigint
)
BEGIN

	-- ***************************************************************************
	-- Versión:		1.0
	-- Autor: 		Cristhian Díaz
	-- Fecha:  		2026-02-28
	-- Objetivo: 	get a payment
	-- ------------------------------------------------------------
	-- Ejemplo de uso
	-- call sp_get_payment('');
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************    
    
    declare n_payment_id		bigint;
    declare n_subscription_id 	int default 1;
    declare d_expire_at 		datetime;
    
    
    select p.payment_id
		, s.subscription_id
		, DATE_ADD(p.created_date, INTERVAL s.subscription_months MONTH) AS fecha_vencimiento
	into n_payment_id
		, n_subscription_id
		, d_expire_at
	from payment p 
		inner join payment_detail pd on pd.payment_id = p.payment_id
		inner join subscription s on s.product_id = pd.product_id
	where p.user_id = ni_user_id
		and p.payment_status_id = 9
	limit 1;
    
    
    update user_subscription 
    set status = 0
    where user_id = ni_user_id
		and status = 1;
    
    
	insert into user_subscription(subscription_id,
		user_id,
		expires_at,
        payment_id)
	values(n_subscription_id,
		ni_user_id,
        d_expire_at,
        n_payment_id);
    
    
    UPDATE user
    SET subscription_id = n_subscription_id
    WHERE user_id = ni_user_id;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_update_user_subscription   to 'qalert_app'@'%';