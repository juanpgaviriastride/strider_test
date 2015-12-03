--name: create-invitation-request<!
--creates a new association
INSERT INTO invitation_requests
(email,
referer_id)
VALUES(
:email,
:referer_id)

--name: get-invitation-requests
--finds invitation requests
SELECT count(email) AS total FROM invitation_requests

