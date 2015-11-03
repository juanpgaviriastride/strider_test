-- name: create-user!
-- creates a new user record
INSERT INTO users
(name, email, birthday, gender, admin, last_login, is_active, pass)
VALUES (:name, :email, :birthday, :gender, :admin, :last_login, :is_active ,:pass)

-- name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users
WHERE email = :email

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id
