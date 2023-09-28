#!/bin/bash

if [ ! -d "/var/lib/mysql/json_exporter" ]; then
    mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
    mysqld --daemonize --user=mysql
    sleep 5
    mysql -uroot < /opt/json_exporter.sql
    mysql -uroot -e "GRANT ALL PRIVILEGES ON json_exporter.* TO 'root'@'localhost' IDENTIFIED BY '123456'; flush privileges;"
else
    mysqld --daemonize --user=mysql
fi

/usr/sbin/nginx &
cd /usr/share/nginx/html/json_exporter && java -jar json_exporter-0.0.1-SNAPSHOT.jar &

echo "ALL start!!!"
tail -f /dev/null
