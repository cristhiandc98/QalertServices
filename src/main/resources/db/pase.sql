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