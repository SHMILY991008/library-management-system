# Docker 本机联调

Docker Compose 固定使用云电脑相同的 Java 17、Node.js 16.20.2、MySQL 5.7 和 Nginx 1.14。它用于本机完整联调，不替代云电脑最终部署。

## 启动

先启动 Docker Desktop，在项目根目录复制本地环境变量文件：

```powershell
Copy-Item .env.example .env
docker compose up --build
```

浏览器访问 `http://localhost:8088`。Nginx 提供 React 页面，并将 `/api` 请求转发到 Spring Boot；Spring Boot 连接 MySQL 容器。首次启动会自动执行 `db/schema.sql`。

## 验证

依次新增图书、登记读者、借书、还书，并刷新页面确认数据存在。单独检查接口：

```powershell
Invoke-WebRequest http://localhost:8088/api/health
docker compose ps
docker compose logs -f backend
```

## 停止和重置

停止服务但保留数据库数据：

```powershell
docker compose down
```

删除测试数据库并在下次启动时重新初始化：

```powershell
docker compose down -v
```

`.env` 保存本机测试密码，不提交到 Git。
