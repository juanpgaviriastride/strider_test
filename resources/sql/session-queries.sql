-- name: create-session<!
-- creates a new session record
INSERT INTO sessions
(email, username, token, created_at)
VALUES (:email, :username, :token, NOW())

--name: get-session
--queries for a session object by the token
SELECT id, email, token FROM sessions
WHERE token = :token

--name: get-session-email
--queries for a session object by email
SELECT id, email, token FROM sessions
WHERE email = :email


--name: delete-session-token!
--deletes a session from the token
DELETE FROM sessions 
WHERE token = :token




