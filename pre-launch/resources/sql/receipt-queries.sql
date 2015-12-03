--name: get-receipt
--finds all the info regarding a payment
SELECT u.name, sc.amount, sc.id, sc.created, sc.source
FROM users u, customer_stripe_accounts csa, stripe_charges sc 
WHERE u.id = :user_id
	AND u.stripe_account_id = csa.id
	AND csa.stripe_id = sc.customer
