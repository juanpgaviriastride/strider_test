-- name: create-invitation<!
-- creates a new invitation record
INSERT INTO invitations
(inviter_id, email)
VALUES (:inviter_id, :email)

--name: get-invitation
--queries for an invitation object by the id
SELECT id, inviter_id, email FROM invitations
WHERE id = :id

--name: delete-invitation!
--deletes a session from the token
DELETE FROM invitations 
WHERE id = :id
