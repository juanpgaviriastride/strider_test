-- name: create-user<!
-- creates a new user record
INSERT INTO users
(name,
email,
admin,
last_login,
is_active,
stripe_account_id,
password)
VALUES (
:name,
:email,
:admin,
:last_login,
:is_active,
:stripe_account_id,
:password)




-- name: get-user
-- retrieve a user given the id.
SELECT
id,
name,
titan_id,
email
FROM users
WHERE email = :email


-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id

--name: user-soft-delete!
--soft delete of a user on the system
UPDATE users
SET is_active = false
WHERE id = :id 

--name: set-titan-id!
--save titan fk into user
UPDATE users
SET titan_id = :titan_id
WHERE email = :email 

--name: get-password
--gets the salted password given the email
SELECT password FROM users
WHERE email = :email

--name: user-count
SELECT count(email) AS total FROM users WHERE email = :email


--name: set-password!
--save titan fk into user
UPDATE users
SET password = :password
WHERE id = :id


--name: get-email-by-titan
--gets an email based on the titan-id
SELECT email, name FROM users WHERE titan_id = :titan_id
