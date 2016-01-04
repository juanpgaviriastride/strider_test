-- user email (varchar)
CREATE UNIQUE INDEX user_email_index ON users(email(50)) USING BTREE;
--;;
-- user titan id (integer)
ALTER TABLE users ADD INDEX user_titan_id_index (titan_id);
--;;
-- invitation request email (varchar)
CREATE UNIQUE INDEX invitation_request_email_index ON invitation_requests(email(50)) USING BTREE;
