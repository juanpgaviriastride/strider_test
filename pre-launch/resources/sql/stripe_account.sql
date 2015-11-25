-- name: create-stripe-account<!
-- creates a new customer stripe account
INSERT INTO customer_stripe_accounts
(
stripe_id,	       
email,
currency,
default_source,
description,
connect_access_token,
refresh_token,
token_type,
scope,
sources)
VALUES (
:stripe_id,	       
:email,
:currency,
:default_source,
:description,
:connect_access_token,
:refresh_token,
:token_type,
:scope,
:sources
)
