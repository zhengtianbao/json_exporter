#!/bin/bash
if [ ! -d "/var/lib/mysql/json_exporter" ]; then
    mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
    mysqld --daemonize --user=mysql
    sleep 5s
    mysql -uroot -e "create database json_exporter default charset 'utf8' collate 'utf8_bin'; grant all on json_exporter.* to 'root'@'127.0.0.1' identified by '123456'; flush privileges;"
else
    mysqld --daemonize --user=mysql
fi


/usr/sbin/nginx &
cd /usr/share/nginx/html/ && java -jar json_exporter-0.0.1-SNAPSHOT.jar &

echo "ALL start!!!"
tail -f /dev/null
