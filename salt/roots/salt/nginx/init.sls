nginx:
  pkg:
    - installed
  service:
    - running
    - watch:
       - pkg: nginx
       - file: /etc/nginx/nginx.conf
       - file: /etc/nginx/sites-available/webtalk

/etc/nginx/nginx.conf:
  file.managed:
    - source: salt://nginx/nginx.conf

/etc/nginx/sites-available/webtalk:
  file.managed:
    - source: salt://nginx/webtalk
