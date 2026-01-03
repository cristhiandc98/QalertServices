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
    
    -- obtener los datos a insertar
    -- cursor para recorrer todos los perfiles
    -- insertar la misma informacion para cada perfil
    
    DROP TEMPORARY TABLE IF EXISTS tmp_products;
	CREATE TEMPORARY TABLE tmp_products AS
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
	select (select profile_id from tmp_products limit 1),
		data,
        harmless_additives_number,
        medium_additives_number,
        harmful_additives_number,
        vi_product_name,
        d_current_date,
        d_current_date,
        vi_image_path
	from tmp_scan_header x 
	where x.user_id = ni_user_id;
    
    
    insert into scan_detail(scan_header_id,
		additive_id,
        created_date,
        created_time
    )
    SELECT 
        LAST_INSERT_ID(),
        x.additive_id,
        d_current_date,
        d_current_date
    from tmp_scan_detail x 
	where x.user_id = ni_user_id;


    delete from tmp_scan_detail
    where user_id = ni_user_id;
    
    delete from tmp_scan_header
    where user_id = ni_user_id;
    
END;
//
DELIMITER ;
grant execute on procedure qalert_bd.sp_insert_scan   to 'qalert_app'@'localhost';