# Quick Framework

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-brightgreen.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.6-brightgreen.svg)](https://spring.io/projects/spring-boot)

基于 **Spring Boot 3.x** + **Java 17** 开发的企业级快速开发框架，提供丰富的中间件集成和常用功能模块，助力开发者快速构建高质量的企业应用。

## ✨ 核心特性

### 🏗️ 架构设计

- **模块化设计** - 按功能划分模块，支持按需引入
- **自动配置** - 基于 Spring Boot 自动配置，开箱即用
- **统一规范** - 提供统一的开发规范和最佳实践
- **扩展性强** - 支持自定义扩展和二次开发

### 🔧 核心功能

- **安全认证** - 基于 JWT 的统一认证授权
- **缓存管理** - 多级缓存抽象，支持 Redis、JetCache
- **数据加密** - 敏感数据加密传输和存储
- **幂等控制** - 分布式环境下的接口幂等性保证
- **分布式锁** - 基于 Redis 的分布式锁实现
- **统一日志** - 结构化日志和链路追踪

### 🌐 Web 功能

- **API 文档** - 基于 Swagger 3.0 的接口文档
- **WebSocket** - 实时双向通信支持
- **SSE** - 服务器推送事件
- **统一响应** - 标准化的 API 响应格式
- **异常处理** - 全局异常处理和错误码管理

### 🔄 中间件集成

- **消息队列** - RabbitMQ 消息队列集成
- **数据同步** - Canal 数据变更监听
- **对象存储** - AWS S3 对象存储支持
- **邮件服务** - 邮件发送功能
- **搜索引擎** - Elasticsearch 集成

### 🎯 设计模式

- **状态机** - 基于 COLA 的状态机实现
- **策略模式** - 可插拔的业务策略
- **责任链** - 请求处理链
- **建造者** - 对象构建模式

## 🛠️ 技术栈

| 技术                       | 版本             | 说明        |
|--------------------------|----------------|-----------|
| **Spring Boot**          | 3.1.6          | 核心框架      |
| **Spring Cloud**         | 2022.0.3       | 微服务框架     |
| **Spring Cloud Alibaba** | 2022.0.0.0-RC2 | 阿里云组件     |
| **Java**                 | 17+            | 编程语言      |
| **Maven**                | 3.6+           | 构建工具      |
| **Redis**                | -              | 缓存和分布式锁   |
| **RabbitMQ**             | -              | 消息队列      |
| **MySQL**                | -              | 关系型数据库    |
| **Elasticsearch**        | -              | 搜索引擎      |
| **JetCache**             | 2.7.5          | 多级缓存      |
| **Redisson**             | 3.24.3         | Redis 客户端 |
| **MyBatis Plus**         | 3.5.3.1        | ORM 框架    |
| **Knife4j**              | 4.3.0          | API 文档    |
| **Hutool**               | 5.8.35         | 工具类库      |
| **MapStruct**            | 1.5.5.Final    | 对象映射      |
| **XXL-Job**              | 2.3.1          | 分布式任务调度   |

## 📁 项目结构

```
quick-framework/
├── dependencies/           # 依赖管理模块
│   └── pom.xml            # 统一依赖版本管理
├── frameworks/            # 框架核心模块
│   ├── cache/            # 缓存抽象层
│   ├── canal/            # 数据同步工具
│   ├── common/           # 通用工具包
│   ├── database/         # 数据库配置
│   ├── designpattern/    # 设计模式实现
│   ├── encrypt/          # 数据加密
│   ├── es/               # Elasticsearch
│   ├── idempotent/       # 幂等性控制
│   ├── job/              # 任务调度
│   ├── lock/             # 分布式锁
│   ├── log/              # 统一日志
│   ├── mail/             # 邮件服务
│   ├── mq/               # 消息队列
│   ├── oss/              # 对象存储
│   ├── security/         # 安全框架
│   ├── sse/              # 服务器推送事件
│   ├── swagger/          # API 文档
│   ├── web/              # Web 层核心
│   ├── websocket/        # WebSocket 通信（Spring MVC）
│   └── websocket-webflux/ # WebSocket 通信（WebFlux）
├── service-demo/         # 示例服务
│   ├── controller/       # 控制器示例
│   ├── service/          # 服务层示例
│   ├── config/           # 配置示例
│   └── resources/        # 配置文件
├── script/               # 数据库脚本
├── pom.xml               # 父模块 POM
└── README.md             # 项目文档
```

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- Redis 6.0+
- MySQL 8.0+ (可选)
- RabbitMQ 3.8+ (可选)

### 2. 克隆项目

```bash
git clone https://github.com/Linweixinyo/quick-framework.git
cd quick-framework
```

### 3. 构建项目

```bash
mvn clean install
```

### 4. 作为 Maven Archetype 使用

当前仓库提供 `quick-framework-archetype` 模块，可在本地安装后生成新的多模块框架源码项目。

Archetype 使用独立 POM，不依赖当前框架的父 POM或 `dependencies` BOM。安装后可直接从其他目录生成项目。

仓库默认不提交 `archetype-resources` 生成物。使用前需要先在本地同步模板资源，再安装 Archetype：

```powershell
./script/sync-archetype-resources.ps1
mvn -pl 'archetype' install
```

`archetype-resources` 由当前源码生成，后续框架源码变化后需要重新执行同步脚本。

进入新项目的父目录，再生成新项目。不要在当前模板项目根目录执行，否则 Maven 会将新项目注册为当前项目的子模块。

```powershell
Set-Location '项目路径'

mvn archetype:generate `
  '-DarchetypeCatalog=local' `
  '-DarchetypeGroupId=org.weixin.framework' `
  '-DarchetypeArtifactId=quick-framework-archetype' `
  '-DarchetypeVersion=1.0-SNAPSHOT' `
  '-DgroupId=com.example.demo' `
  '-DartifactId=demo-framework' `
  '-Dversion=1.0-SNAPSHOT' `
  '-Dpackage=com.example.demo' `
  '-DprojectName=Demo Framework' `
  '-DdeveloperName=developer' `
  '-DdeveloperEmail=developer@example.com' `
  '-DdeveloperUrl=https://example.com' `
  '-DrepositoryUrl=https://example.com/demo-framework' `
  '-DinteractiveMode=false'
```

上述示例会生成 `项目路径/demo-framework`。`artifactId` 同时作为生成目录名。

`package` 默认可与 `groupId` 保持一致；如果需要不同 Java 基础包名，可在生成时显式覆盖。

### 5. 创建业务模块

在父模块下创建新的 Maven 模块：

```xml

<parent>
    <artifactId>quick-framework</artifactId>
    <groupId>org.weixin.framework</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>

<artifactId>your-service</artifactId>
```

### 6. 引入所需模块

```xml

<dependencies>
    <!-- Web 核心 -->
    <dependency>
        <groupId>org.weixin.framework</groupId>
        <artifactId>web</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <!-- API 文档 -->
    <dependency>
        <groupId>org.weixin.framework</groupId>
        <artifactId>swagger</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <!-- 安全框架 -->
    <dependency>
        <groupId>org.weixin.framework</groupId>
        <artifactId>security</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <!-- 缓存 -->
    <dependency>
        <groupId>org.weixin.framework</groupId>
        <artifactId>cache</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 7. 配置文件

```yaml
server:
  port: 8080

spring:
  application:
    name: your-service
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

framework:
  swagger:
    config:
      title: Your Service API
      description: Your service description
      version: v1.0.0
      author: Your Name
      email: your.email@example.com

  security:
    config:
      token-prefix: Bearer
      header: Authorization
      parameter: token
      ignore-urls:
        - /public/**
        - /swagger-ui/**

  cache:
    prefix: your-service
    prefix-charset: UTF-8
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 作者：weixin
- 邮箱：1320627222@qq.com
- GitHub：https://github.com/Linweixinyo

## ⭐ Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Linweixinyo/quick-framework&type=date&legend=top-left)](https://www.star-history.com/#Linweixinyo/quick-framework&type=date&legend=top-left)

---

如果这个项目对你有帮助，请给它一个 ⭐ Star！
