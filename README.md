### 数据库

```shell
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=123456 -v /mysqldata:/var/lib/mysql -p 3306:3306 mysql:8
```

```sql
create database prometheus DEFAULT CHARSET  utf8mb4 COLLATE utf8mb4_general_ci;
```
