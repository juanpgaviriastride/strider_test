include:
  - openjdk-7-jdk

unzip:
  pkg.latest


/usr/local/src/titan-server-0.4.4.zip:
  file.managed:
    - source: salt://titan/src/titan-server-0.4.4.zip
    - makedirs: true

extract_titan:
  cmd.run:
    - name: "unzip titan-server-0.4.4.zip"
    - cwd: /usr/local/src/
    - unless: test -d /usr/local/src/titan-server-0.4.4
    - require:
      - pkg: unzip
      - file: /usr/local/src/titan-server-0.4.4.zip


/usr/local/src/titan-server-0.4.4/conf/rexster-cassandra-es.xml:
  file.managed:
    - source: salt://titan/conf/rexster-cassandra-es.xml
    - mode: 644
    - template: jinja
    - host: 'http://192.168.50.4'
    - listen_address: '0.0.0.0'
    - cassandra_host: '192.168.50.4'
    - elasticsearch_hosts: '192.168.50.4'
    - elasticsearch_cluster: 'webtalk'

/usr/local/src/titan-server-0.4.4/conf/titan-cassandra-es.properties:
  file.managed:
    - source: salt://titan/conf/titan-cassandra-es.properties
    - mode: 644
    - template: jinja
    - cassandra_host: '192.168.50.4'
    - elasticsearch_hosts: '192.168.50.4'
    - elasticsearch_cluster: 'webtalk'

/etc/init.d/titan:
  file.managed:
    - source: salt://titan/init.d/titan
    - mode: 755

titan:
  service:
    - running
    - require:
      - cmd: extract_titan
    - watch:
      - file: /etc/init.d/titan
      - file: /usr/local/src/titan-server-0.4.4/conf/rexster-cassandra-es.xml
