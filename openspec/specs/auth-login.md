# 用户登录页面规范（Auth - Login）
## Requirements: 用户登录

### Endpoint
- `POST /api/auth/login`

### Request
- `phone` 字符串，必填，正则 `^\d{11}$`
- `password` 字符串，必填，长度 `6-18`
- `rememberMe` 布尔，可选（当前仅作为预留，不改变 Token 过期时长）

### Behavior
- 使用 `BCrypt` 验证密码（数据库中存储密码哈希）
- 颁发 `JWT`（算法 `HS256`），载荷含 `uid`, `type`, `name`, `phone`
- 将 `JWT` 同步设置到 HttpOnly Cookie：`AUTH_TOKEN`
- 返回体包含：`token`, `expireSeconds`, `user`

### Response
- 200 OK：
  - Body：
    - `token` 字符串，JWT
    - `expireSeconds` 数值，Token 过期秒数
    - `user` 对象：`id`, `name`, `type`, `phone`, `avatar`
  - Cookie：`AUTH_TOKEN`，`HttpOnly`，`Secure`（生产建议开启），`Path=/`

### Errors
- 400 Bad Request：参数校验失败（手机号格式、密码长度等）
- 401 Unauthorized：手机号不存在或密码错误

### Security
- 密码使用 `BCrypt` 存储，绝不回传明文密码
- `jwt.secret` 使用高熵随机值（Base64），仅服务器持有
- Cookie 使用 `HttpOnly` 防止前端 JS 访问；生产环境建议开启 `Secure` 与 CSRF 保护

#### Scenario: 登录成功
- WHEN 提供有效手机号与密码
- THEN 返回 200，设置 `AUTH_TOKEN` Cookie，并在响应体返回 `token`、`expireSeconds` 与 `user`

#### Scenario: 登录失败（凭证错误）
- WHEN 手机号不存在或密码不匹配
- THEN 返回 401，不设置 Cookie

#### Scenario: 参数校验失败
- WHEN `phone` 或 `password` 不满足格式约束
- THEN 返回 400，并包含错误消息

#### Scenario: 记住我预留字段
- WHEN `rememberMe=true`
- THEN 当前行为不改变 Token 过期策略（未来可扩展更长过期时间）
参考原型：`原型图/用户登录.png`

## 页面目标
- 为用户与管理员提供统一登录入口，基于手机号与密码鉴权。
- 登录成功后根据角色分流：用户进入 `/home`，管理员进入 `/dashboard`。

## 关键元素
- 输入：`phone`（手机号）、`password`（密码）
- 选项：`rememberMe`（记住我，可选）
- 按钮：`登录`、`去注册`、`忘记密码（首期不实现）`
- 提示：错误消息、表单校验提示

## 表单校验规则
- `phone`：必填；11 位手机号码；推荐规则：`^1[3-9]\d{9}$`（大陆号段），或采用更通用 `^\d{11}$`。
- `password`：必填；长度 6-20；不在前端做复杂度强校验（复杂度在注册时强校验）。
- `rememberMe`：布尔；选中时在前端延长本地会话保存时间（不影响后端令牌有效期）。

## 接口契约

### 登录
- 方法：`POST /api/auth/login`
- 请求：
```json
{
  "phone": "13800138000",
  "password": "P@ssw0rd",
  "rememberMe": true
}
```
- 响应 200：
```json
{
  "token": "jwt-token-string",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "name": "张三",
    "type": 0,
    "phone": "13800138000",
    "avatar": "/static/avatars/u1.png"
  }
}
```
- 错误：
  - 400 `VALIDATION_ERROR`：字段格式不合法
  - 401 `INVALID_CREDENTIALS`：账号或密码错误
  - 403 `USER_DISABLED`：用户被禁用或锁定
  - 429 `RATE_LIMITED`：达到尝试上限稍后再试
  - 500 `INTERNAL_ERROR`

### 获取当前用户
- 方法：`GET /api/auth/me`
- 说明：用于刷新登录态或进入受限路由时获取用户信息。
- 响应 200：
```json
{
  "id": 1,
  "name": "张三",
  "type": 0,
  "phone": "13800138000",
  "avatar": "/static/avatars/u1.png"
}
```

## 安全策略
- 令牌：统一使用 HttpOnly Cookie 存储 JWT；首期不启用 Bearer Token。
- 密码：后端使用 bcrypt 哈希校验；禁止明文存储。
- 频控：登录失败累计达到阈值（如 5 次/15 分钟）后短暂锁定。
- CSRF：基于 Cookie 的会话需配合 CSRF 防护。
- MVP 范围：不提供忘记密码、短信验证码、图形验证码入口；相关 UI 隐藏或弱化。

## 交互流程
1. 用户输入手机号与密码，提交表单。
2. 校验通过→发送登录请求→显示加载状态。
3. 成功：保存令牌与用户信息，按 `type` 分流跳转；显示欢迎提示。
4. 失败：展示错误提示，不泄露具体是手机号还是密码错误。

## 边界与异常
- 初始管理员：系统需预置至少一个 `type=1` 管理员账号（种子数据）。
- 账号锁定：超限锁定期间返回 429；前端提示稍后重试或找回密码。
- 兼容性：允许国际化手机号时需调整校验并增加国家码选择。

## 验收标准
- 正确凭证登录成功并分流至对应主页。
- 输入非法或凭证错误时给出明确提示，不暴露隐私信息。
- 失败多次后触发限流与锁定反馈。
- 刷新或重新进入受限路由可通过 `/api/auth/me` 获取当前用户信息。