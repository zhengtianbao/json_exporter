# json_exporter

转换http服务暴露的任意json结构数据为prometheus格式的metrics指标，同时暴露对应endpoint用于prometheus采集。

前端项目地址: [json_exporter_frontend](https://github.com/zhengtianbao/json_exporter_frontend)

## Build

```shell
./mvnw package 
```

## Run

```shell
java -jar target/json_exporter:0.0.1-SNAPSHOT.jar
```

## Docker

```shell
docker build -t json_exporter:0.0.1-SNAPSHOT .
# or
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=json_exporter
```


```shell
docker run -p 8080:8080 json_exporter:0.0.1-SNAPSHOT
```

## 数据库初始化

```shell
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=123456 -v /mysqldata:/var/lib/mysql -p 3306:3306 mysql:8
```

```sql
create database prometheus DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
use prometheus;
source src/main/resources/prometheus.sql
```
