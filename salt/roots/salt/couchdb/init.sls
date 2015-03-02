couchdb:
  pkg:
    - installed
  service:
    - running
    - watch:
      - file: /etc/couchdb/local.ini

/etc/couchdb/local.ini:
  file.managed:
    - source: salt://couchdb/conf/local.ini
    - mode: 644
