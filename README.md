# json_exporter

转换 HTTP 服务暴露的任意 JSON 结构数据为 prometheus 格式的 metrics 指标，同时暴露对应 endpoint 用于 prometheus 采集。

预处理步骤可将 XML 结构数据提前转换为 JSON 结构。

## Requirements

- jdk 11
- nodejs v6.17.1

## Build

构建 all-in-one Jar 包

```shell
cd web
npm install
npm run build
mkdir ../server/src/main/resources/static
cp -r jsonpath dist index.html ../server/src/main/resources/static/
cd ../server
./mvnw package -DskipTests
```

## run

```shell
cd server/target
java -jar json_exporter-0.0.1-SNAPSHOT.jar
```

## 启动修改配置

在 json_exporter-0.0.1-SNAPSHOT.jar 同级目录下新建 application.yml

例如修改服务端口：

```yaml
server:
  port: 8080
```
