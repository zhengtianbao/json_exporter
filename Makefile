SHELL = /bin/bash

#java版本
BUILD_IMAGE_SERVER  = openjdk:11
#node版本
BUILD_IMAGE_WEB     = node:6.11.5
#项目名称
PROJECT_NAME        = json_exporter
#镜像名称
REPOSITORY          = docker.io/zhengtianbao/${PROJECT_NAME}

ifeq ($(TAGS_OPT),)
TAGS_OPT            = latest
else
endif

#容器环境前后端共同打包
build: build-web build-server
	docker run --name build-local --rm -v $(shell pwd):/src/${PROJECT_NAME} -w /src/${PROJECT_NAME} ${BUILD_IMAGE_SERVER} make build-local

#容器环境打包前端
build-web:
	docker run --name build-web-local --rm -v $(shell pwd):/src/${PROJECT_NAME} -w /src/${PROJECT_NAME} ${BUILD_IMAGE_WEB} make build-web-local

#容器环境打包后端
build-server:
	docker run --name build-server-local --rm -v $(shell pwd):/src/${PROJECT_NAME} -w /src/${PROJECT_NAME} ${BUILD_IMAGE_SERVER} make build-server-local

#本地环境打包前后端
build-local:
	if [ -d "build" ];then rm -rf build; else echo "OK!"; fi \
	&& if [ -f "/.dockerenv" ];then echo "OK!"; else make build-web-local && make build-server-local; fi \
	&& mkdir build && cp -r web/dist build/ && cp server/target/json_exporter-0.0.1-SNAPSHOT.jar build/ 

#本地环境打包前端
build-web-local:
	@cd web/ && if [ -d "dist" ];then rm -rf dist; else echo "OK!"; fi \
	&& npm set registry http://mirrors.cloud.tencent.com/npm/ && npm rebuild node-sass && npm install && npm run build

#本地环境打包后端
build-server-local:
	@cd server/ && if [ -d "target" ];then rm -rf target; else echo "OK!"; fi \
	&& ./mvnw install -DskipTests

#打包前后端二合一镜像
image: build 
	docker build -t ${REPOSITORY}:${TAGS_OPT} -f deploy/docker/Dockerfile .
