-- name: create-user<!
-- creates a new user record
INSERT INTO users
(name, email, birthday, gender, admin, last_login, is_active, pass)
VALUES (:name, :email, :birthday, :gender, :admin, :last_login, :is_active, :pass)


-- name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT name, email, birthday, gender, admin, last_login, is_active FROM users
WHERE id = :id

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id

--name: user-soft-delete!
--soft delete of a user on the system
UPDATE users
SET is_active = false
WHERE id = :id

--name: get-password
--gets the salted password given the email
SELECT pass FROM users
WHERE email = :email
