# 碧潭村旅游官网

一个采用 Vue 3、Spring Boot 和 MySQL 构建的动态旅游官网。村落介绍、游玩路线、四季风物、图库和出行指南均来自数据库，访客可以提交到访留言，管理员可在独立工作台维护内容。

## 技术栈

- 前端：Vue 3、Vite、Nginx
- 后端：Java 17、Spring Boot 3、Spring JDBC、Bean Validation
- 数据库：MySQL 8.0、Flyway
- 部署：Docker Compose

## 快速启动

项目推荐使用 Docker Compose 运行，本机无需安装 Maven 或 MySQL。

```bash
cp .env.example .env
docker compose up --build -d
```

启动完成后访问：

- 官网：http://localhost:5173
- 管理后台：http://localhost:5173/admin
- 后端健康检查：http://localhost:8080/api/health
- MySQL：`localhost:3307`

查看服务状态与日志：

```bash
docker compose ps
docker compose logs -f backend
```

停止服务：

```bash
docker compose down
```

## 管理后台

服务会根据环境变量创建并同步管理员密码。使用 `.env.example` 创建 `.env` 后，请务必修改：

```env
ADMIN_USERNAME=admin
ADMIN_PASSWORD=一段足够长且唯一的密码
```

进入 http://localhost:5173/admin 后可以：

- 编辑村落基础资料和首页横幅
- 维护百度百科简介、词条来源和村落介绍配图
- 维护统计数据、游玩路线、四时风物、图库和出行指南
- 调整内容顺序和发布状态
- 上传 JPEG、PNG 图片（单张不超过 10MB）
- 查看、标记、归档和删除游客留言

管理员密码使用 PBKDF2 加盐摘要保存，登录令牌默认 12 小时失效。修改 `ADMIN_PASSWORD` 并重启后会更新密码，同时注销旧令牌。上传图片存放在 `upload_data` 数据卷中，重新构建容器不会丢失。

如需同时删除本地数据库数据：

```bash
docker compose down -v
```

## API

### 获取网站内容

```http
GET /api/site
```

返回村落资料、统计信息、路线、四季内容、图库和出行指南。

### 提交访客留言

```http
POST /api/inquiries
Content-Type: application/json
```

```json
{
  "name": "访客",
  "email": "visitor@example.com",
  "visitDate": "2026-10-01",
  "partySize": 2,
  "message": "想了解秋季适合走的路线。"
}
```

### 健康检查

```http
GET /api/health
```

健康检查会同时验证后端与 MySQL 的连接。

## 数据库

Flyway 会在首次启动时自动创建并填充以下数据表：

- `village_profile`：村落主资料
- `site_stat`：首页统计数据
- `journey_stop`：游玩路线
- `season_content`：四季内容
- `gallery_item`：图库
- `guide_item`：出行指南
- `visitor_inquiry`：访客留言
- `admin_user`：管理员账号
- `admin_session`：短期登录令牌

修改公开内容后，`GET /api/site` 会读取数据库中的最新记录；每条内容都可通过 `published` 字段控制是否显示。

## 本地开发

启动 MySQL 和后端后，前端可单独运行热更新开发服务器：

```bash
cd frontend
npm install
npm run dev
```

Vite 会将 `/api` 请求代理到 `http://localhost:8080`。
