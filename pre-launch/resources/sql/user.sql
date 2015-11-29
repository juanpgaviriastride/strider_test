-- name: create-user<!
-- creates a new user record
INSERT INTO users
(name,
email,
admin,
last_login,
is_active,
password)
VALUES (
:name,
:email,
:admin,
:last_login,
:is_active,
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
