CREATE TABLE sessions
(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 email VARCHAR(250) UNIQUE,
 username VARCHAR(250),
 token VARCHAR(250),
 user_id INT,
 created_at DATETIME
);
