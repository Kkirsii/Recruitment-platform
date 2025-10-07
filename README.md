# 招聘平台 (Recruitment Platform)

一个现代化的在线招聘平台，支持求职者和招聘方双向交互，提供职位发布、简历投递、实时聊天等功能。

## 🎯 项目概述

这是一个基于前后端分离架构的招聘平台，包含用户端、管理员端和文件服务器三个主要模块。平台采用现代化的技术栈，提供完整的招聘流程管理功能。

## 🏗️ 技术架构

### 后端 (RecruitmentPlatform)
- **框架**: Spring Boot 3.2.4
- **语言**: Java 21
- **数据库**: MySQL
- **ORM**: MyBatis Plus
- **缓存**: Redis
- **认证**: JWT
- **实时通信**: WebSocket
- **构建工具**: Maven

### 前端

#### 用户端 (rp-frontend)
- **框架**: Vue 3.5.13
- **语言**: TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios

#### 管理员端 (admin-frontend)
- **框架**: Vue 3.5.22
- **语言**: JavaScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios

### 文件服务器 (file_server.py)
- **语言**: Python 3.6+
- **功能**: 文件上传下载服务
- **配置**: YAML

## 🔄 招聘流程

### 完整招聘流程
1. **简历投递** (状态: 0) - 求职者投递简历
2. **简历筛选** (状态: 1) - 管理员通过简历筛选
3. **面试邀请** (状态: 2) - 发送面试邀请邮件
4. **面试通过** (状态: 3) - 面试通过待录用
5. **录用通知** (状态: 6) - 发送录用通知邮件

### 其他状态
- **不匹配** (状态: 4) - 候选人不符合要求
- **放弃申请** (状态: 5) - 候选人主动放弃

## 🚀 功能特性

### 用户端功能
- 🔐 **用户认证**: 注册、登录、邮箱验证
- 📝 **个人资料**: 个人信息管理、简历上传
- 🔍 **职位浏览**: 查看职位列表、搜索筛选
- 📄 **职位详情**: 查看职位详细信息
- 📤 **简历投递**: 一键投递简历
- 📊 **投递状态**: 查看投递进度和状态
- 💬 **实时聊天**: WebSocket在线聊天功能

### 管理员端功能
- 🔐 **管理员登录**: 安全的管理员认证
- 📊 **数据统计**: 职位数量、投递统计、录用统计等
- 📋 **职位管理**: 发布、编辑、删除职位
- 📥 **投递管理**: 查看、审核简历投递
- 🔄 **流程管理**: 简历筛选、面试邀请、录用通知
- 📧 **邮件通知**: 自动发送面试邀请和录用通知
- 📈 **仪表板**: 数据可视化展示

### 通用功能
- 🔒 **安全认证**: JWT令牌认证
- 📁 **文件管理**: 图片、简历文件上传下载
- 💾 **数据持久化**: MySQL数据库存储
- ⚡ **性能优化**: Redis缓存
- 📧 **邮件服务**: 面试邀请和录用通知邮件
- 📱 **响应式设计**: 支持多设备访问

## 📁 项目结构

```
Recruitment platform/
├── RecruitmentPlatform/           # 后端 Spring Boot 项目
│   ├── src/main/java/
│   │   └── com/kkirsii/recruitmentplatform/
│   │       ├── config/           # 配置类
│   │       ├── controller/       # 控制器
│   │       ├── mapper/          # 数据访问层
│   │       ├── pojo/            # 实体类和DTO
│   │       ├── server/          # 业务逻辑层
│   │       ├── util/            # 工具类
│   │       └── ws/              # WebSocket
│   └── src/main/resources/       # 配置文件
├── rp-frontend/                   # 用户端 Vue 项目
│   ├── src/
│   │   ├── components/          # 组件
│   │   ├── views/               # 页面
│   │   ├── router/              # 路由
│   │   └── stores/              # 状态管理
├── admin-frontend/               # 管理员端 Vue 项目
│   ├── src/
│   │   ├── components/          # 组件
│   │   ├── views/               # 页面
│   │   ├── router/              # 路由
│   │   └── stores/              # 状态管理
├── file_server.py               # 文件服务器
├── start_server.sh              # 启动脚本
└── stop_server.sh               # 停止脚本
```

