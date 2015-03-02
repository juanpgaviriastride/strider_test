redis-server:
  pkg:
    - installed
  service:
    - running
    - watch:
      - file: /etc/redis/redis.conf

/etc/redis/redis.conf:
  file.managed:
    - source: salt://redis/conf/redis.conf
    - mode: 644