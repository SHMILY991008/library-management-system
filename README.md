# 图书管理系统 · 馆藏工作台

一个供练习和演示的前后端分离项目。React 页面可管理图书、登记读者和办理借还；Java Spring Boot 后端使用 MyBatis 访问 MySQL。借书时由数据库原子扣减库存，借还操作由事务保护。

## 项目结构

```text
frontend/   React + TypeScript + Vite
backend/    Java 17 + Spring Boot 3.2 + MyBatis
db/         MySQL 初始化脚本
deploy/     WSL Ubuntu 所需的 Nginx、systemd 和部署脚本
docs/       API 与部署说明
```

## 本地运行

云电脑环境使用 Java 17、Node.js 16.20.2、MySQL 5.7 和 Nginx 1.14。本机可以用 Docker 模拟这套环境；若直接运行，则安装相同版本。后端使用项目自带的 Maven Wrapper，因此不依赖系统 Maven 版本。先用 MySQL 客户端执行 `db/schema.sql`，该脚本创建 `library_manager` 数据库与三张表。设置环境变量 `LIBRARY_DB_USERNAME`、`LIBRARY_DB_PASSWORD`，必要时设置 `LIBRARY_DB_URL`。不要把密码提交到 Git。

启动后端：

```powershell
cd backend
$env:LIBRARY_DB_USERNAME="你的数据库用户名"
$env:LIBRARY_DB_PASSWORD="你的数据库密码"
.\mvnw.cmd spring-boot:run
```

在另一个终端启动前端：

```powershell
cd frontend
npm ci
npm run dev
```

打开 Vite 输出的本地地址，通常是 `http://localhost:5173/`。开发服务器会把 `/api` 请求转发到 `http://localhost:8080`。

## 验证与演示

后端测试：在 `backend/` 运行 `./mvnw.cmd test`（Ubuntu 中为 `./mvnw test`）。前端测试与构建：在 `frontend/` 运行 `npm test` 和 `npm run build`。

演示建议按“添加图书 → 登记读者 → 借出 → 查看库存减少与借阅记录 → 归还 → 查看库存恢复”进行。库存为零时借出会被拒绝，同一借阅记录不能重复归还。

## 部署与接口

- [WSL Ubuntu 部署步骤](docs/deployment.md)
- [Docker 本机联调步骤](docs/docker.md)
- [后端 API](docs/API.md)

生产环境由 Nginx 提供 React 静态文件，并将 `/api` 转发到 Spring Boot。云电脑的公网地址、Windows 防火墙和 WSL 网络模式要根据实际环境配置。
