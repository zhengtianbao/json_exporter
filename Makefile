SHELL = /bin/bash

#项目名称
PROJECT_NAME        = json_exporter
#镜像名称
REPOSITORY          = docker.io/zhengtianbao/${PROJECT_NAME}

ifeq ($(TAGS_OPT),)
TAGS_OPT            = latest
else
endif

#本地环境打包前后端
build:
	if [ -d "build" ];then rm -rf build; fi \
	&& make build-web-local \
	&& make build-server-local \
	&& mkdir build \
	&& cp -r web/dist build/ \
	&& cp web/index.html build/ \
	&& cp -r web/jsonpath build/ \
	&& cp server/target/json_exporter-0.0.1-SNAPSHOT.jar build/

#本地环境打包前端
build-web-local:
	@cd web/ && if [ -d "dist" ];then rm -rf dist; fi \
	&& npm install && npm run build

#本地环境打包后端
build-server-local:
	@cd server/ && if [ -d "target" ];then rm -rf target; fi \
	&& ./mvnw install -DskipTests

#打包前后端二合一镜像
image: build 
	docker build -t ${REPOSITORY}:${TAGS_OPT} -f deploy/docker/Dockerfile .
