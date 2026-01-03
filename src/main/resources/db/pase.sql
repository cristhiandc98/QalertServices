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