# 在云电脑的 WSL Ubuntu 部署

项目运行链路：浏览器 → Nginx（前端和 `/api` 代理）→ Spring Boot → MySQL。以下操作在云电脑的 Ubuntu 终端中执行。第一次部署前，确认云电脑允许安装软件并开放所需端口。

## 1. 检查环境

需要 Ubuntu WSL2、Java 21、Node.js 20.19+ 或 22.12+、MySQL 8、Nginx。系统需启用 systemd：运行 `systemctl status` 确认。如果不可用，在 `/etc/wsl.conf` 中加入：

```ini
[boot]
systemd=true
```

随后在 Windows PowerShell 执行 `wsl --shutdown`，再重新进入 Ubuntu。WSL 的 systemd 服务不会让 WSL 实例一直保持运行；演示前应确认云电脑、WSL 和服务均已启动。

## 2. 获取代码和初始化数据库

将本仓库克隆到 Ubuntu 自己的文件系统（例如 `~/projects/`），不要放在 `/mnt/c/` 或 `/mnt/d/` 下构建。

```bash
git clone https://github.com/SHMILY991008/library-management-system.git
cd library-management-system
sudo mysql < db/schema.sql
```

上面的 SQL 会创建 `library_manager` 数据库及三张表。然后在 MySQL 中为应用创建一个仅用于该数据库的账号，例如：

```sql
CREATE USER 'libraryapp'@'localhost' IDENTIFIED BY '请换成随机密码';
GRANT SELECT, INSERT, UPDATE, DELETE ON library_manager.* TO 'libraryapp'@'localhost';
```

如果连接地址使用 `127.0.0.1`，MySQL 可能按 TCP 主机匹配；请根据本机 MySQL 用户配置创建相应的 `'libraryapp'@'127.0.0.1'` 授权，或把数据库 URL 改为 `localhost`。可用 `mysql -u libraryapp -p library_manager` 先验证连接。

创建运行环境文件，并限制访问权限：

```bash
sudo install -d -m 700 /etc/library-management
sudo cp deploy/api.env.example /etc/library-management/api.env
sudo chmod 600 /etc/library-management/api.env
sudo nano /etc/library-management/api.env
```

在文件中填入实际数据库账号与密码。不要将真实密码提交到 Git。

## 3. 安装应用

确认 Java、Node.js、MySQL、Nginx 已安装并运行。检查 `/etc/nginx/sites-enabled/default`：如果它仍占用 80 端口，先确认没有其他网站依赖，再手动禁用。部署脚本会在默认站点仍启用时停止，避免覆盖已有网站。

```bash
bash deploy/scripts/deploy.sh
systemctl status library-api
curl http://127.0.0.1/api/books
```

脚本会执行前后端构建，安装 Nginx 与 systemd 配置，然后启动服务。更新时先 `git pull`，再运行同一个脚本。`frontend/dist/` 与 `backend/target/` 是构建产物，不提交到 Git。

## 4. 从其他电脑访问

先在云电脑 Windows 主机访问 `http://localhost/`。若要从局域网或公网访问，还需要检查云电脑平台的公网 IP/安全组、Windows 防火墙和 WSL 网络模式。WSL2 默认 NAT 模式下通常需要 Windows 端口转发；Windows 11 的 mirrored 模式可以让局域网直接访问 WSL，但仍需检查防火墙。公网演示建议先配置域名与 HTTPS，避免明文传输。

排查顺序：`systemctl status library-api` → `curl http://127.0.0.1:8080/api/health` → `sudo nginx -t` → `curl http://127.0.0.1/api/books` → Windows `localhost` → 其他电脑访问。

当前版本没有登录功能。若要通过公网演示，请先将访问限制到可信 IP，或在 Nginx 前增加身份验证；仅配置 HTTPS 不能阻止陌生人调用新增、删除和借还接口。

云电脑重启后请先打开 Ubuntu，确认 `systemctl status library-api`。WSL 实例关闭后，服务也会停止。
