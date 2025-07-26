# 🎾 双人在线聊天室系统

一个基于 Spring Boot + WebSocket + MySQL + AI 的智能实时双人在线聊天室系统，通过AI内容分析技术，鼓励用户文明聊天，让人们"好好说话"。

# 🎾 双人在线聊天室系统架构图

## 系统整体架构流程图

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                 🎾 双人在线聊天室系统 - "让人们好好说话"                                │
│                                      System Architecture                                        │
└─────────────────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────────────────────┐
                                    │            👤 用户层              │
                                    │         (Frontend Layer)       │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   📱 Web Frontend    │          │   💬 Chat Interface │          │   🛡️ Security Layer │
        │                     │          │                     │          │                     │
        │ • login.html        │          │ • chat-enhanced.html│          │ • JWT Token Auth   │
        │ • register.html     │          │ • WebSocket Client  │          │ • Session Management│
        │ • CSS/JS Assets     │          │ • Real-time UI      │          │ • CORS Config       │
        │ • Form Validation   │          │ • Message Display   │          │ • XSS Protection    │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    └─────────────────────────────────┼─────────────────────────────────┘
                                                    │
                                    ┌─────────────────────────────────┐
                                    │          🌐 网络层                │
                                    │        (Network Layer)          │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   📡 HTTP/REST API   │          │   🔌 WebSocket      │          │   🔒 Authentication │
        │                     │          │                     │          │                     │
        │ • POST /api/login   │          │ • ws://*/ws/chat    │          │ • JWT Validation    │
        │ • POST /api/register│          │ • Real-time Comm   │          │ • Token Refresh     │
        │ • POST /api/chat/*  │          │ • Message Broadcast │          │ • User Session      │
        │ • GET /api/scores/* │          │ • User Join/Leave   │          │ • Security Filter   │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    └─────────────────────────────────┼─────────────────────────────────┘
                                                    │
                                    ┌─────────────────────────────────┐
                                    │           ⚙️ 控制层              │
                                    │       (Controller Layer)        │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   👥 User Controller │          │   💬 Chat Controller │          │   📊 Score Controller│
        │                     │          │                     │          │                     │
        │ • LoginController   │          │ • ChatRoomController│          │ • ScoreController   │
        │ • RegisterController│          │ • MessageController │          │ • LeaderboardCtrl   │
        │ • User Management   │          │ • Room Management   │          │ • Achievement Mgmt  │
        │ • JWT Generation    │          │ • History Query     │          │ • Analytics API     │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    └─────────────────────────────────┼─────────────────────────────────┘
                                                    │
                                    ┌─────────────────────────────────┐
                                    │          🔧 业务层                │
                                    │        (Service Layer)          │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   🔐 Auth Service    │          │   💬 Chat Service    │          │   🤖 AI Service     │
        │                     │          │                     │          │                     │
        │ • User Auth Logic   │          │ • Message Processing│          │ • Content Analysis  │
        │ • Password Encrypt  │          │ • Room Management   │          │ • Toxicity Detection│
        │ • Session Handling  │          │ • History Storage   │          │ • Sentiment Analysis│
        │ • Security Validation│          │ • Real-time Sync   │          │ • Keyword Filter    │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   🏆 Score Service   │          │   📈 Analytics Svc  │          │   🔔 Notification   │
        │                     │          │                     │          │                     │
        │ • Score Calculation │          │ • User Behavior     │          │ • Real-time Alert   │
        │ • Level Management  │          │ • Statistics Report │          │ • Achievement Push  │
        │ • Achievement System│          │ • Trend Analysis    │          │ • Warning System    │
        │ • Leaderboard Logic │          │ • Data Insights     │          │ • Progress Update   │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    └─────────────────────────────────┼─────────────────────────────────┘
                                                    │
                                    ┌─────────────────────────────────┐
                                    │          🗃️ 数据层               │
                                    │         (Data Layer)            │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   📊 Repository      │          │   🔄 MyBatis Mapper │          │   💾 Cache Layer    │
        │                     │          │                     │          │                     │
        │ • UserRepository    │          │ • ControlsMapper    │          │ • Redis Cache       │
        │ • ChatMessageRepo   │          │ • LoginMapper       │          │ • Session Cache     │
        │ • ChatRoomRepo      │          │ • RegisterMapper    │          │ • Message Cache     │
        │ • ScoreRepository   │          │ • ChatRoomMapper    │          │ • User State Cache  │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
                    │                                 │                                 │
                    └─────────────────────────────────┼─────────────────────────────────┘
                                                    │
                                    ┌─────────────────────────────────┐
                                    │         🗄️ 存储层                │
                                    │        (Storage Layer)          │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   🗃️ MySQL Database  │          │   📁 File Storage   │          │   🔍 Search Engine  │
        │                     │          │                     │          │                     │
        │ • user_information  │          │ • Static Assets     │          │ • Message Index     │
        │ • chat_rooms        │          │ • User Avatars      │          │ • Content Search    │
        │ • chat_messages     │          │ • File Uploads      │          │ • History Query     │
        │ • user_scores       │          │ • Log Files         │          │ • Analytics Data    │
        │ • ai_analysis_results│          │ • Backup Files      │          │ • Report Generation │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘

                                    ┌─────────────────────────────────┐
                                    │         🌐 外部服务层             │
                                    │       (External Services)       │
                                    └─────────────────────────────────┘
                                                    │
                    ┌─────────────────────────────────┼─────────────────────────────────┐
                    │                                 │                                 │
        ┌─────────────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
        │   🤖 AI/ML Services  │          │   📧 Email Service  │          │   📱 Push Service   │
        │                     │          │                     │          │                     │
        │ • LLM API (DashScope)│          │ • Registration Mail │          │ • Mobile Push      │
        │ • Toxicity Detection│          │ • Password Reset    │          │ • Browser Notify   │
        │ • Sentiment Analysis│          │ • Notification Mail │          │ • Real-time Alert  │
        │ • Content Filter    │          │ • System Announcements│          │ • Status Update    │
        └─────────────────────┘          └─────────────────────┘          └─────────────────────┘
```

## 📋 项目简介

本项目是一个功能完善且具有社会价值的双人在线聊天室系统，**核心理念是通过AI技术促进网络文明，让每个人都能好好说话**。系统不仅提供实时通信功能，更重要的是通过智能内容分析，实时监测用户发言内容，建立积分奖惩机制，营造健康的网络交流环境。

### 🎯 项目理念

在当今网络环境中，不文明言论、恶意攻击、网络暴力等现象屡见不鲜。本项目致力于：

- **🌟 促进网络文明**：通过AI分析技术，实时识别不当言论
- **📈 正向激励**：积分奖励机制鼓励用户发表正面、友善的内容
- **🛡️ 内容净化**：自动过滤和警示不当内容，维护健康的聊天环境
- **🎓 教育引导**：帮助用户认识到文明用语的重要性
- **🤝 和谐交流**：创建一个相互尊重、友善交流的数字空间

### 🌟 主要特性

#### 🤖 AI内容分析系统
- **智能言论监测**：实时分析每条消息内容，识别不当言论
- **越网行为判断**：AI自动判断消息是否包含不文明、攻击性或有害内容
- **情感分析**：分析消息的情感倾向，识别负面情绪表达
- **关键词过滤**：智能识别敏感词汇和不当表达
- **上下文理解**：结合聊天上下文，更准确地判断消息意图

#### 📊 积分激励系统
- **文明加分**：发送正面、友善、建设性的消息 **+1分**
- **越网扣分**：发送不当、攻击性、有害的消息 **-1分**
- **积分等级**：根据积分划分用户文明等级
- **成就系统**：设置文明聊天成就，鼓励持续正向表达
- **积分展示**：实时显示用户当前积分和等级状态

#### 💬 聊天室功能
- **用户管理**：完整的用户注册、登录、身份验证系统
- **实时通信**：基于 WebSocket 的实时消息推送
- **安全认证**：JWT Token 认证机制，保障用户数据安全
- **聊天记录**：消息持久化存储，支持历史记录查询
- **聊天室管理**：支持创建、加入、关闭聊天室
- **响应式界面**：现代化的前端界面，支持移动端适配
- **在线状态**：实时显示用户在线状态

## 🛠️ 技术栈

### 后端技术
- **Java 17** - 编程语言
- **Spring Boot 3.5.0** - 应用框架
- **Spring Security** - 安全认证框架
- **Spring Data JPA** - 数据访问层
- **MyBatis** - ORM框架
- **WebSocket** - 实时通信
- **MySQL 8.0** - 数据库
- **Redis** - 缓存中间件
- **Druid** - 数据库连接池
- **JWT** - 身份认证
- **Maven** - 项目构建工具

### 🤖 AI分析技术
- **大语言模型** - 内容理解和情感分析
- **自然语言处理** - 文本分析和关键词识别
- **情感计算** - 消息情感倾向分析
- **内容分类** - 不当内容自动识别
- **机器学习** - 持续学习和优化分析准确性
- **文本分析API** - 高效的内容分析服务

### 前端技术
- **HTML5** - 页面结构
- **CSS3** - 样式设计
- **JavaScript** - 交互逻辑
- **WebSocket API** - 实时通信
- **Bootstrap** - UI框架
- **jQuery** - JavaScript库

### 开发工具
- **Swagger** - API文档
- **Logback** - 日志管理
- **Lombok** - 代码简化

## 🚀 快速开始

### 环境要求

- Java 17+
- MySQL 8.0+
- Maven 3.6+
- Redis 6.0+ (可选)

### 1. 克隆项目

```bash
git clone https://github.com/lingview/tennis-voice-chat.git
cd tennis-voice-chat
```

### 2. 数据库配置

创建 MySQL 数据库并导入 SQL 文件：

```sql
-- 创建数据库
CREATE DATABASE chat CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 导入数据表结构
source chat.sql;
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chat?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 4. 编译和运行

```bash
# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

### 5. 访问系统

- 系统地址：http://localhost:8080
- 登录页面：http://localhost:8080/login.html
- 注册页面：http://localhost:8080/register.html
- 聊天界面：http://localhost:8080/chat-enhanced.html
- API文档：http://localhost:8080/swagger-ui.html

## 📊 数据库设计

### 用户表 (user_information)

*包含用户基本信息和积分系统*
```sql
CREATE TABLE `user_information` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id，自增',
  `username` varchar(255) NOT NULL COMMENT '用户名，不能重复',
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码，使用BCrypt加密',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int NOT NULL COMMENT '用户状态，普通用户：1，管理员：0，封禁用户：2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB;
```

### 聊天室表 (chat_room)
```sql
CREATE TABLE `chat_room` (
  `uuid` varchar(255) NOT NULL COMMENT '房间的唯一id',
  `create_user` varchar(255) NOT NULL COMMENT '创建人',
  `status` int NOT NULL COMMENT '1为正常 2为关闭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uuid`),
  KEY `create_user` (`create_user`),
  CONSTRAINT `create_user` FOREIGN KEY (`create_user`) REFERENCES `user_information` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
```

### 聊天消息表 (chat_messages)
```sql
CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_uuid` varchar(255) NOT NULL COMMENT '房间UUID',
  `sender_username` varchar(255) NOT NULL COMMENT '发送者用户名',
  `message_type` int NOT NULL DEFAULT '1' COMMENT '消息类型: 1=文本, 2=系统, 3=图片',
  `content` text NOT NULL COMMENT '消息内容',
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态: 1=正常, 2=已删除',
  `reply_to_id` bigint DEFAULT NULL COMMENT '回复消息ID',
  PRIMARY KEY (`id`),
  KEY `idx_room_uuid` (`room_uuid`),
  KEY `idx_sender_username` (`sender_username`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB;
```

### 积分记录表 (user_scores)

*记录用户文明聊天积分变化*
```sql
CREATE TABLE `user_scores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `message_id` bigint NOT NULL COMMENT '消息 ID',
  `score_change` int NOT NULL COMMENT '积分变化：+1 或 -1',
  `reason` varchar(500) NOT NULL COMMENT '变化原因：文明或越网',
  `ai_analysis` text COMMENT 'AI分析结果详情',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `current_total_score` int NOT NULL DEFAULT '0' COMMENT '当前总积分',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_user_scores_username` FOREIGN KEY (`username`) REFERENCES `user_information` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
```

### AI分析结果表 (ai_analysis_results)

*存储消息的AI分析详细结果*
```sql
CREATE TABLE `ai_analysis_results` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message_id` bigint NOT NULL COMMENT '消息 ID',
  `analysis_type` varchar(50) NOT NULL COMMENT '分析类型：sentiment/toxicity/keywords',
  `analysis_result` text NOT NULL COMMENT '分析结果 JSON',
  `confidence_score` decimal(3,2) NOT NULL COMMENT '置信度分数 0-1',
  `is_violation` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否违规：0=正常 1=越网',
  `keywords_detected` text COMMENT '检测到的关键词',
  `sentiment_score` decimal(3,2) COMMENT '情感分数 -1到1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分析时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_analysis_type` (`analysis_type`),
  KEY `idx_is_violation` (`is_violation`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_ai_analysis_message` FOREIGN KEY (`message_id`) REFERENCES `chat_messages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
```

## 🔌 API 接口文档

### 用户认证接口

#### 用户注册
```http
POST /api/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### 用户登录
```http
POST /api/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例：**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "message": "登录成功"
}
```

### 聊天室管理接口

#### 创建聊天室
```http
POST /api/chat/create
Authorization: Bearer <token>
```

#### 加入聊天室
```http
POST /api/chat/join
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomId": "room-uuid"
}
```

#### 获取聊天历史
```http
GET /api/chat/history/{roomId}
Authorization: Bearer <token>
```

#### 关闭聊天室
```http
POST /api/chat/close
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomId": "room-uuid"
}
```

### 🤖 AI分析接口

#### 获取消息分析结果
```http
GET /api/ai/analysis/{messageId}
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "success": true,
  "data": {
    "messageId": 123,
    "isViolation": false,
    "sentimentScore": 0.8,
    "confidenceScore": 0.95,
    "analysisType": "sentiment",
    "keywordsDetected": ["hello", "good"],
    "analysisResult": {
      "sentiment": "positive",
      "toxicity": "low",
      "emotion": "friendly"
    }
  }
}
```

#### 重新分析消息
```http
POST /api/ai/reanalyze
Authorization: Bearer <token>
Content-Type: application/json

{
  "messageId": 123
}
```

### 📊 积分系统接口

#### 获取用户积分
```http
GET /api/scores/user/{username}
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "success": true,
  "data": {
    "username": "testuser",
    "totalScore": 85,
    "level": "civilized",
    "levelName": "文明用户",
    "nextLevelScore": 100,
    "positiveMsgCount": 90,
    "negativeMsgCount": 5,
    "achievements": [
      {
        "name": "文明使者",
        "description": "连续30条正面消息",
        "unlockedAt": "2025-07-26T10:30:00Z"
      }
    ]
  }
}
```

#### 获取积分历史
```http
GET /api/scores/history/{username}?page=1&size=20
Authorization: Bearer <token>
```

#### 获取积分排行榜
```http
GET /api/scores/leaderboard?limit=10
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "success": true,
  "data": {
    "leaderboard": [
      {
        "rank": 1,
        "username": "civilized_user",
        "totalScore": 245,
        "level": "exemplary",
        "levelName": "模范用户"
      },
      {
        "rank": 2,
        "username": "friendly_user",
        "totalScore": 189,
        "level": "civilized",
        "levelName": "文明用户"
      }
    ]
  }
}
```

### WebSocket 通信

#### 连接地址
```
ws://localhost:8080/ws/chat
```

#### 消息格式

**加入房间：**
```json
{
  "type": "join",
  "roomId": "room-uuid",
  "username": "username"
}
```

**发送消息：**
```json
{
  "type": "message",
  "roomId": "room-uuid",
  "username": "username",
  "content": "Hello World!"
}
```

**离开房间：**
```json
{
  "type": "leave",
  "roomId": "room-uuid",
  "username": "username"
}
```

**AI分析结果通知：**
```json
{
  "type": "aiAnalysis",
  "messageId": 123,
  "username": "username",
  "isViolation": false,
  "scoreChange": 1,
  "newTotalScore": 86,
  "level": "civilized",
  "analysisResult": {
    "sentiment": "positive",
    "confidenceScore": 0.95,
    "reason": "正面积极的表达"
  }
}
```

**积分更新通知：**
```json
{
  "type": "scoreUpdate",
  "username": "username",
  "scoreChange": -1,
  "newTotalScore": 84,
  "level": "normal",
  "reason": "检测到不当言论",
  "warning": "请注意文明用语，营造健康的聊天环境"
}
```

**成就解锁通知：**
```json
{
  "type": "achievement",
  "username": "username",
  "achievement": {
    "name": "文明使者",
    "description": "连续30条正面消息",
    "icon": "medal",
    "points": 50
  }
}
```

## 💻 使用指南

### 1. 用户注册和登录

1. 打开浏览器访问 http://localhost:8080/register.html
2. 填写用户名、邮箱和密码进行注册
3. 注册成功后自动跳转到登录页面
4. 使用注册的用户名和密码登录系统

### 2. 创建聊天室

1. 登录成功后进入聊天界面
2. 点击"创建房间"按钮
3. 系统自动生成房间ID
4. 将房间ID分享给其他用户

### 3. 加入聊天室

1. 在聊天界面输入房间ID
2. 点击"加入房间"按钮
3. 成功加入后可以开始聊天

### 4. 发送消息

1. 在消息输入框中输入要发送的内容
2. 按回车键或点击发送按钮
3. 消息会实时同步给房间内的所有用户

### 5. 查看聊天历史

- 加入房间后会自动加载历史消息
- 支持消息时间戳显示
- 区分不同用户的消息

### 6. 🤖 AI内容分析体验

#### 智能消息分析
1. **实时分析**：发送消息后，AI立即对内容进行分析
2. **情感检测**：系统会分析消息的情感倾向（正面/负面/中性）
3. **内容分类**：自动识别消息类型（文明/不当/攻击性）
4. **关键词提取**：检测消息中的重要关键词和敏感词汇

#### 分析结果展示
- 消息旁显示情感指示器（😊 正面、😐 中性、😡 负面）
- 置信度分数展示（高、中、低）
- 对于不当内容，显示警告标识和改进建议

### 7. 📊 积分系统使用

#### 积分获取规则
- **文明加分**：发送正面、友善、有建设性的消息即可获得 **+1分**
- **越网扣分**：发送不当、攻击性、有害的消息将被扣除 **-1分**
- **实时反馈**：每次发送消息后，系统会实时显示积分变化

#### 等级系统
- **新手用户** (0-19分)：刚入门的用户
- **普通用户** (20-49分)：表现良好的用户
- **文明用户** (50-99分)：一贯保持文明的用户
- **模范用户** (100-199分)：模范作用突出的用户
- **文明大使** (200+分)：最高等级，社区文明的引领者

#### 成就系统
- **文明初心者**：连续10条正面消息
- **文明使者**：连续30条正面消息
- **文明守护者**：连续50条正面消息
- **文明先锋**：连续100条正面消息
- **文明大使**：连续200条正面消息

#### 积分查看
- 聊天界面右上角显示当前积分和等级
- 点击积分显示详细的积分历史
- 查看排行榜，了解自己在社区中的排名

### 8. 🛡️ 文明聊天建议

#### 正面示例（推荐）
- “您好！很高兴认识您。”
- “谢谢分享，这个观点很有趣。”
- “我不同意您的看法，但尊重您的意见。”
- “让我们一起探讨这个问题吧。”

#### 负面示例（避免）
- 人身攻击和侮辱性言论
- 恶意辱骂和粗俗语言
- 歧视性言论和仇恨言论
- 恶意挑衅和网络暴力

#### 文明技巧
1. **尊重他人**：使用离貌用语，尊重不同意见
2. **理性讨论**：保持冒静，理性表达自己的观点
3. **积极沟通**：多使用建设性的语言，少使用破坏性的词汇
4. **包容理解**：对不同背景的人保持开放和包容的态度

## 🔒 安全特性

### 身份认证
- JWT Token 认证机制
- BCrypt 密码加密
- 登录状态持久化

### 数据安全
- SQL 注入防护
- XSS 攻击防护
- CSRF 保护机制

### WebSocket 安全
- 连接握手验证
- 消息来源验证
- 自动断线重连

## 📁 项目结构

```
tennis-voice-chat/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── xyz/lingview/chat/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── entity/          # 实体类
│   │   │       ├── repository/      # 数据访问层
│   │   │       ├── service/         # 业务逻辑层
│   │   │       ├── security/        # 安全配置
│   │   │       ├── websocket/       # WebSocket处理
│   │   │       └── StartServer.java # 启动类
│   │   └── resources/
│   │       ├── static/              # 静态资源
│   │       ├── mapper/              # MyBatis映射
│   │       └── application.yml      # 配置文件
├── chat.sql                         # 数据库脚本
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## 🎨 界面预览

### 登录界面
- 简洁的用户登录表单
- 输入验证和错误提示
- 自动跳转到注册页面

### 聊天界面
- 现代化的聊天界面设计
- 实时消息显示
- 用户状态指示
- 响应式布局

### 功能特色
- 消息气泡显示
- 时间戳标记
- 用户头像显示
- 在线状态指示

## 🐛 常见问题

### Q1: 数据库连接失败
**A:** 请检查 MySQL 服务是否启动，数据库配置是否正确。

### Q2: WebSocket 连接失败
**A:** 确保防火墙允许 WebSocket 连接，检查端口是否被占用。

### Q3: 用户登录失败
**A:** 检查用户名密码是否正确，确认用户状态为正常。

### Q4: 消息发送失败
**A:** 确认用户已正确加入聊天室，检查网络连接状态。

## 📈 性能优化

### 数据库优化
- 合理的索引设计
- 连接池配置优化
- 查询语句优化

### 缓存策略
- Redis 缓存热点数据
- 会话信息缓存
- 静态资源缓存

### WebSocket 优化
- 连接数限制
- 心跳检测机制
- 自动断线重连

## 🔄 版本历史

### v1.0.0 (2025-07-26) - “好好说话”初始版
- ✅ 基础聊天功能实现
- ✅ 用户认证系统
- ✅ WebSocket 实时通信
- ✅ **AI内容分析系统**：实时监测消息内容
- ✅ **积分激励机制**：文明加分，越网扣分
- ✅ **情感识别**：智能分析消息情感倾向
- ✅ **文明等级系统**：根据积分划分用户等级
- ✅ **成就系统**：文明聊天成就加持

### 后续计划 - 持续优化“好好说话”体验

#### v1.1.0 - 智能升级版
- [ ] **更高级的AI分析**：支持多语言和方言识别
- [ ] **上下文理解**：结合对话历史的更准确判断
- [ ] **个性化建议**：根据用户行为提供定制改进建议
- [ ] **实时翻译**：支持多语言文明沟通

#### v1.2.0 - 社区化版
- [ ] **群聊功能**：支持多人聊天室的文明管理
- [ ] **社区管理**：引入管理员和社区规则
- [ ] **举报机制**：用户可以举报不当行为
- [ ] **文明大使系统**：高等级用户可以帮助新手

#### v1.3.0 - 教育引导版
- [ ] **文明教育模块**：提供网络文明学习资料
- [ ] **互动教学**：通过游戏化方式学习文明沟通
- [ ] **数据分析**：为用户提供沟通习惯分析报告
- [ ] **文明证书**：颁发数字文明证书

#### v2.0.0 - 平台化版
- [ ] **开放API**：为第三方应用提供AI分析服务
- [ ] **插件系统**：支持各种社交平台集成
- [ ] **移动端APP**：开发原生移动应用
- [ ] **数据可视化**：网络文明的数据分析与展示

#### 长期愿景
- [ ] **全球化部署**：在世界范围内推广文明沟通
- [ ] **教育机构合作**：与学校、机构合作开展文明教育
- [ ] **政策建议**：为网络治理提供数据支持和政策建议
- [ ] **社会影响力**：成为网络文明的标杆平台

## 🤝 贡献指南

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📜 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 📞 联系方式

- 项目维护者：lingview
- 邮箱：official@lingview.xyz
- 项目地址：https://github.com/lingview/tennis-voice-chat

---

> 如果这个项目对您有帮助，请给我们一个 ⭐️ Star！您的支持是我们持续改进的动力。