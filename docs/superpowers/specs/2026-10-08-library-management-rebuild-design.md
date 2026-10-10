# 图书管理系统前后端分离改造设计

## 目标

将现有图书管理项目整理为一个单仓库、前后端分离的练手项目。项目使用 React 实现前端页面，使用 Java Spring Boot 提供 REST API，使用 MyBatis 访问 MySQL，并能部署在云电脑的 WSL Ubuntu 环境中。

本次改造需要产出清晰的 Git 提交记录、可复现的部署说明，以及可完整演示的图书借还业务。

## 项目结构

```text
library-management-system/
├─ frontend/                    # React + Vite
├─ backend/                     # Java 21 + Spring Boot + MyBatis
├─ deploy/
│  ├─ nginx/                    # 生产环境 Nginx 配置
│  ├─ systemd/                  # Spring Boot 服务配置
│  └─ scripts/                  # Ubuntu 部署脚本
├─ docs/                        # API 与部署文档
└─ README.md                    # 项目说明与本地运行指南
```

## 架构与职责

前端通过 HTTP 调用以 `/api` 开头的后端接口。Nginx 在生产环境中提供 React 构建产物，并将 `/api` 请求反向代理到 Spring Boot。

后端采用分层结构：

```text
React → Controller → Service → Mapper → MySQL
```

- `controller`：验证请求参数、调用业务层并返回 JSON。
- `service`：实现业务规则与事务，例如借书时校验库存并创建借阅记录。
- `mapper`：使用 MyBatis SQL 访问 MySQL。
- `entity`：对应数据库表的 Java 数据对象。
- `dto`：接口请求与响应对象，避免把数据库对象直接暴露给前端。

## 业务范围

### 图书管理

- 查询全部图书，支持按书名、作者、ISBN 搜索。
- 新增、编辑、删除图书。
- 显示总库存与可借库存。

### 读者管理

- 查询读者。
- 新增读者。

### 借阅管理

- 为指定读者借出指定图书。
- 库存为零时拒绝借书。
- 归还图书时更新借阅记录与可借库存。
- 查看借阅记录及其借阅、归还状态和时间。

本版本不包含登录、角色权限、预约、罚款、消息通知等扩展功能。

## 数据模型

### book

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| isbn | ISBN，唯一 |
| title | 书名 |
| author | 作者 |
| publisher | 出版社 |
| total_stock | 总库存 |
| available_stock | 可借库存 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### reader

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| name | 姓名 |
| reader_no | 读者编号，唯一 |
| phone | 联系电话 |
| created_at | 创建时间 |

### borrow_record

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| book_id | 图书 ID |
| reader_id | 读者 ID |
| borrowed_at | 借出时间 |
| returned_at | 归还时间，可为空 |
| status | `BORROWED` 或 `RETURNED` |

`borrow_record.book_id` 关联 `book.id`，`borrow_record.reader_id` 关联 `reader.id`。

## API

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| GET | `/api/books` | 图书列表与关键字搜索 |
| POST | `/api/books` | 新增图书 |
| PUT | `/api/books/{id}` | 编辑图书 |
| DELETE | `/api/books/{id}` | 删除图书 |
| GET | `/api/readers` | 读者列表 |
| POST | `/api/readers` | 新增读者 |
| GET | `/api/borrow-records` | 最近 100 条借阅记录 |
| POST | `/api/borrow-records` | 借书 |
| PUT | `/api/borrow-records/{id}/return` | 还书 |

借书与还书接口必须使用数据库事务。借书时先检查图书存在且 `available_stock > 0`，再扣减库存并创建借阅记录。还书时只允许归还状态为 `BORROWED` 的记录，随后更新状态、写入归还时间并增加库存。

## 前端页面

- 图书管理：图书搜索、表格、增改删表单与库存展示。
- 读者管理：读者表格与新增表单。
- 借阅管理：借书表单、还书操作和借阅记录表格。

前端应显示可理解的加载、成功与失败提示；后端校验失败时显示接口返回的具体原因。

## 部署设计

1. 在 WSL Ubuntu 中安装 Java 21、MySQL、Nginx 与 Node.js。
2. 创建 MySQL 数据库并执行项目初始化 SQL。
3. 构建后端 JAR，使用 `systemd` 将其作为服务运行。
4. 构建 React 静态文件，放入 Nginx 的站点目录。
5. Nginx 为前端提供静态文件，并将 `/api` 转发到本机 Spring Boot 服务端口。
6. 配置 WSL 与 Windows 防火墙或端口转发，使部署地址可从约定的网络范围访问。

## 验收与验证

- 本地：前端能够调用后端并完成新增图书、新增读者、借书、还书和查询记录。
- 业务：库存为零不能借书；重复归还同一记录会被拒绝；借还后库存正确变化。
- 构建：前端生产构建成功；后端测试与打包成功。
- 部署：通过云电脑 WSL Ubuntu 的部署地址完成一次图书借还演示。
- 版本管理：以基础结构、后端、前端、联调、部署文档等阶段生成清晰的 Git 提交记录。
