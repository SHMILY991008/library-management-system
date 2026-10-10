# 在云电脑的 WSL Ubuntu 部署

项目运行链路：浏览器 → Nginx（前端和 `/api` 代理）→ Spring Boot → MySQL。以下操作在云电脑的 Ubuntu 终端中执行。第一次部署前，确认云电脑允许安装软件并开放所需端口。

## 1. 检查环境

本项目已适配云电脑的 Ubuntu WSL1：Java 17、Node.js 16.20.2、MySQL 5.7 和 Nginx 1.14。后端使用项目自带的 Maven Wrapper，以避免受系统 Maven 3.6.0 版本限制。WSL1 不支持 systemd，因此本项目使用 `service` 管理 MySQL、Nginx，并使用 `nohup` 在后台运行 Spring Boot。

## 2. 获取代码和初始化数据库

将本仓库克隆到 Ubuntu 自己的文件系统（例如 `~/projects/`），不要放在 `/mnt/c/` 或 `/mnt/d/` 下构建。

```bash
git clone https://github.com/SHMILY991008/library-management-system.git
cd library-management-system
sudo mysql < db/schema.sql
```

上面的 SQL 会创建 `library_manager` 数据库及三张表。然后在 MySQL 中为应用创建一个仅用于该数据库的账号，例如：

```sql
CREATE USER 'ply'@'localhost' IDENTIFIED BY '请换成随机密码';
GRANT SELECT, INSERT, UPDATE, DELETE ON library_manager.* TO 'ply'@'localhost';
```

如果连接地址使用 `127.0.0.1`，MySQL 可能按 TCP 主机匹配；请根据本机 MySQL 用户配置创建相应的 `'ply'@'127.0.0.1'` 授权，或把数据库 URL 改为 `localhost`。可用 `mysql -u ply -p library_manager` 先验证连接。

创建运行环境文件，并限制访问权限：

```bash
sudo useradd --system --home /opt/library-management --shell /usr/sbin/nologin libraryapp 2>/dev/null || true
sudo install -d -m 700 /etc/library-management
sudo cp deploy/api.env.example /etc/library-management/api.env
sudo chown libraryapp:libraryapp /etc/library-management/api.env
sudo chmod 600 /etc/library-management/api.env
sudo nano /etc/library-management/api.env
```

在文件中填入实际数据库账号与密码。不要将真实密码提交到 Git。

## 3. 安装应用

确认 Java 17、Node.js 16.20.2、MySQL 5.7、Nginx 已安装。首次执行 Maven Wrapper 时，它会下载项目指定的 Maven 版本。检查 `/etc/nginx/sites-enabled/default`：如果它仍占用 80 端口，先确认没有其他网站依赖，再手动禁用。部署脚本会在默认站点仍启用时停止，避免覆盖已有网站。

```bash
bash deploy/scripts/deploy.sh
cat /opt/library-management/library-api.pid
tail -n 50 /opt/library-management/library-api.log
curl http://127.0.0.1/api/books
```

脚本会执行前后端构建，安装 Nginx 配置，然后启动 MySQL、Nginx 和后端进程。更新时先 `git pull`，再运行同一个脚本。`frontend/dist/` 与 `backend/target/` 是构建产物，不提交到 Git。

## 4. 从其他电脑访问

先在云电脑 Windows 主机访问 `http://localhost/`。WSL1 与 Windows 共享网络栈，通常无需 WSL2 的端口转发。若要从局域网或公网访问，仍需检查云电脑平台的公网 IP/安全组和 Windows 防火墙。公网演示建议先配置域名与 HTTPS，避免明文传输。

排查顺序：`cat /opt/library-management/library-api.pid` → `tail -n 50 /opt/library-management/library-api.log` → `curl http://127.0.0.1:8080/api/health` → `sudo nginx -t` → `curl http://127.0.0.1/api/books` → Windows `localhost` → 其他电脑访问。

当前版本没有登录功能。若要通过公网演示，请先将访问限制到可信 IP，或在 Nginx 前增加身份验证；仅配置 HTTPS 不能阻止陌生人调用新增、删除和借还接口。

云电脑或 WSL1 重启后，请重新进入 Ubuntu 并执行：

```bash
sudo service mysql start
sudo service nginx start
sudo -u libraryapp bash deploy/scripts/restart-api.sh
```

WSL1 没有 systemd，不能自动恢复 Spring Boot 后端进程。
