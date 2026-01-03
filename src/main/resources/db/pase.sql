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

GRANT EXECUTE ON PROCEDURE qalert_bd.sp_insert_profile TO 'qalert_app'@'localhost';