## 🛠️ 快速开始

### 环境要求
- Java 21+
- MySQL 8.0+
- Redis 6.0+
- Node.js 20.19.0+
- Python 3.6+

### 启动步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd "Recruitment platform"
```

2. **启动后端服务**
```bash
cd RecruitmentPlatform
mvn clean install
mvn spring-boot:run
```

3. **启动文件服务器**
```bash
pip install -r requirements.txt
python file_server.py
```

4. **启动用户端前端**
```bash
cd rp-frontend
npm install
npm run dev
```

5. **启动管理员端前端**
```bash
cd admin-frontend
npm install
npm run dev
```

### 访问地址
- 用户端: http://localhost:5173
- 管理员端: http://localhost:5174
- 后端API: http://localhost:8080
- 文件服务器: http://localhost:8080

### 一键启动脚本
项目提供了便捷的启动脚本：

**Windows用户:**
```bash
# 启动前端服务
start_frontend.bat

# 停止前端服务
stop_frontend.bat
```

**Linux/Mac用户:**
```bash
# 启动前端服务
./start_frontend.sh

# 停止前端服务
./stop_frontend.sh
```

## 🔧 配置说明

### 数据库配置
在 `RecruitmentPlatform/src/main/resources/application.properties` 中配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recruitment_platform
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Redis配置
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 文件服务器配置
在 `config.yaml` 中配置文件存储路径：

```yaml
Path:
  Image: "/path/to/images"
  Resume: "/path/to/resumes"
```

### 邮件服务配置
在 `application.properties` 中配置邮件服务：

```properties
# 邮件服务配置
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your_email@qq.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 📋 API 接口

### 用户接口
- `POST /user/login` - 用户登录
- `POST /user/signup` - 用户注册
- `GET /user/info` - 获取用户信息
- `GET /jobs` - 获取职位列表
- `GET /jobs/{id}` - 获取职位详情
- `POST /jobs/{id}/apply` - 投递简历
- `GET /user/job/get_submitState` - 获取投递状态

### 管理员接口
- `POST /admin/login` - 管理员登录
- `POST /admin/job/publish` - 发布职位
- `GET /admin/job/getlist` - 获取职位列表
- `GET /admin/job/submitlist` - 获取投递列表
- `PUT /admin/job/{id}` - 更新职位
- `DELETE /admin/job/{id}` - 删除职位
- `PUT /admin/userinfo/push_1` - 通过简历筛选
- `PUT /admin/userinfo/send_invitation` - 发送面试邀请
- `PUT /admin/userinfo/push_3` - 面试通过
- `PUT /admin/userinfo/send_offer` - 发送录用通知
- `PUT /admin/userinfo/push_4` - 标记不匹配
- `PUT /admin/userinfo/push_5` - 标记放弃申请

### WebSocket接口
- `ws://localhost:8080/chat` - 实时聊天

## 🎨 界面预览

### 用户端
- 登录/注册页面
- 个人资料管理
- 职位浏览和搜索
- 职位详情查看
- 投递状态跟踪
- 实时聊天界面

### 管理员端
- 管理员登录
- 数据统计仪表板
- 职位发布和管理
- 投递审核管理
- 面试邀请表单
- 招聘流程管理

## 🔒 安全特性

- JWT令牌认证
- 密码加密存储
- 文件上传安全检查
- SQL注入防护
- XSS攻击防护
- CORS跨域配置

## 📊 数据库设计

主要数据表：
- `users` - 用户信息表
- `admins` - 管理员表
- `jobs` - 职位信息表
- `user_profiles` - 用户档案表
- `submit_states` - 投递状态表
- `invitation_info` - 面试邀请信息表

### 投递状态说明
- `0` - 简历筛选中
- `1` - 待发送面试邀请
- `2` - 已发送面试邀请
- `3` - 面试通过待录用
- `4` - 不匹配
- `5` - 放弃申请
- `6` - 已录用

## 🚀 部署说明

### 生产环境部署
1. 构建前端项目
2. 打包后端应用
3. 配置Nginx反向代理
4. 设置SSL证书
5. 配置数据库和Redis

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

MIT License

## 📞 联系方式

如有问题或建议，请联系项目维护者。
