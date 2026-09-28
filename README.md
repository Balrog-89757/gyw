# 🎵 Music-Box 在线智能音乐盒

基于 **Spring Boot 3 + MyBatis-Plus + Thymeleaf + Layui** 的在线音乐播放与管理系统，包含用户端（浏览、搜索、播放、收藏、评论）与管理端（音乐 / 用户 / 评论 / 热门推荐管理）两套界面。

## 功能特性

### 用户端

- **首页**：热门推荐（`is_hot = 1` 按播放量取前 6 条）、全部音乐分页浏览（每页 8 条）、按歌名 / 歌手关键字搜索
- **用户模块**：注册、登录、退出（Session 保存登录状态）
- **音乐详情页**：在线播放、播放量自增、评论列表
- **收藏**：添加 / 取消收藏、我的收藏列表、收藏内搜索
- **评论**：发表评论、我的评论、删除评论
- **个人中心**：查看 / 修改个人资料、修改密码

### 管理端

- **音乐管理**：分页列表、关键字查询、新增、修改、删除，支持音频与封面文件上传（`MultipartFile`，UUID 重命名）
- **用户管理**：用户列表、新增、编辑、禁用 / 启用
- **评论管理**、**热门推荐管理**

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3.0.2、Spring MVC |
| 持久层 | MyBatis-Plus 3.5.5（`BaseMapper` + `LambdaQueryWrapper` + 分页插件） |
| 数据库 | MySQL 8.0（`mysql-connector-java` 8.0.28） |
| 模板引擎 | Thymeleaf 3 |
| 前端 | HTML / CSS / JavaScript、Layui、jQuery、ECharts |
| 工具类库 | Lombok、commons-lang3、SLF4J、spring-security-crypto |
| 构建 | Maven |

## 项目结构

```
music-box
├── pom.xml
├── sql/
│   └── music_box.sql                 # 建库建表 + 测试数据
└── src/main
    ├── java/com/baidu
    │   ├── MusicBoxApplication.java  # 启动类（@MapperScan("com.baidu.dao")）
    │   ├── config/                   # MyBatis-Plus 分页插件配置
    │   ├── controller/               # Index / Main / User / Music / Comment / Favorite / HotRecommend
    │   ├── dao/                      # Mapper 接口
    │   ├── pojo/                     # 实体：User / Music / Comment / Favorite / HotRecommend
    │   ├── service/ (+ impl)         # 业务层
    │   └── utils/                    # R（统一响应）、PageResult（Layui 表格分页）、SysMenu、TransFerPlace
    └── resources
        ├── application.yml           # 数据源 / MyBatis-Plus / Thymeleaf 配置
        ├── templates/                # Thymeleaf 页面
        └── static/                   # layuiadmin、css、js、img 等静态资源
```

## 快速开始

### 1. 环境要求

| 依赖 | 版本 |
| --- | --- |
| JDK | 17 及以上（Spring Boot 3.x 的最低要求） |
| Maven | 3.6 及以上 |
| MySQL | 8.0 及以上 |

### 2. 初始化数据库

```bash
mysql -uroot -p < sql/music_box.sql
```

脚本会创建 `music_box` 数据库（utf8mb4）以及 `user`、`music`、`favorite`、`comment`、`hot_recommend`、`operation_log` 六张表，并写入测试数据。

内置测试账号：用户名 `user`，密码 `123`。

### 3. 配置数据源

`src/main/resources/application.yml` 中的数据源信息通过环境变量注入，未设置时使用默认值：

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `MYSQL_HOST` | `localhost` | 数据库地址 |
| `MYSQL_PORT` | `3306` | 数据库端口 |
| `MYSQL_DATABASE` | `music_box` | 数据库名 |
| `MYSQL_USERNAME` | `root` | 数据库用户名 |
| `MYSQL_PASSWORD` | `change-me` | 数据库密码（**请务必改为自己的密码**） |

例如 Windows PowerShell：

```powershell
$env:MYSQL_PASSWORD="你的密码"
mvn spring-boot:run
```

也可以直接修改 `application.yml` 中 `${...}` 冒号后面的默认值。

### 4. 启动项目

```bash
# 开发模式运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/music-box-1.0-SNAPSHOT.jar
```

启动成功后访问：<http://localhost:8080/>

## 主要接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/` | 首页（热门推荐 + 分页 + 搜索） |
| POST | `/user/login` | 登录 |
| POST | `/user/register` | 注册 |
| POST | `/user/logout` | 退出登录 |
| GET | `/user/profile` | 个人资料 |
| GET | `/music/detail/{id}` | 音乐详情页（播放量 +1） |
| GET | `/music/admin/list` | 音乐分页列表 |
| POST | `/music/admin/add` | 新增音乐（文件上传） |
| PUT | `/music/admin/update` | 修改音乐 |
| DELETE | `/music/admin/delete/{id}` | 删除音乐 |
| POST | `/favorite/add` | 收藏 / 取消收藏 |
| GET | `/favorite/list` | 我的收藏 |
| POST | `/comment/add` | 发表评论 |
| GET | `/hotRecommend/admin/list` | 热门推荐列表 |

## 说明与注意事项

- **密码安全**：当前登录校验为明文比较，若要用于生产环境，请改为 BCrypt 加密（项目已引入 `spring-security-crypto`）。
- **文件上传**：管理员上传的音频与封面保存在运行时目录 `target/classes/static/{music,covers}/`，执行 `mvn clean` 后会丢失；如需持久化，请改为独立的上传目录并配置静态资源映射。
- 仓库不包含任何音频文件，测试音频请自行准备。
- `src/main/resources/static/layuiadmin`、`jquery`、`echarts` 等为第三方前端库，版权归各自作者所有。

## 许可证

本项目基于 [MIT License](LICENSE) 开源。
