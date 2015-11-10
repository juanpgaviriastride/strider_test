-- name: create-user<!
-- creates a new user record
INSERT INTO users
(name,
email,
birthday,
profession,
avatar_url,
type,
city_name,
job_title,
former_job_title,
place_of_employment,
former_place_of_employment,
start_date,
end_date,
location,
ocupation,
industry,
seeking_job,
phone,
degree,
field_of_study,
school_name,
facebook_profile,
linkedin_profile,
gender,
admin,
last_login,
is_active,
password)
VALUES (
:name,
:email,
:birthday,
:profession,
:avatar_url,
:type,
:city_name,
:job_title,
:former_job_title,
:place_of_employment,
:former_place_of_employment,
:start_date,
:end_date,
:location,
:ocupation,
:industry,
:seeking_job,
:phone,
:degree,
:field_of_study,
:school_name,
:facebook_profile,
:linkedin_profile,
:gender,
:admin,
:last_login,
:is_active,
:password)


-- name: update-user!
-- update an existing user record
UPDATE users
SET
name = :name,
email = :email,
birthday = :birthday,
profession = :profession,
avatar_url = :avatar_url,
type = :type,
city_name = :city_name,
job_title = :job_title,
former_job_title = :former_job_title,
place_of_employment = :place_of_employment,
former_place_of_employment = :former_place_of_employment,
start_date = :start_date,
end_date = :end_date,
location = :location,
ocupation = :ocupation,
industry = :industry,
seeking_job = :seeking_job,
phone = :phone,
degree = :degree,
field_of_study = :field_of_study,
school_name = :school_name,
facebook_profile = :facebook_profile,
linkedin_profile = :linkedin_profile,
gender = :gender
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT
id,
name,
email,
birthday,
profession,
avatar_url,
type,
city_name,
job_title,
former_job_title,
place_of_employment,
former_place_of_employment,
start_date,
end_date,
location,
ocupation,
industry,
seeking_job,
phone,
degree,
field_of_study,
school_name,
facebook_profile,
linkedin_profile,
gender
FROM users
WHERE email = :email AND is_active = true


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
SELECT password FROM users
WHERE email = :email
