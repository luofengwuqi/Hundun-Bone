# 接口文档

馄饨骨架（Hundun-Bone）后端共 **29 个接口**。本文按模块组织，列出每个接口的方法、路径、所需身份、限流参数与请求 / 响应示例。

- **Base URL（直连后端）**：`http://localhost:5757`
- **Base URL（经前端开发服务器）**：`http://localhost:5577/api` —— Vite 会把 `/api` 前缀去掉再转发
- **无 context-path**，上表中的路径就是完整路径
- 请求与响应均为 `application/json;charset=UTF-8`，文件上传为 `multipart/form-data`

## 通用约定

### 统一响应体

所有接口（含错误）都返回同一结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2026-10-30T06:36:17"
}
```

| 字段 | 说明 |
| --- | --- |
| `code` | 业务状态码，`200` 表示成功 |
| `message` | 提示文案，可直接用于前端展示 |
| `data` | 业务数据，无返回值时为 `null` |
| `timestamp` | 服务端时间 |

分页接口的 `data` 统一为：

```json
{
  "total": 5,
  "list": []
}
```

### 认证方式

登录成功后拿到 `token`，之后每个请求带在请求头里：

```
Authorization: Bearer <token>
```

也兼容旧式的裸 `token` 请求头。token 同时受两道限制：JWT 自身有效期 24 小时，Redis 中的会话 30 分钟滑动窗口——**只要 30 分钟内没有任何请求，会话就失效**。

> [!IMPORTANT]
> 有两处错误语义并存，客户端需要同时处理：
> 业务异常（如限流）返回 **HTTP 200 + 业务码 429**；
> 鉴权失败（未登录 / token 过期 / 被封禁 / 越权）由过滤器直接返回 **对应的 HTTP 401 / 403**。
> 也就是说，判断「是否成功」不能只看 HTTP 状态码，也不能只看业务码。

### 身份与权限

权限不建表，全部由 `users.status` 表达：

| status | 身份 | 可访问范围 |
| --- | --- | --- |
| `1` | 普通用户 | `/user/**`、`/feedback`（提交）、`/files` |
| `-1` | 普通用户（封禁） | 无，登录即被拒 |
| `2` | 管理员 | 追加 `/admin/**`、反馈查询与处理 |
| `-2` | 管理员（封禁） | 无 |
| `3` | 超级管理员 | 追加签发邀请码、直接创建管理员 |

下文表格中的「身份」列含义：

- **公开** —— 无需 token（`/user/login/**` 与 `POST /user/register`）
- **登录** —— 任意有效 token
- **管理员** —— `status` 为 `2` 或 `3`
- **超管** —— `status` 为 `3`

### 错误码

| code | HTTP | 触发场景 |
| --- | --- | --- |
| `200` | 200 | 成功 |
| `400` | 200 | 参数校验失败、密码错误、验证码错误、文件类型不合法 |
| `401` | 200 / 401 | 未登录、token 过期或无效、登录失败 |
| `403` | 200 / 403 | 账号被封禁、访问 `/admin` 但身份不足、`@RequireRole` 不匹配 |
| `404` | 200 | 用户不存在 |
| `409` | 200 | 用户名 / 邮箱 / 手机号已存在 |
| `429` | 200 | 触发限流 |
| `500` | 200 | SQL 异常、OSS 异常、其他未捕获异常 |

---

## 认证（6）

### `POST /user/login/username-password`

用户名 + 密码登录。

| 项 | 值 |
| --- | --- |
| 身份 | 公开 |
| 限流 | 10 次 / 60 秒（IP 与用户名双维度，登录成功后重置用户名维度） |

```json
{
  "username": "demo",
  "password": "123456"
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 63,
      "username": "demo",
      "nickname": "演示账号",
      "email": "demo@example.com",
      "phone": "13800000001",
      "avatarUrl": null,
      "status": 3,
      "createdAt": "2026-10-30 06:36:12",
      "updatedAt": "2026-10-30 06:36:12",
      "lastLoginAt": "2026-10-30T06:37:15"
    }
  },
  "timestamp": "2026-10-30T06:37:15"
}
```

用户名长度 1–10，密码长度 6–20。`passwordHash` 与 `inviteCode` 被 `@JsonIgnore` 排除，永远不会出现在响应里。

### `POST /user/login/phone-password`

手机号 + 密码登录。

| 项 | 值 |
| --- | --- |
| 身份 | 公开 |
| 限流 | 10 次 / 60 秒（IP 与手机号双维度） |

```json
{ "phone": "13800000001", "password": "123456" }
```

手机号需匹配 `^1[3-9]\d{9}$`。响应结构同上。

### `POST /user/login/email-password`

邮箱 + 密码登录。

| 项 | 值 |
| --- | --- |
| 身份 | 公开 |
| 限流 | 10 次 / 60 秒（IP 与邮箱双维度） |

```json
{ "email": "demo@example.com", "password": "123456" }
```

响应结构同上。

### `POST /user/login/email-code/send`

发送 6 位邮箱验证码。

| 项 | 值 |
| --- | --- |
| 身份 | 公开 |
| 限流 | 3 次 / 60 秒（IP 与邮箱双维度） |

```json
{ "email": "demo@example.com" }
```

```json
{ "code": 200, "message": "操作成功", "data": null, "timestamp": "2026-10-30T06:40:00" }
```

邮箱必须是已注册账号，否则返回 `404`。验证码 6 位纯数字，有效期 **5 分钟**，存于 `user:email:code:{email}`。发送失败返回 `400`（提示授权码或 SMTP 配置问题）。

### `POST /user/login/email-code`

邮箱验证码登录。

| 项 | 值 |
| --- | --- |
| 身份 | 公开 |
| 限流 | 10 次 / 60 秒（IP 与邮箱双维度） |

```json
{ "email": "demo@example.com", "code": "482913" }
```

`code` 必须正好 6 位。校验使用恒定时间比较，登录成功后验证码立即失效。响应结构同用户名登录。

### `POST /user/login/logout`

登出，删除 Redis 中的 `token:{id}` 与 `user:data:{id}`，token 立即失效。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

无请求体，返回 `data: null`。

---

## 用户（8）

### `POST /user/register`

普通用户注册。

| 项 | 值 |
| --- | --- |
| 身份 | 公开（其父路径 `/user/register` 在过滤器白名单内） |
| 限流 | 5 次 / 60 秒（IP 维度） |

```json
{
  "username": "alice",
  "password": "123456",
  "email": "alice@example.com",
  "phone": "13800000002",
  "avatarUrl": null
}
```

```json
{ "code": 200, "message": "操作成功", "data": { "username": "alice" }, "timestamp": "2026-10-30T06:36:15" }
```

| 字段 | 校验 |
| --- | --- |
| `username` | 1–10 位，仅中英文、数字、下划线、连字符 |
| `password` | 6–20 位，BCrypt(12) 存储 |
| `email` | 格式校验 |
| `phone` | `^1[3-9]\d{9}$` |
| `avatarUrl` | 可选 |

用户名 / 邮箱 / 手机号任一重复都返回 `409`，且三者共用同一句提示文案（防止账号枚举）。注册后 `status = 1`。

### `GET /user/info`

获取当前登录用户信息。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 63,
    "username": "demo",
    "nickname": "演示账号",
    "email": "demo@example.com",
    "phone": "13800000001",
    "avatarUrl": null,
    "status": 3,
    "createdAt": "2026-10-30 06:36:12",
    "updatedAt": "2026-10-30 06:36:12",
    "lastLoginAt": "2026-10-30 06:37:15"
  },
  "timestamp": "2026-10-30T06:41:02"
}
```

先读 `user:data:{id}` 缓存，未命中查库并回填。

### `PUT /user/nickname`

修改昵称，**需要当前密码**。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

```json
{ "value": "新昵称", "password": "123456" }
```

`value` 为新昵称，`password` 为当前登录密码（不是新密码）。密码错误返回 `400`。

### `PUT /user/email`

修改邮箱，需要当前密码。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

```json
{ "value": "new@example.com", "password": "123456" }
```

`value` 需符合邮箱格式；与已有账号重复时返回 `409`。

### `PUT /user/phone`

修改手机号，需要当前密码。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

```json
{ "value": "13800000009", "password": "123456" }
```

`value` 需匹配 `^1[3-9]\d{9}$`。

> [!NOTE]
> 这个接口只接收 `value` 与 `password` 两个字段，**没有验证码字段**。前端 `UserManage.vue` 会多传一个邮箱验证码，但会在封装层被丢弃——也就是说改手机号目前只校验密码。

### `PUT /user/password`

用邮箱验证码重置密码。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |
| 限流 | 5 次 / 60 秒 |

```json
{ "code": "482913", "password": "newpass123" }
```

`code` 为发送到**当前账号邮箱**的 6 位验证码，`password` 为新密码（6–20 位）。旧密码不作校验；验证码一次性，成功后即删除。

### `PUT /user/avatar`

上传头像。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |
| Content-Type | `multipart/form-data` |
| 字段名 | `file` |

```bash
curl -X PUT http://localhost:5757/user/avatar \
  -H "Authorization: Bearer <token>" \
  -F "file=@avatar.png"
```

返回 `data: null`，头像地址需再调 `GET /user/info` 获取。仅接受图片（`jpg/jpeg/png/gif/webp/bmp`），单文件不超过 10 MB，且会校验文件魔数。对象名格式 `{username}/avatars/{uuid}.{ext}`，换新图时会删除旧图。

### `DELETE /user`

删除账号（自身注销 / 管理员批量删除共用）。

| 项 | 值 |
| --- | --- |
| 身份 | 登录（普通用户只能删自己；管理员可批量） |

```json
{ "usernames": ["alice"], "password": "123456" }
```

- **普通用户**：`usernames` 只能包含自己，且必须提供正确 `password`。
- **管理员**：可一次删除最多 100 个用户，逐个做层级校验（不能删同级或超管），不需要密码。
- 超级管理员账号不可被任何人删除。

删除成功后同时吊销被删用户的 token 并清理缓存。

---

## 管理（9）

以下接口除特别说明外都要求 **管理员（status 2 或 3）**，且路径以 `/admin` 开头，过滤器会先做一次前缀拦截。

### `POST /admin`

直接创建管理员账号（需要邀请码）。

| 项 | 值 |
| --- | --- |
| 身份 | **超级管理员** |

```json
{
  "username": "newadmin",
  "password": "123456",
  "importantPassword": "A1b2C3d4E5",
  "email": "newadmin@example.com",
  "phone": "13800000010"
}
```

`importantPassword` 就是邀请码。逻辑与 `POST /user/register/admin` 等价：校验邀请码存在 → 创建 `status = 2` 的账号 → 删除占位账号。

### `GET /admin/users/count`

统计用户总数。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |

```json
{ "code": 200, "message": "操作成功", "data": 5, "timestamp": "2026-10-30T06:42:00" }
```

### `GET /admin/invite-code`

生成一个新的管理员邀请码。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员（后端未做超管限制，但前端侧边栏只对 status 3 展示该入口） |

```json
{ "code": 200, "message": "操作成功", "data": "A1b2C3d4E5", "timestamp": "2026-10-30T06:42:30" }
```

返回 10 位随机码（数字 + 大小写字母）。同时插入一条占位账号：用户名 `管理员待注册{code}`、状态 `-2`、密码为随机 UUID 的哈希。占位账号占用 `username` 唯一索引，从而保证一个邀请码只能用一次。

### `PUT /admin/user`

管理端修改用户信息（逐字段编辑）。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员（受层级约束） |

```json
{
  "oldId": 64,
  "id": 64,
  "username": "alice",
  "nickname": "林晚",
  "email": "alice@example.com",
  "phone": "13800000002",
  "password": null
}
```

`oldId` 为定位用的原 id，`id` 为可修改的新 id。`password` 为空表示不改密码，非空则重新 BCrypt 加密。目标用户层级必须低于操作者。

### `PUT /admin/user/status?username=alice`

切换用户启用 / 禁用状态。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员（受层级约束） |
| 参数 | **query**：`username` |

```bash
curl -X PUT "http://localhost:5757/admin/user/status?username=alice" \
  -H "Authorization: Bearer <token>"
```

```json
{ "code": 200, "message": "操作成功", "data": -1, "timestamp": "2026-10-30T06:43:00" }
```

`data` 为变更后的状态值。状态翻转规则：`1 ↔ -1`、`-2 ↔ 2`；`3`（超管）禁止操作；未知状态一律置 `-1`。变更后立即吊销该用户 token 并清理缓存。

> [!WARNING]
> 这个接口用 **query 参数**，没有请求体。写成 `PUT /admin/user/status` 加 JSON body 是拿不到 `username` 的。

### `DELETE /admin/user/{id}`

按 id 删除单个用户。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员（受层级约束） |
| 参数 | **path**：`id` |

```bash
curl -X DELETE http://localhost:5757/admin/user/64 -H "Authorization: Bearer <token>"
```

成功返回 `data: null`；目标不存在返回 `404`；越级操作返回 `403`。

### `GET /admin/logs`

分页查询操作审计日志。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |
| 参数 | **请求体**（见下方警告） |

```json
{
  "page": 1,
  "size": 10,
  "operateUserName": "demo",
  "operateType": "SELECT",
  "moduleName": "用户模块",
  "operateStatus": "1"
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 128,
    "list": [
      {
        "id": 128,
        "operateUserId": 63,
        "operateUserName": "demo",
        "operateType": "SELECT",
        "moduleName": "用户模块",
        "ipAddress": "127.0.0.1",
        "operateFunctionName": "getUsers",
        "operateAt": "2026-10-30T06:37:20",
        "operateTime": "12ms",
        "operateStatus": 1,
        "executedSql": "SELECT * FROM users WHERE status = ? LIMIT ?,?"
      }
    ]
  },
  "timestamp": "2026-10-30T06:44:00"
}
```

所有字段除 `page` / `size` 外均可选。`operateStatus` 传字符串 `"1"`（成功）或 `"0"`（失败）。

> [!WARNING]
> 这是 GET 请求却用 `@RequestBody` 接收参数，浏览器地址栏直接访问或普通 `GET` 请求都拿不到过滤条件。必须用 HTTP 客户端（curl / Postman / axios）发带 body 的 GET。这是已知的设计问题，已在 README 的「已知问题」中记录。

### `POST /admin/user/page`

用户分页检索。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |

```json
{
  "page": 1,
  "size": 20,
  "name": "ali",
  "status": 1,
  "beginTime": "2026-10-01T00:00:00",
  "endTime": "2026-10-31T23:59:59",
  "loginDays": 7
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { "total": 5, "list": [ { "id": 63, "username": "demo", "status": 3 } ] },
  "timestamp": "2026-10-30T06:37:20"
}
```

| 字段 | 说明 |
| --- | --- |
| `page` / `size` | 必填，`page ≥ 1`，`1 ≤ size ≤ 1000`（默认 20） |
| `name` | 用户名模糊匹配 |
| `status` | 精确匹配状态值 |
| `beginTime` / `endTime` | 创建时间区间 |
| `loginDays` | 最近 N 天登录过 |

结果按「全部条件」组合成 Redis key 缓存 5 分钟；查不到结果时写一个空标记（同样 5 分钟）防止缓存穿透，同时返回 `404`。

### `GET /admin/user/info?id=64`

按 id 查询指定用户信息。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |
| 参数 | **query**：`id` |

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { "id": 64, "username": "alice", "nickname": "林晚", "status": 2 },
  "timestamp": "2026-10-30T06:45:00"
}
```

`id` 虽在代码里声明为 `required = false`，但实际必须传：不传或传不存在的 id 都会返回 `404 用户不存在`。

### `POST /user/register/admin`

凭邀请码自助注册管理员。

| 项 | 值 |
| --- | --- |
| 身份 | 公开（`/user/register` 的 POST 在过滤器白名单内） |
| 限流 | 5 次 / 60 秒（IP 维度） |

```json
{
  "username": "newadmin",
  "password": "123456",
  "importantPassword": "A1b2C3d4E5",
  "email": "newadmin@example.com",
  "phone": "13800000010"
}
```

请求体与 `POST /admin` 完全一致（同一个 `AddAdminDTO`）。区别只是这里不需要先登录。邀请码无效返回 `400`，用户名冲突返回 `409`。

---

## 反馈（4）

### `POST /feedback`

提交反馈。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |

```json
{ "type": "Bug反馈", "text": "在 Safari 里点头像下拉，菜单会闪一下就消失。" }
```

```json
{ "code": 200, "message": "操作成功", "data": null, "timestamp": "2026-10-30T06:36:17" }
```

`type` 与 `text` 均不可为空，`text` 前端限制 2000 字。提交人信息取自登录态（不接受客户端传入）。落库后异步给所有 `status = 2` 与 `3` 的账号发通知邮件——**邮件发送失败不影响接口返回**。

### `GET /feedback?page=1&size=50`

分页查询全部反馈。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |
| 参数 | **query**：`page`（默认 1，最小 1）、`size`（默认 50，1–200） |

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "type": "Bug反馈",
      "text": "在 Safari 里点头像下拉，菜单会闪一下就消失，Chrome 正常。",
      "userId": 63,
      "username": "demo",
      "userEmail": "demo@example.com",
      "status": 1,
      "createdAt": "2026-10-30T06:36:17",
      "finishedAt": "2026-10-30T06:36:20",
      "handleUserId": 63
    }
  ],
  "timestamp": "2026-10-30T06:46:00"
}
```

注意这个接口返回的是**数组**，不是 `{ total, list }`。`status` 取值：`0` 未处理、`1` 已处理、`-1` 已驳回。

### `PUT /feedback/status`

处理反馈并邮件回执提交人。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |

```json
{ "id": 1, "status": 1, "handleUserId": 63 }
```

`status` 只接受 `1`（完成）或 `-1`（驳回），其他值返回 `400`。`handleUserId` 虽在 DTO 中必填，但服务端实际以登录态为准，不接受客户端指定处理人——传当前登录用户 id 即可。

### `DELETE /feedback/handled`

批量删除所有已处理 / 已驳回的反馈。

| 项 | 值 |
| --- | --- |
| 身份 | 管理员 |

无请求体，返回 `data: null`。只删除 `status` 为 `1` 或 `-1` 的记录，未处理的反馈不受影响。

---

## 通用（1）

### `POST /files`

上传文件到阿里云 OSS。

| 项 | 值 |
| --- | --- |
| 身份 | 登录 |
| 限流 | 10 次 / 60 秒 |
| Content-Type | `multipart/form-data` |
| 字段名 | `file` |

```bash
curl -X POST http://localhost:5757/files \
  -H "Authorization: Bearer <token>" \
  -F "file=@report.pdf"
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "http://fifteen-hundun.oss-cn-beijing.aliyuncs.com/demo/files/1f2e3d4c.pdf",
  "timestamp": "2026-10-30T06:47:00"
}
```

| 限制 | 值 |
| --- | --- |
| 单文件大小 | ≤ 10 MB（`UPLOAD_MAX_SIZE_BYTES` 可调） |
| 图片扩展名 | `jpg` `jpeg` `png` `gif` `webp` `bmp` |
| 文档扩展名 | `pdf` `txt` `md` `doc` `docx` `xls` `xlsx` `ppt` `pptx` |
| 图片附加校验 | 读取文件头魔数，与扩展名不符即拒绝 |

对象名格式 `{username}/files/{uuid}.{ext}`，接口直接返回可访问的完整 URL。

---

## 附：接口速查表

| # | 方法 | 路径 | 身份 |
| --- | --- | --- | --- |
| 1 | POST | `/user/login/username-password` | 公开 |
| 2 | POST | `/user/login/phone-password` | 公开 |
| 3 | POST | `/user/login/email-password` | 公开 |
| 4 | POST | `/user/login/email-code/send` | 公开 |
| 5 | POST | `/user/login/email-code` | 公开 |
| 6 | POST | `/user/login/logout` | 登录 |
| 7 | POST | `/user/register` | 公开 |
| 8 | GET | `/user/info` | 登录 |
| 9 | PUT | `/user/nickname` | 登录 |
| 10 | PUT | `/user/email` | 登录 |
| 11 | PUT | `/user/phone` | 登录 |
| 12 | PUT | `/user/password` | 登录 |
| 13 | PUT | `/user/avatar` | 登录 |
| 14 | DELETE | `/user` | 登录 |
| 15 | POST | `/admin` | 超管 |
| 16 | GET | `/admin/users/count` | 管理员 |
| 17 | GET | `/admin/invite-code` | 管理员 |
| 18 | PUT | `/admin/user` | 管理员 |
| 19 | PUT | `/admin/user/status?username=` | 管理员 |
| 20 | DELETE | `/admin/user/{id}` | 管理员 |
| 21 | GET | `/admin/logs` | 管理员 |
| 22 | POST | `/admin/user/page` | 管理员 |
| 23 | GET | `/admin/user/info?id=` | 管理员 |
| 24 | POST | `/user/register/admin` | 公开 |
| 25 | POST | `/feedback` | 登录 |
| 26 | GET | `/feedback` | 管理员 |
| 27 | PUT | `/feedback/status` | 管理员 |
| 28 | DELETE | `/feedback/handled` | 管理员 |
| 29 | POST | `/files` | 登录 |
