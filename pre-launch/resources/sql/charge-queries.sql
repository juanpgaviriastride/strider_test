-- name: create-charge<!
-- creates a new srtipe charge record
INSERT INTO stripe_charges
(amount,
captured,
created,
source,
customer,
balanceTransaction,
currency,
refunded,
amountRefunded,
status,
id,
paid,
fraudDetails,
livemode,
metadata)
VALUES(
:amount,
:captured,
:created,
:source,
:customer,
:balanceTransaction,
:currency,
:refunded,
:amountRefunded,
:status,
:id,
:paid,
:fraudDetails,
:livemode,
:metadata
)
