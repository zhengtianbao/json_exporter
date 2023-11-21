# json_exporter

转换HTTP服务暴露的任意JSON结构数据为prometheus格式的metrics指标，同时暴露对应endpoint用于prometheus采集。

预处理步骤可将XML结构数据提前转换为JSON结构。

## Requirements

- jdk 11
- nodejs v6.11.5

## Build

```shell
# 构建all-in-one镜像
make image

# run
docker run -d --name json_exporter -p8080:80 zhengtianbao/json_exporter:latest
```
