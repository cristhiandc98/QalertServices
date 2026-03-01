drop table if exists payment_detail;
drop table if exists payment;
drop table if exists payment_status;
drop table if exists currency;

ALTER TABLE user DROP FOREIGN KEY fk_user__subscription;
DROP TABLE IF EXISTS user_subscription;
DROP TABLE IF EXISTS subscription;
drop table if exists product;


delete from status where status_type_id = 5;
delete from status where status_type_id = 6;
delete from status_type where status_type_id = 5;
delete from status_type where status_type_id = 6;

insert into status_type(status_type_id, name)values(5, 'product');
insert into status(status_id, status_type_id, name)values(6, 5, 'active'), (7, 5, 'inactive');

insert into status_type(status_type_id, name)values(6, 'payment');
insert into status(status_type_id, status_id, status_code, name)values
(6, 8, 'PENDING',   'Pago creado, esperando confirmación'),
(6, 9, 'PAID',      'Pago confirmado exitosamente'),
(6, 10, 'FAILED',    'Pago rechazado o fallido'),
(6, 11, 'CANCELLED', 'Pago cancelado por el usuario'),
(6, 12, 'EXPIRED',   'Sesión de pago expirada'),
(6, 13, 'REFUNDED',  'Pago reintegrado'),
(6, 14, 'ERROR_GENERATE_URL',  'Error durante la generación de URL de pago');



create table product(
	product_id			int,
    product_code 		CHAR(11),
    product_name		varchar(100),
    price				DECIMAL(10, 2),
    product_description	varchar(200),
    product_status_id	int default 6,
    created_datetime 	DATETIME DEFAULT CURRENT_TIMESTAMP,
    constraint pk_product primary key(product_id),
    constraint fk_product__status foreign key(product_status_id) references status(status_id),
    constraint uk_product__code unique(product_code)
);
GRANT SELECT ON qalert_bd.product                TO 'qalert_app'@'localhost';
INSERT INTO product(product_id, product_code, product_name,
	price, product_description)
values(1, 'QALERT-0001', 'QALERT PREMIUM', 
	100, 'versión premium del app');



CREATE TABLE currency (
    currency_id 		int,
    currency_code 		VARCHAR(3) NOT NULL UNIQUE,
    currency_name 		VARCHAR(50) NOT NULL,
    symbol 				VARCHAR(5) NOT NULL,
    constraint pk_currency primary key(currency_id)
);
INSERT INTO currency (currency_id, currency_code, currency_name, symbol) VALUES
(1, 'PEN', 'SOL', 'S/');
GRANT SELECT, INSERT, UPDATE ON qalert_bd.currency                TO 'qalert_app'@'localhost';



CREATE TABLE payment (
    payment_id 					BIGINT AUTO_INCREMENT,
    payment_code				char(17),
    user_id 					BIGINT NOT NULL,

    amount 						DECIMAL(10,2),
    currency_id 				int NOT NULL DEFAULT 1,

    payment_status_id			int DEFAULT 8,

    created_date 				DATE DEFAULT (CURRENT_DATE),
    created_time 				TIME DEFAULT (CURRENT_TIME),
    updated_at 					DATETIME NULL,
    
    payment_error				text,
	
    constraint pk_payment primary key(payment_id),
    CONSTRAINT fk_payment__currency FOREIGN KEY (currency_id) REFERENCES currency(currency_id),
    CONSTRAINT fk_payment__payment_status FOREIGN KEY (payment_status_id) REFERENCES status(status_id),
    CONSTRAINT fk_payment__user FOREIGN KEY (user_id) REFERENCES user(user_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.payment                TO 'qalert_app'@'localhost';



CREATE TABLE payment_detail (
	payment_id  bigint not null,
	product_id	int not null,
    quantity 	INT NOT NULL,
    unit_price 	DECIMAL(10,2) NOT NULL,
    discount	DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    constraint pk_payment_detail primary key(payment_id, product_id),
    constraint fk_payment_detail__product foreign key(product_id) references product(product_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.payment_detail                TO 'qalert_app'@'localhost';



CREATE TABLE subscription (
    subscription_id 	INTEGER not null,
    product_id			int not null,
    total_discount 		DECIMAL(10, 2),
    subscription_months INT,
    created_datetime 	DATETIME DEFAULT CURRENT_TIMESTAMP,
    status 				BIT(1) DEFAULT b'1',
    CONSTRAINT pk_subscription PRIMARY KEY (subscription_id),
    constraint fk_subscription__product foreign key(product_id) references product(product_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.subscription                TO 'qalert_app'@'localhost';
insert into subscription(subscription_id, product_id, total_discount, subscription_months) values
(1, 1, 0, 1),
(2, 1, 20, 6),
(3, 1, 50, 12);



ALTER TABLE user ADD CONSTRAINT fk_user__subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id);



CREATE TABLE user_subscription (
    user_subscription_id BIGINT NOT NULL AUTO_INCREMENT,
    subscription_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    created_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at	DATETIME,
    status BIT(1) DEFAULT b'1',
    CONSTRAINT pk_user_subscription PRIMARY KEY (user_subscription_id),
    CONSTRAINT fk_user_subscription__subscription FOREIGN KEY (subscription_id) REFERENCES subscription(subscription_id),
    CONSTRAINT fk_user_subscription__user FOREIGN KEY (user_id) REFERENCES user(user_id)
);
GRANT SELECT, INSERT, UPDATE ON qalert_bd.user_subscription                TO 'qalert_app'@'localhost';



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
    
    
    insert into payment_detail(payment_id, product_id, quantity, unit_price, discount, total_amount)
    select @last_id, p.product_id, s.subscription_months, p.price, s.total_discount, (s.subscription_months * p.price - s.total_discount)
    from subscription s
		inner join product p on p.product_id = s.product_id
			and p.product_status_id = 6
    where s.subscription_id = ni_subscription_id	
		and s.status = 1;
        
              
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
grant execute on procedure qalert_bd.sp_insert_payment   to 'qalert_app'@'localhost';



drop procedure if exists sp_update_payment;
DELIMITER ;;
CREATE PROCEDURE sp_update_payment(
	ni_payment_id				bigint,
	ni_payment_status_id		int,
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
    
	UPDATE payment p
	SET p.payment_status_id = ni_payment_status_id
		, updated_at = CURRENT_TIMESTAMP()
        , payment_error = vi_payment_error
	WHERE p.payment_id = ni_payment_id;
    
END;;
DELIMITER ;
grant execute on procedure qalert_bd.sp_update_payment   to 'qalert_app'@'localhost';