base:
  pkgrepo.managed:
    - humanname: MongoDB 2.6.1
    - name: deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen
    - file: /etc/apt/sources.list.d/mongodb.list
    - keyid: 7F0CEB10
    - keyserver: keyserver.ubuntu.com
    - require_in:
      - pkg: mongodb-org

  pkg.latest:
    - name: mongodb-org
    - refresh: True

mongodb-org:
  pkg.latest

mongod:
  service:
    - running
    - require:
      - pkg: mongodb-org
    - watch:
      - file: /etc/mongod.conf


/etc/mongod.conf:
  file.managed:
    - source: salt://mongodb/conf/mongodb.conf
