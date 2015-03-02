jsvc:
  pkg.latest

libjna-java:
  pkg.latest


cassandra-repo:
  cmd.run:
    - name: "gpg --keyserver pgp.mit.edu --recv-keys 2B5C1B00; gpg --export --armor 2B5C1B00 | sudo apt-key add -; gpg --keyserver pgp.mit.edu --recv-keys 0353B12C; gpg --export --armor 0353B12C | sudo apt-key add -;"
  pkgrepo.managed:
    - humanname: Cassandra
    - name: deb http://www.apache.org/dist/cassandra/debian 21x main
    - file: /etc/apt/sources.list.d/cassandra.apache.list
    - keyid: F758CE318D77295D
    - keyserver: pgp.mit.edu
    - require_in:
      - pkg: cassandra

  pkg.latest:
    - name: cassandra
    - refresh: True


cassandra:
  pkg.installed:
    - require_in:
      - sls: openjdk-7-jdk
  service:
   - running
   - require:
     - pkg: cassandra
   - watch:
     - file: /etc/cassandra/cassandra.yaml


/etc/cassandra/cassandra.yaml:
  file.managed:
    - source: salt://cassandra/conf/cassandra.yaml
    - mode: 644
    - template: jinja
    - cluster_name: 'Webtalk'
    - listen_address: '192.168.50.4'
    - seeds: '192.168.50.4'
    - rpc_address: '192.168.50.4'
