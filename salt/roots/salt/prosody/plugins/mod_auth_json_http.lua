-- HTTP authentication using REST-like json based API authentication
-- e.g. node.js passport local strategy
--
-- author: Sebastian Castillo <castillobuiles@gmail.com>
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.

-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.

-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.

local name = "HTTP json auth";
local log = require "util.logger".init("auth_json_http");
local json = require "util.json";
local http = require "socket.http";
local ltn12 = require "ltn12";
local util_sasl_new = require "util.sasl".new;


local auth_token_url = module:get_option_string("auth_token_url");
local auth_credentials_url = module:get_option_string("auth_credentials_url");

assert(auth_token_url, "auth_token_url setting missing");
assert(auth_credentials_url, "auth_credentials_url setting missing");

function auth(url, headers, body, method)
   local headers = headers or {};
   local body = body or "";

   headers["Content-Type"] = "application/json";
   headers["Content-Length"] = tostring(#body);
   local respbody = {};

   log("debug", "URL: %s", url);

   local result, respcode, respheaders, respstatus = http.request{
      method = method,
      url = url,
      headers = headers,
      source = ltn12.source.string(body),
      sink = ltn12.sink.table(respbody)
   }


   if type(respcode) == "number" and respcode >= 200 and respcode <= 299 then
      return true;
   else
      log("debug", "HTTP authentication returned with code: %s", respcode);
      return nil, "Unauthorized."
   end
end

function auth_token(token)
   local url = auth_token_url;
   local headers = {
      ["Authorization"] = "Bearer "..token;
   };
   local body = "";
   local method = "GET";

   return auth(url, headers, body, method);
end

function auth_credentials(username, password)
   local url = auth_credentials_url;
   local headers = {};
   local body = json.encode({ username = username, password = password });
   local method = "POST";

   return auth(url, headers, body, method);
end


local provider = {};

-- for 0.8
--provider = {
--	name = module.name:gsub("^auth_","");
--};


function provider.get_sasl_handler()
   local getpass_authentication_profile = {
      plain_test = function(sasl, username, password, realm)
         local token = password;
         return auth_token(token) or auth_credentials(username, password), true
      end
   };
   return util_sasl_new(module.host, getpass_authentication_profile);
end


-- Non implemented

function provider.create_user(username, password)
   return nil, "Not implemented"
end

function provider.delete_user(username)
   return nil, "Not implemented"
end

function provider.set_password(username, password)
   return nil, "Not implemented"
end

function provider.test_password(username, password)
   return nil, "Not implemented"
end

function provider.get_password(username)
   return nil, "Not implemented"
end

function provider.user_exists(username)
   return true;
end

-- for 0.8
--module:add_item("auth-provider", provider);

module:provides("auth", provider)
