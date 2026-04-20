select x.http_status_code
	, x.error_
    , x.request_header
    , x.request_body
	, x.response_body
    , ''
    , e.endpoint_id
    , e.endpoint_name
    , x.*
from log_service x 
	left join endpoint e on e.endpoint_id = x.endpoint_id
where 1 = 1
	-- and x.begin_date = '2026-01-04'
	-- and x.begin_time > '19:37:30'
order by x.log_service_id desc
limit 5;


select * from status;
select * from status_type;


select * from endpoint where endpoint_name like '%scan%';
select * from user_subscription;
select * from master;


select * from tmp_validate_email;
select * from user;
select * from profile;
select * from suggestions;
select * from subscription;
select * from currency;


select * from tmp_scan_header order by 1 desc limit 10;
select * from tmp_scan_detail order by 1 desc limit 10;


select * from scan_header order by 1 desc limit 10;
select * from scan_detail order by 1 desc limit 10;



select p.payment_error, p.payment_id, p.payment_code, p.payment_order_id
    , u.user_id, u.username
    , c.currency_id, c.currency_name
    , p.amount
    , p.payment_status_id
    , s.name, s.status_code, ss.subscription_id
    , pr.product_id, pr.product_code, pr.product_name
    , pd.quantity
    , pd.unit_price
    , pd.total_discount
    , pd.total_amount
    , p.created_date
    , p.created_time
from payment p
	inner join user u on u.user_id = p.user_id
    inner join currency c on c.currency_id = p.currency_id
    inner join status s on s.status_id = p.payment_status_id
    left join payment_detail pd on pd.payment_id = p.payment_id
    left join product pr on pr.product_id = pd.product_id
	left join subscription ss on ss.product_id = pd.product_id
order by p.payment_id desc
limit 3;