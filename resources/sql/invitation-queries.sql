-- name: create-invitation<!
-- creates a new invitation record
INSERT INTO invitations
(inviter_id, email, token)
VALUES (:inviter_id, :email, :token)

--name: get-invitation
--queries for an invitation object by the id
SELECT id, inviter_id, email, token FROM invitations
WHERE id = :id

--name: find-invitation
--queries for an invitation object by the token
SELECT id, inviter_id, email, token FROM invitations
WHERE token = :token

--name: set-invitation-token!
--updates an invitation in order to update token
UPDATE invitations
SET token = :token
WHERE id = :id 

--name: delete-invitation!
--deletes a session from the token
DELETE FROM invitations 
WHERE id = :id
