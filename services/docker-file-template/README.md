# Docker 部署模板

该目录用于给新项目复制一套统一的 Docker 部署结构。模板参考
`services/docker-file` 的当前部署方式，但已经去掉项目专用服务名、镜像名、
端口和密码。

## 目录结构

```text
docker-file-template/
├── .env.infra.example
├── infra/
│   └── docker-compose-datasource.yml
├── service-template/
│   ├── .env.example
│   ├── Dockerfile-backend
│   └── docker-compose.yml
└── deploy/
    ├── deploy-all-single-server.sh
    ├── deploy-common.sh
    ├── deploy-infra-server.sh
    ├── deploy-service-server.sh
    └── services.conf.example
```

## 使用方式

1. 将本目录复制到目标项目的 `services/docker-file/` 或同类部署目录。
2. 复制公共环境文件：

```bash
cp .env.infra.example .env.infra
```

3. 为每个后端服务复制一份 `service-template`：

```bash
cp -r service-template api-service
cp -r service-template admin-service
```

4. 将每个服务手动打包后的 Jar 放入对应服务目录，并统一命名为 `app.jar`：

```text
api-service/app.jar
admin-service/app.jar
```

5. 为每个服务复制并调整环境文件：

```bash
cp api-service/.env.example api-service/.env
cp admin-service/.env.example admin-service/.env
```

关键变量需要按服务调整：

- `SERVICE_DIR`：服务目录名，例如 `api-service`。
- `SERVICE_IMAGE`：镜像名，例如 `api-service:latest`。
- `SERVICE_CONTAINER_NAME`：容器名，例如 `api-service`。
- `HOST_PORT`：宿主机暴露端口。
- `APP_PORT`：应用容器内监听端口。

6. 如果需要单台服务器部署多个服务，复制多服务声明文件：

```bash
cp deploy/services.conf.example deploy/services.conf
```

然后在 `deploy/services.conf` 中声明服务目录：

```bash
SERVICES=(
  "api-service"
  "admin-service"
)
```

## 部署命令

只部署基础设施：

```bash
bash deploy/deploy-infra-server.sh
```

部署单个服务：

```bash
bash deploy/deploy-service-server.sh api-service
```

单台服务器部署基础设施和多个服务：

```bash
bash deploy/deploy-all-single-server.sh
```

## 配置加载顺序

部署脚本会按顺序传入 Compose 环境文件：

1. 公共环境文件：`.env.infra`
2. 服务环境文件：`<service-dir>/.env`

后面的服务环境文件会覆盖公共环境文件中的同名变量。命令行已导出的环境变量仍可按 Docker Compose
自身规则覆盖 env 文件中的值。

## 约定

- 后端 Jar 由开发者手动打包，模板不在 Dockerfile 内执行 Maven 构建。
- 每个服务目录下的 Jar 固定命名为 `app.jar`，减少脚本解析复杂度。
- MySQL 使用 `mysql:8.0`。
- Redis 使用 `redis:7.2`。
- MySQL 和 Redis 数据使用 Docker named volumes 管理。
- 密码示例必须在目标项目部署前替换，不要直接用于生产环境。
