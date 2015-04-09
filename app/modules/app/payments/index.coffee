HTTPStatus = require "http-status"
# need to add the key to the configuration
# we also need an actived account to be able to setup stripe connect
# and within the platform settings - turn on the managed accounts
# We also need the integration with Stripe.js once we start working on the front end for this
# we need the tokens to ensure / accounts linking via stripe-connect
var stripe = require('stripe')('sk_test_xNQypy8qLCa95GqqSF2x2Dz6');

class Payments
  account_for_user: (user, cb) =>
    if user.stripe_account_id.present?
      stripe.account.retrieve(user.stripe_account_id, (err, account) =>
        cb(err, account)
      )
    #retrive or create accordingly need the stripe connect for this

  pay_from_account: (amount, description, account, cb) =>
    stripe.charges({
      amount: amount,
      currency: "usd",
      customer: account.id,
      description: description
      }, (err, charge) =>
        cb(err, charge)      
      )

  add_to_account: (amount, description, account, cb) =>
    stripe.transfer.create({
      amount: amount,
      currency: "usd",
      destination: account.id,
      description: description
      }, (err, transfer) =>
        cb(err, transfer)
    )
      
  payments_history: (account, cb) =>
    stripe.charges.list({customer: account.id}, (err, charges) =>
      cb(err, charges)
    )

  additions_history: (account, cb) =>
    stripe.transfers.list({recipient: account.id}, (err, transfers) =>
      cb(err, transfers)
    )
    

module.exports = Payments
