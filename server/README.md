# server

[json_exporter](https://github.com/zhengtianbao/json_exporter) 后端服务

## Requirements

- java 17

## Build

```shell
./mvnw -DskipTests package
```

## Run

```shell
java -jar target/json_exporter:0.0.1-SNAPSHOT.jar
```

## 数据库初始化

```shell
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=123456 -v /mysqldata:/var/lib/mysql -p 3306:3306 mysql:8
```

```sql
create database json_exporter DEFAULT CHARSET utf8mb4;
use json_exporter;
source src/main/resources/json_exporter.sql
```
