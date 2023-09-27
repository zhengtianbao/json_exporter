# json_exporter_frontend

[json_exporter](https://github.com/zhengtianbao/json_exporter) 前端页面

`jsonpath`目录是由[jsonpath-online-evaluator](https://github.com/zhengtianbao/jsonpath-online-evaluator)构建出来的静态文件

## Requirements
- nodejs v6.11.5

## Build Setup

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# serve with hot reload at localhost:3010
npm run dev -- --port 3010

# build for production with minification
npm run build

# build docker image
docker build -t json_exporter_frontend .
```

## Run
通过docker-compose集成前后端运行

后端json_exporter暴露端口为`8080`，nginx反代后端路径为`/backend`， 因此后端服务启动需要指定`SERVER_SERVLET_CONTEXT_PATH=/backend`
详情查看`docker-compose.yml`和`nginx.conf`文件

启动服务:
```bash
 $ docker-compose up -d
```
