CREATE TABLE users
(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(250),
 email VARCHAR(50) UNIQUE,
 birthday DATE,
 gender VARCHAR(50),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(100));
