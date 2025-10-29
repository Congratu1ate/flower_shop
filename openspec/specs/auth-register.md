# 用户注册页面规范（Auth - Register）

参考原型：`原型图/用户注册.png`

## 页面目标
- 支持用户以手机号注册账号，创建基础资料并设置安全密码。
- 注册成功后可选择自动登录或跳转到登录页（MVP：跳转登录页）。

## 关键元素
- 输入：`name`（姓名）、`phone`（手机号）、`password`（密码）、`confirmPassword`（确认密码）
- 可选：`sex`（性别："男"/"女"/"未知"）、`avatar`（头像上传；MVP 可不做上传）
- 勾选：`agree`（同意条款，必选）
- 按钮：`注册`、`去登录`
- 说明：首期不使用短信验证码/图形验证码。

## 表单校验规则
- `name`：必填；2-32 个字符；禁止纯空白。
- `phone`：必填；11 位手机号；注册前需服务端校验唯一性。
- `password`：必填；至少 8 位；需包含大小写字母与数字（MVP 至少数字+字母）；不允许与手机号相同。
- `confirmPassword`：必填；与 `password` 一致。
- `sex`：可选；枚举：`男`/`女`/`未知`；后端存储建议映射为 `M`/`F`/`N` 或留空。
- `avatar`：可选；图片类型限制（`jpeg/png`），大小建议 ≤2MB。
- `agree`：必选；未勾选不允许提交。

## 接口契约

### 注册
- 方法：`POST /api/auth/register`
- 请求：
```json
{
  "name": "张三",
  "phone": "13800138000",
  "password": "P@ssw0rd123",
  "sex": "男",
  "avatar": null
}
```
- 响应 201：
```json
{
  "id": 1,
  "name": "张三",
  "type": 0,
  "phone": "13800138000",
  "avatar": null,
  "createdAt": "2025-01-01T12:00:00Z"
}
```
- 错误：
  - 400 `VALIDATION_ERROR`：字段格式不合法
  - 409 `PHONE_EXISTS`：手机号已存在
  - 422 `WEAK_PASSWORD`：密码强度不足
  - 500 `INTERNAL_ERROR`

### 校验手机号是否已注册（可选）
- 方法：`GET /api/auth/check-phone?phone=13800138000`
- 响应 200：
```json
{ "exists": true }
```

## 安全策略
- 密码：使用 bcrypt（如 cost=10）哈希；禁止明文存储和回显。
- 唯一性：`user.phone` 需唯一索引，防止并发重复注册。
- 频控：注册接口限流（如 IP/设备级），防止批量恶意注册。
- 隐私：响应中不返回敏感字段；头像上传需鉴别文件类型与大小。
- MVP 范围：不引入短信验证码/图形验证码，仅表单校验 + 服务端频控。

## 交互流程
1. 用户填写表单，前端即时校验；可在失焦时检查手机号可用性。
2. 提交后显示加载状态；后端完成校验与入库。
3. 成功：提示“注册成功”，跳转登录页并带入手机号。
4. 失败：展示具体错误（如“手机号已注册”），保留用户已填写信息。

## 数据落库映射（基于需求文档）
- 表：`user`
- 字段：`type=0`（普通用户）、`name`、`phone`、`password`（哈希）、`sex`、`avatar`、`create_time`/`update_time` 自动维护。

## 验收标准
- 合规表单通过后可成功注册，服务端对手机号唯一性严格校验。
- 密码强度达标，服务端落库为哈希值，不能明文。
- 频控生效，批量尝试时返回限流提示。
- 成功注册跳转登录页，登录页可自动填充手机号（非强制）。
## Requirements: 用户注册

### Endpoint
- `POST /api/auth/register`

### Request
- `name` 字符串，必填，非空
- `phone` 字符串，必填，正则 `^\d{11}$`，必须唯一
- `password` 字符串，必填，长度 `6-18`
- `sex` 字符串，可选（示例：`男`/`女`），允许为空
- `avatar` 字符串，可选，头像 URL

### Behavior
- 检查 `phone` 唯一性，已存在则拒绝
- 使用 `BCrypt` 对 `password` 进行哈希后存储
- 默认新用户 `type=0`（普通用户）

### Response
- 201 Created：
  - Body：`UserResponse`（`id`, `name`, `type`, `phone`, `avatar`）
- 不会自动登录（不设置 Cookie）

### Errors
- 400 Bad Request：参数校验失败（手机号格式、密码长度、必填项为空）
- 409 Conflict：手机号已存在

### Data & Constraints
- `user.phone` 必须有唯一索引，避免并发注册冲突
- `user.password` 字段必须满足存储 `BCrypt` 哈希长度，建议 `VARCHAR(255)`
- 示例 DDL 修复：`ALTER TABLE user MODIFY COLUMN password VARCHAR(255) NOT NULL COMMENT '密码哈希';`

#### Scenario: 注册成功
- WHEN 提供有效参数且手机号未注册
- THEN 返回 201 和 `UserResponse`，数据库保存 `BCrypt` 哈希

#### Scenario: 注册失败（手机号重复）
- WHEN `phone` 已存在
- THEN 返回 409，不创建用户

#### Scenario: 参数校验失败
- WHEN `name`/`phone`/`password` 未满足校验规则
- THEN 返回 400，并包含错误消息