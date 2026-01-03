drop procedure if exists sp_insert_scan;
DELIMITER //
CREATE PROCEDURE sp_insert_scan(
	ni_user_id					bigint,
    vi_profile_id_concatenated  varchar(100),
    vi_separator				varchar(1),
    vi_product_name				varchar(100),
    vi_image_path				varchar(200)
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
    
    DECLARE d_current_date DATETIME DEFAULT CURRENT_TIMESTAMP();
    
    DROP TEMPORARY TABLE IF EXISTS tmp_profile;
	CREATE TEMPORARY TABLE tmp_profile AS
	SELECT CAST(jt.value AS UNSIGNED) AS profile_id
	FROM JSON_TABLE(
		CONCAT('["', REPLACE(vi_profile_id_concatenated, vi_separator, '","'), '"]'),
		'$[*]' COLUMNS (value VARCHAR(20) PATH '$')
	) jt;

    -- Insert header para TODOS los profiles
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
    d_current_date,
    vi_image_path
FROM tmp_scan_header h
JOIN tmp_profile p
WHERE h.user_id = ni_user_id;

    
INSERT INTO scan_detail (
    scan_header_id,
    additive_id,
    created_date,
    created_time
)
SELECT
    sh.scan_header_id,
    d.additive_id,
    d_current_date,
    d_current_date
FROM scan_header sh
JOIN tmp_scan_detail d
    ON d.user_id = ni_user_id
WHERE sh.created_date = d_current_date
  AND sh.user_id = ni_user_id;


    delete from tmp_scan_detail
    where user_id = ni_user_id;
    
    delete from tmp_scan_header
    where user_id = ni_user_id;
    
END;
//
DELIMITER ;
grant execute on procedure qalert_bd.sp_insert_scan   to 'qalert_app'@'localhost';


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

grant execute on procedure qalert_bd.sp_login to 'qalert_app'@'localhost';