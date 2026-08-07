# 图书管理系统

一个用于学习 Java 后端开发的 Spring Boot REST API 项目。系统目前支持图书管理、读者管理、借书、还书、借阅记录查询，以及借书时的原子库存扣减。

## 功能

- 图书：新增、按 ID 查询、修改、逻辑删除、按标题分页查询。
- 读者：新增、按 ID 查询、读者编号唯一校验。
- 借阅：借书、还书、按读者查询借阅记录。
- 规则：ISBN 唯一、读者编号唯一、库存不能为负、可借库存不能超过总库存、同一记录不能重复归还。
- 并发保护：借书通过数据库原子 SQL 扣减库存，避免多请求同时借最后一本书时超借。

## 技术栈

- Java 21
- Spring Boot 4.1.0
- Maven
- MySQL 8
- MyBatis
- JUnit 5、Mockito、Spring MockMvc

## 环境要求

- 已安装 JDK 21、Maven、MySQL。
- MySQL 中存在 `library_manager` 数据库，并且应用配置的账号具有读写权限。
- 环境变量 `LIBRARY_DB_PASSWORD` 保存 MySQL 密码；不要把密码写入 Git 或 `application.properties`。

## 启动

在项目根目录的 PowerShell 中执行：

```powershell
$env:LIBRARY_DB_PASSWORD="你的MySQL密码"
mvn "-Dmaven.repo.local=D:\codex project\library manager system\.m2\repository" spring-boot:run
```

默认地址为 `http://localhost:8080`。

## 测试

```powershell
mvn "-Dmaven.repo.local=D:\codex project\library manager system\.m2\repository" test
```

当前完整测试集包含 Controller 测试、Service 单元测试和 MySQL Mapper 集成测试。

## 接口文档

详见 [docs/API.md](docs/API.md)。

## 项目结构

```text
src/main/java/com/pp/library
├── book/       图书模块
├── reader/     读者模块
├── borrow/     借阅模块
└── health/     健康检查模块

src/main/resources/mapper
├── BookMapper.xml
├── ReaderMapper.xml
└── BorrowRecordMapper.xml
```

每个业务模块按职责分为：

- `controller`：HTTP 路由、参数绑定和状态码。
- `dto`：接收请求数据。
- `entity`：映射数据库表。
- `mapper`：定义数据库访问方法及 SQL。
- `service`：业务规则与事务。
- `vo`：返回给客户端的数据。

## 数据库初始化说明

项目中保留了 `db/schema.sql` 作为数据库结构参考。

> 注意：当前 `db/schema.sql` 的 `borrow_record` 字段名与运行中的 `BorrowRecordMapper.xml` 不一致。运行中的代码使用 `borrowed_at`、`returned_at` 和 `status`；在全新数据库初始化前，应先同步该 SQL 文件与 Mapper。现有开发数据库和自动化测试均已验证通过。
