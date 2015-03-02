lua-bitop:
  pkg.latest

prosody:
  pkg:
    - installed
  service:
    - running
    - require:
       - pkg: lua-bitop
    - watch:
       - pkg: prosody
       - file: /etc/prosody/prosody.cfg.lua
       - file: /usr/lib/prosody/modules/mod_auth_json_http.lua
       - file: /usr/lib/prosody/modules/mod_websocket.lua
       - file: /usr/lib/prosody/modules/mod_carbons.lua

/etc/prosody/prosody.cfg.lua:
  file.managed:
    - source: salt://prosody/conf/prosody.cfg.lua
    - mode: 644
    - template: jinja
    - auth_credentials_url: "http://192.168.50.1:3000/api/v1/auth/local/"
    - auth_token_url: "http://192.168.50.1:3000/api/v1/auth/token/"

/usr/lib/prosody/modules/mod_auth_json_http.lua:
  file.managed:
    - source: salt://prosody/plugins/mod_auth_json_http.lua

/usr/lib/prosody/modules/mod_websocket.lua:
  file.managed:
    - source: salt://prosody/plugins/mod_websocket.lua

/usr/lib/prosody/modules/mod_carbons.lua:
  file.managed:
    - source: salt://prosody/plugins/mod_carbons.lua
