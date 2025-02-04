# quick-framework

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-brightgreen.svg)](https://maven.apache.org/)

基于SpringBoot3+Java17开发用于快速搭建项目的开发脚手架，提供开发基础的工具和配置，集成常见中间件和设计模式实现。

---

## 📦 功能特性

- **核心模块**
    - 幂等性控制（Idempotent）
    - 统一日志管理（Log）
    - 数据加密工具（Encrypt）
    - 数据库多数据源支持（Database）
    - WebSocket 实时通信（WebSocket）

- **中间件集成**
    - 缓存抽象层（Cache）
    - 消息队列适配（MQ）
    - 数据同步工具（Canal）
    - API 文档（Swagger）
    - 安全框架（Security）

- **架构设计**
    - 常用设计模式实现（DesignPattern）
    - 服务器推送事件（SSE）
    - 通用工具包（Common）

---

## 🛠️ 技术栈

| 技术                 | 说明                     |
|----------------------|--------------------------|
| **核心框架**         | Spring Boot 3.x          |
| **构建工具**         | Maven 3.8+              |
| **数据库**           | MySQL                    |
| **缓存**             | Redis                    |
| **消息队列**         | RabbitMQ                 |
| **文档工具**         | Swagger 3.0              |

---

## 📂 项目结构

```bash
.
├── dependencies        # 依赖管理模块
│   └── pom.xml
├── frameworks          # 框架核心模块
│   ├── cache           # 缓存抽象层
│   ├── canal           # 数据同步工具
│   ├── common          # 通用工具包
│   ├── database        # 数据库常见配置
│   ├── designpattern   # 设计模式实现
│   ├── encrypt         # 数据加密
│   ├── idempotent      # 幂等性控制
│   ├── log             # 统一日志
│   ├── mq              # 消息队列适配
│   ├── security        # 安全框架
│   ├── sse             # 服务器推送事件
│   ├── swagger         # API 文档
│   ├── web             # Web 层核心
│   └── websocket       # WebSocket 通信
├── pom.xml             # 父模块 POM
├── .gitignore          # Git 忽略规则
├── LICENSE             # MIT 许可证
└── README.md           # 项目文档
```

---

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Linweixinyo/quick-framework.git
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 新建项目模块（service模块）

* 在父模块下新建maven项目

### 4. 引入所需模块

```xml

<dependency>
    <groupId>org.weixin.framework</groupId>
    <artifactId>swagger</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 5. 项目配置

```yaml
framework:
  swagger:
    config:
      author: weixin
      title: quick-framework-service-demo
      description: quick-framework-service-demo
      email: 1320627222@qq.com
      version: v1.0.0
```

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Linweixinyo/quick-framework&type=Date)](https://star-history.com/#Linweixinyo/quick-framework&Date)