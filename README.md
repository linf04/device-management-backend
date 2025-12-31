# 设备管理系统 - 后端 API

## 项目概述

这是一个基于 Spring Boot 3.2 的设备管理系统后端 API，提供完整的设备管理、权限管理和安全检查功能。

## 核心特性

- ✅ **Spring Data JPA** - 简化数据库操作
- ✅ **Spring Security + JWT** - 安全认证和授权
- ✅ **自动审计** - 自动记录创建/更新时间和用户
- ✅ **全局异常处理** - 统一的错误响应
- ✅ **参数验证** - 使用 @Valid 注解
- ✅ **分页支持** - Pageable 接口
- ✅ **CORS 配置** - 允许前端跨域访问
- ✅ **Excel 导出** - 支持设备和权限列表导出

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security + JWT
- Apache POI（Excel 导出）
- Lombok（代码简化）
- Maven

## 项目结构

```
backend/
├── src/main/java/com/device/management/
│   ├── entity/              # JPA实体类
│   │   ├── User.java
│   │   ├── Device.java
│   │   ├── DeviceUsagePermission.java
│   │   └── SecurityCheck.java
│   ├── repository/          # Spring Data JPA接口
│   │   ├── UserRepository.java
│   │   ├── DeviceRepository.java
│   │   ├── DeviceUsagePermissionRepository.java
│   │   └── SecurityCheckRepository.java
│   ├── service/            # 业务逻辑层
│   │   ├── AuthService.java
│   │   ├── DeviceService.java
│   │   ├── DeviceUsagePermissionService.java
│   │   ├── SecurityCheckService.java
│   │   └── ExcelExportService.java
│   ├── controller/         # REST API控制器
│   │   ├── AuthController.java
│   │   ├── DeviceController.java
│   │   ├── DevicePermissionController.java
│   │   └── SecurityCheckController.java
│   ├── dto/               # 数据传输对象
│   │   ├── UserDTO.java
│   │   ├── DeviceDTO.java
│   │   ├── DeviceUsagePermissionDTO.java
│   │   ├── SecurityCheckDTO.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── ChangePasswordRequest.java
│   │   └── ApiResponse.java
│   ├── security/          # 安全配置
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthenticationFilter.java
│   ├── config/           # 其他配置
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   ├── exception/        # 异常处理
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   └── DeviceManagementApplication.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

## API 端点列表

### 认证 API

| 功能 | 方法 | 端点 | 请求体 | 说明 |
|------|------|------|--------|------|
| 登录 | POST | `/auth/login` | `{userId, password}` | 返回 JWT Token |
| 修改密码 | POST | `/auth/change-password` | `{userId, currentPassword, newPassword}` | - |
| 登出 | POST | `/auth/logout` | - | - |

### 设备 API

| 功能 | 方法 | 端点 | 参数/请求体                               | 说明 |
|------|------|------|--------------------------------------|------|
| 列表 | GET | `/devices` | `?page=1&size=10&userId=xxx&XXXXXXX` | 支持分页、筛选 |
| 详情 | GET | `/devices/{id}` | -                                    | - |
| 新增 | POST | `/devices` | Device 对象                            | - |
| 编辑 | PUT | `/devices/{id}` | Device 对象                            | - |
| 删除 | DELETE | `/devices/{id}` | -                                    | - |
| 导出 | GET | `/devices/export` | `?userId=xxx&XXXXXXX`                | 返回 Excel 文件 |

### 权限 API

| 功能 | 方法 | 端点 | 参数/请求体 | 说明 |
|------|------|------|------------|------|
| 列表 | GET | `/permissions` | `?page=1&size=10&deviceId=xxx&userId=xxx` | - |
| 详情 | GET | `/permissions/{id}` | - | - |
| 新增 | POST | `/permissions` | Permission 对象 | - |
| 编辑 | PUT | `/permissions/{id}` | Permission 对象 | - |
| 导出 | GET | `/permissions/export` | - | 返回 Excel 文件 |

### 安全检查 API

| 功能 | 方法 | 端点 | 参数/请求体 | 说明 |
|------|------|------|------------|------|
| 列表 | GET | `/security-checks` | `?page=1&size=10&deviceId=xxx&userId=xxx` | - |
| 详情 | GET | `/security-checks/{id}` | - | - |
| 新增 | POST | `/security-checks` | SecurityCheck 对象 | - |
| 编辑 | PUT | `/security-checks/{id}` | SecurityCheck 对象 | - |
| 删除 | DELETE | `/security-checks/{id}` | - | - |

### 共通 API

| 功能 | 方法 | 端点 | 参数/请求体 | 说明 |
|----|------|------|------------|------|
| 详情 | GET | `/api/dict/items` | - | - |

## 快速开始

### 前置要求

- Java 17+
- Maven 3.6+

## JWT 认证

### 工作流程

1. 用户调用 `/auth/login` 登录
2. 后端返回 JWT Token
3. 客户端在请求头中添加 `Authorization: Bearer <token>`
4. 后端验证 Token 并处理请求

### Token 配置

在 `application.properties` 中修改：

```properties
# JWT 密钥（建议至少 256 位）
jwt.secret=your-secret-key-minimum-256-bits-long-change-this-in-production-environment-please

# Token 过期时间（毫秒），默认 24 小时
jwt.expiration=86400000
```

