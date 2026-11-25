# 408 AI 错题诊断与个性化复习平台

面向计算机考研（408）的 AI 错题诊断与个性化复习平台 —— 毕业设计项目。

## 目录结构

```
kaoyan-platform/
├── backend/                 Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/kaoyan/
│       │   ├── KaoyanApplication.java     启动类
│       │   ├── common/                    统一返回、异常、枚举、工具、基类
│       │   ├── config/                    MyBatis-Plus / Redis / Kafka / Web / Swagger / 加密
│       │   ├── security/                  JWT 拦截器、用户上下文、权限注解 + AOP
│       │   ├── infrastructure/            大模型客户端、SSE 管理器
│       │   └── module/                    业务模块（auth / system / diagnosis / health ...）
│       └── resources/
│           ├── application.yml            主配置
│           └── application-dev.yml        开发环境（数据源 / Redis / Kafka）
├── frontend/                Vue 前端（待创建）
└── sql/                     数据库脚本
    ├── schema.sql           29 张表的完整建表脚本
    ├── init-data.sql        角色、菜单和演示账号
    ├── seed-408.sql         4 门课程、20 章节、100 知识点
    └── migration-20261124-add-diagnosis-task.sql
```

## 技术栈

| 层次 | 选型 |
| --- | --- |
| 后端 | SpringBoot 2.7.18 / Java 17 / MyBatis-Plus 3.5.7 |
| 数据 | MySQL 8 / Redis |
| 消息 | Kafka 3.9.1（KRaft 单节点，Docker 运行） |
| AI | 大模型 API（OpenAI 兼容协议）/ Embedding / OCR |
| 实时 | SSE（SseEmitter） |
| 文档 | Knife4j（Swagger 3） |
| 前端 | Vue3 + ElementUI + ECharts |

## 快速开始

### 1. 启动中间件

```bash
# Kafka（已配置为随 Docker 自启）
docker start kafka

# Redis（本机安装版，手动启动）
C:\JavaDemo\redis\Redis-8.0.1-Windows-x64-cygwin\start.bat

# MySQL 已作为 Windows 服务 MySQL80 运行
```

### 2. 初始化数据库

新数据库按顺序执行：

```bash
mysql -uroot -p < ../sql/schema.sql
mysql -uroot -p < ../sql/init-data.sql
mysql -uroot -p < ../sql/seed-408.sql
```

已有 28 表数据库只执行增量迁移，再导入演示课程：

```bash
mysql -uroot -p < ../sql/migration-20261124-add-diagnosis-task.sql
mysql -uroot -p < ../sql/init-data.sql
mysql -uroot -p < ../sql/seed-408.sql
```

演示账号：`admin / 123456`、`student01 / 123456`。

### 3. 配置环境变量

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `MYSQL_USER` | 数据库用户 | root |
| `MYSQL_PASSWORD` | 数据库密码 | root123 |
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DB` | 连接信息 | localhost / 3306 / kaoyan_platform |
| `REDIS_HOST` / `REDIS_PORT` | Redis 地址 | localhost / 6379 |
| `KAFKA_SERVERS` | Kafka 地址 | localhost:9092 |
| `LLM_API_KEY` | 大模型 API Key（**必填，勿写入代码**） | 空 |
| `LLM_BASE_URL` | 大模型接口地址 | https://api.deepseek.com |
| `LLM_MODEL` | 模型名 | deepseek-chat |
| `KAAYAN_JWT_SECRET` | JWT 密钥（生产必改） | 内置默认值 |

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动后：
- 接口文档：http://localhost:8081/doc.html
- 健康检查：http://localhost:8081/api/health/all

### 5. 自检清单

| 检查项 | 方式 |
| --- | --- |
| 应用存活 | `GET /api/health/ping` → pong |
| MySQL | `GET /api/health/mysql` → status UP |
| Redis | `GET /api/health/redis` → status UP，readBack ok |
| Kafka | `POST /api/health/kafka` → SENT，控制台出现 `DiagnosisConsumer` 日志 |
| 大模型 | `GET /api/health/all` → llm.configured true |
| 注册登录 | `POST /api/auth/register` → `POST /api/auth/login` 拿 token |
| 鉴权 | 带 `Authorization: Bearer <token>` 访问 `GET /api/auth/me` |

## 开发约定

- 分层：`controller` → `service` → `mapper`，实体继承 `BaseEntity`（含审计字段与逻辑删除）
- 统一返回 `Result<T>`，分页返回 `PageResult<T>`，异常抛 `BizException` 由全局处理器兜底
- 权限：接口上加 `@RequiresPermission(value = "xxx:yyy", role = "ADMIN")`
- 当前用户：`UserContext.userId()`（由 JWT 拦截器写入 ThreadLocal）
- 敏感信息（API Key、密码、JWT 密钥）一律走环境变量，禁止提交到仓库
- SSE 统一用 `fetch` 携带 `Authorization` 请求头订阅，令牌不进入 URL

## 诊断任务接口

| 方法与路径 | 说明 |
| --- | --- |
| `POST /api/diagnosis/tasks` | 创建诊断任务；同一错题已有进行中任务时直接复用 |
| `GET /api/diagnosis/tasks/{taskId}` | 查询任务状态与结果关联 |
| `GET /api/diagnosis/tasks?status=FAILED&current=1&size=10` | 分页查询当前用户任务 |
| `POST /api/diagnosis/tasks/{taskId}/cancel` | 取消未结束任务 |
| `POST /api/diagnosis/tasks/{taskId}/retry` | 手动重试失败/待重试任务，复用原 taskId |
| `GET /api/diagnosis/tasks/{taskId}/events` | SSE 订阅，推送 `status`、`done`、`error`、`ping` |

任务创建后先落库，事务提交后再投递 Kafka；投递失败会将任务置为 `FAILED`，前端可从任务列表重试。

## 408 课程体系接口

| 方法与路径 | 说明 |
| --- | --- |
| `GET /api/courses/subjects` | 查询考试科目 |
| `GET /api/courses?subjectId=1` | 查询课程列表，`subjectId` 可省略 |
| `GET /api/courses/{courseId}/tree` | 一次返回章节树和挂载的知识点 |
| `GET /api/courses/chapters/{chapterId}/knowledge-points` | 查询单个章节的知识点 |

种子数据包含 4 门课程、20 个章节和 100 个知识点，接口按 `sort` 与主键稳定排序。

## 开发进度

- [x] 阶段零：环境与准备（JDK 17 / Maven / MySQL / Redis / Kafka(Docker) / IDEA）
- [x] 阶段一：需求与设计（需求文档、ER、28 张表、接口清单）
- [x] 阶段二：工程骨架（分层、统一返回/异常、Redis/Kafka 集成、JWT、Swagger）
- [x] 数据基线：建表、初始化、408 种子数据和 diagnosis_task 增量迁移
- [x] 诊断任务：任务实体、状态机、REST API、SSE 进度推送和应用层测试
- [ ] 阶段三：基础业务（课程树查询已完成；RBAC 管理、错题 CRUD 待开发）
- [ ] 阶段四：错题管理与 AI 诊断（核心）
- [ ] 阶段五：优化点与辅助功能
- [ ] 阶段六：系统支撑与联调
- [ ] 阶段七：系统测试
- [ ] 阶段八：部署与交付
