# 阶段 1：图书 CRUD 设计

## 目标

在现有 Spring Boot REST 工程中加入 MySQL 持久化和 MyBatis，完成图书新增、查询、修改、逻辑删除和分页搜索。

## 数据模型

表名：`book`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | `BIGINT` | 主键、自增 | 图书记录 ID |
| `isbn` | `VARCHAR(20)` | 非空、唯一 | 国际标准书号 |
| `title` | `VARCHAR(200)` | 非空 | 书名 |
| `author` | `VARCHAR(100)` | 非空 | 作者 |
| `publisher` | `VARCHAR(200)` | 可空 | 出版社 |
| `total_stock` | `INT` | 非空、>= 0 | 总册数 |
| `available_stock` | `INT` | 非空、>= 0 | 当前可借册数 |
| `deleted` | `TINYINT` | 非空、默认 0 | 逻辑删除标记 |
| `created_at` | `DATETIME` | 非空 | 创建时间 |
| `updated_at` | `DATETIME` | 非空 | 修改时间 |

`available_stock` 初始值等于 `total_stock`。修改库存时不允许将可借库存变成负数，也不允许超过总库存。第一版通过 Service 校验，数据库保留基础非空约束。

ISBN 由数据库唯一索引和 Service 层错误转换共同保证唯一性。所有普通查询都必须过滤 `deleted = 0`。

## 分层结构

```text
BookController
    ↓
BookService
    ↓
BookMapper (MyBatis XML/注解 SQL)
    ↓
MySQL book 表
```

- `BookEntity` 对应数据库列。
- `CreateBookRequest` 接收新增参数。
- `UpdateBookRequest` 接收修改参数。
- `BookResponse` 返回 API 数据，不直接暴露 Entity。
- Controller 不编写 SQL 或库存规则。
- Service 负责 ISBN 重复、库存范围和不存在图书判断。
- Mapper 只负责 SQL，并在查询中统一加入 `deleted = 0` 条件。

## API 契约

- `POST /api/books`：创建图书，返回 HTTP 201。
- `GET /api/books/{id}`：查询单本未删除图书，成功返回 HTTP 200。
- `GET /api/books`：按 `title` 可选搜索并分页，默认 `page=1`、`size=10`。
- `PUT /api/books/{id}`：修改书名、作者、出版社和库存，返回 HTTP 200。
- `DELETE /api/books/{id}`：逻辑删除，成功返回 HTTP 204。

第一版响应可以直接返回对象或简单分页对象，不提前引入复杂统一响应包装；阶段 2 再统一错误和响应格式。

## 错误边界

阶段 1 先使用明确的 Service 异常或简单运行时异常完成行为测试。阶段 2 再统一异常类型、错误码和全局异常响应。

- ISBN 为空或重复：创建/修改失败。
- 书名、作者为空：创建/修改失败。
- 总库存小于 0：失败。
- 可借库存小于 0 或大于总库存：失败。
- 查询、修改、删除不存在或已删除图书：失败。

## 测试策略

- Mapper 集成测试验证真实 MySQL SQL 和逻辑删除过滤。
- Service 单元测试验证 ISBN 重复、库存边界和不存在图书。
- Controller 测试验证 HTTP 方法、路径、状态码和 JSON 字段。
- 至少保留一个端到端的新增→查询→修改→逻辑删除流程。

## 暂不处理

- 借阅记录与读者表。
- 事务并发库存控制。
- JWT、权限、缓存和文件上传。
- 物理删除。
