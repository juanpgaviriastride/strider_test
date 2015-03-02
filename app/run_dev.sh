#!/usr/bin/env bash

export NODE_ENV=dev 
export NODE_PATH=modules 
nodemon -w config -w modules -w app.coffee -w lib -i modules/webserver/frontend -i modules/webserver/public app.coffee

