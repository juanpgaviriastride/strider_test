CREATE TABLE customer_stripe_accounts
(
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
stripe_id VARCHAR(200),	       
email VARCHAR(150),
currency VARCHAR(50),
default_source VARCHAR(200),
description VARCHAR(100),
connect_access_token VARCHAR(200),
refresh_token VARCHAR(200),
token_type VARCHAR(100),
scope VARCHAR(100),
sources LONGTEXT
)
