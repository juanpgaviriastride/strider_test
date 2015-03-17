include:
  - openjdk-7-jdk

elasticsearch-1_3:
  pkgrepo.managed:
    - humanname: ElasticSearch
    - name: deb http://packages.elasticsearch.org/elasticsearch/1.2/debian stable main
    - file: /etc/apt/sources.list.d/elasticsearch.list
    - key_url: http://packages.elasticsearch.org/GPG-KEY-elasticsearch
    - require_in:
      - pkg: elasticsearch

  pkg.latest:
    - name: elasticsearch
    - refresh: True

elasticsearch:
  pkg.installed:
    - require_in:
      - sls: openjdk-7-jdk
  service:
   - running
   - require:
     - pkg: elasticsearch
   - watch:
     - file: /etc/elasticsearch/elasticsearch.yml


/etc/elasticsearch/elasticsearch.yml:
  file.managed:
    - source: salt://elasticsearch/lib/elasticsearch.yml
    - mode: 644
    - template: jinja
    - master_nodes: ["192.168.50.4"]
    - cluster_name: "webtalk"
