CREATE TABLE stripe_charges
(
identifier INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
amount BIGINT,
captured BOOLEAN, 
created BIGINT, 
source LONGTEXT,
customer VARCHAR(100),
balanceTransaction VARCHAR(100),
currency VARCHAR(50), 
refunded BOOLEAN, 
amountRefunded BIGINT, 
status VARCHAR(100), 
id VARCHAR(100),
paid BOOLEAN, 
fraudDetails LONGTEXT, 
livemode BOOLEAN, 
metadata LONGTEXT 
)
