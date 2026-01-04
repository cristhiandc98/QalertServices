DROP PROCEDURE IF EXISTS sp_update_scan_product_name;
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

grant execute on procedure qalert_bd.sp_update_scan_header   to 'qalert_app'@'localhost';

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
    and x.status == 1;

END;
//

DELIMITER ;

grant execute on procedure qalert_bd.sp_get_scan_list   to 'qalert_app'@'localhost';


drop procedure if exists sp_get_additives_report;
DELIMITER //
CREATE PROCEDURE sp_get_additives_report(
    ni_profile_id 	BIGINT
  	,ni_report_type int
    ,ni_scan_header_id int
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
	-- 				call sp_get_additives_report(1, 0, 1);
	-- ------------------------------------------------------------
	-- Log
	-- Fecha			Autor		Cod. Mod.	Comentarios
    -- 
	-- ***************************************************************************
    
    declare d_current_date date default current_date();
    declare d_begin_date   date;
    declare d_end_date	   date default d_current_date;
		
        
	set d_begin_date = case ni_report_type  when 1 then d_current_date
											when 2 then DATE_SUB(d_current_date, INTERVAL 7 DAY)
                                            when 3 then DATE_SUB(d_current_date, INTERVAL 30 DAY)
                                            when 4 then DATE_SUB(d_current_date, INTERVAL 90 DAY)
						end;
                        

	-- ***********************************************************
	-- **************************************************header
	-- ***********************************************************
	select t.toxicity_level_id
		, t.name as toxicity_level
		, count(a.toxicity_level_id) as total
    , h.product_name
	from scan_detail d 
		inner join scan_header h on h.scan_header_id = d.scan_header_id
			and h.profile_id = ni_profile_id
      and h.status = 1
			
            -- buscar por producto
            and ((ni_report_type = 0 and h.scan_header_id = ni_scan_header_id)
            
            -- buscar por rango de fechas
				or (h.created_date between d_begin_date and d_end_date))
            
		inner join additive a on a.additive_id = d.additive_id
		right join toxicity_level t on t.toxicity_level_id = a.toxicity_level_id
	group by  t.toxicity_level_id;
	
    
	select d.additive_id
		, a.name
		, a.toxicity_level_id
		, count(1) total
	from scan_detail d 
		inner join additive a on a.additive_id = d.additive_id
		inner join scan_header h on h.scan_header_id = d.scan_header_id
			and h.profile_id = ni_profile_id
      and h.status = 1
			
            -- buscar por producto
            and ((ni_report_type = 0 and h.scan_header_id = ni_scan_header_id)
            
            -- buscar por rango de fechas
				or (h.created_date between d_begin_date and d_end_date))
	group by d.additive_id, a.name, a.toxicity_level_id
	order by a.toxicity_level_id asc, a.name
	;

END;
//
DELIMITER ;
grant execute on procedure qalert_bd.sp_get_additives_report   to 'qalert_app'@'localhost';