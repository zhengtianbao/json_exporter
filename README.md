# json_exporter

转换HTTP服务暴露的任意JSON结构数据为prometheus格式的metrics指标，同时暴露对应endpoint用于prometheus采集。

预处理步骤可将XML结构数据提前转换为JSON结构。

## Requirements

- jdk 11
- nodejs v6.11.5

## Build

构建all-in-one Jar 包

```shell
cd web 
npm run build
cp -r dist index.html server/src/main/resources/static/
cd ../server
./mvnw package
```

## run

```shell
cd server/target
java -jar json_exporter-0.0.1-SNAPSHOT.jar
```

## 启动修改配置

在json_exporter-0.0.1-SNAPSHOT.jar同级目录下新建application.yml

例如修改服务端口：

```yaml
server:
  port: 8080
```
