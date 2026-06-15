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

-- Si la tabla ya tiene esta columna, ignorar la operación.
ALTER TABLE qalert_bd_1.payment 
ADD COLUMN payment_order_id CHAR(32) NULL AFTER payment_code;

-- Si ya actulizaste la colummna, ignorar la operación.
UPDATE currency 
SET currency_code = 'USD'
WHERE currency_id = 1;