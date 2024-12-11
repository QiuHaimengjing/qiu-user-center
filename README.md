# 用户中心

## 项目介绍
一个简单的初级且通用的用户中心模板，包含用户注册、登录、后台管理功能，适用于项目初始化时使用。
![](https://github.com/QiuHaimengjing/ImageStorage/blob/main/repositories/qiu-user-center/qiuusercenter01.png)
![](https://github.com/QiuHaimengjing/ImageStorage/blob/main/repositories/qiu-user-center/qiuusercenter02.png)

## 项目技术
### 前端
![Vue.js](https://img.shields.io/badge/Vue.js-3.3.10-4FC08D?logo=vue.js&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-4.5.1-646CFF?logo=vite&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-%3E%3D18-339933?logo=nodedotjs&logoColor=white)  
采用`Element Plus`作为UI框架，`axios`作为请求库，`vue-router`作为路由管理，`pinia`作为状态管理。使用 `Prettierrc` 作为代码格式化工具，`ESLint`作为代码检查工具。

### 后端
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.13-green?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)  
包含`MyBatis-Plus` + `Hutool`工具类库和`Knif4j`作为接口文档生成工具。

## 项目结构
```shell
qiu-user-center
├── backend -- 后端
└── frontend -- 前端
```

## 项目启动
### 后端
1. 打开 idea 根据`pom.xml`使用 Maven 安装依赖；
2. 创建数据库，执行 sql 文件夹下的 `user.sql` 文件创建表；
3. 修改`application.yml`中的数据库配置；
4. 启动`UsercenterApplication`类。

项目启动后接口文档地址：`http://localhost:8080/doc.html`

### 前端
进入`frontend`目录，使用 `pnpm` 安装依赖，执行`pnpm install`安装依赖，执行`pnpm run dev`启动项目。或者使用 `npm` 安装依赖，执行`npm install`安装依赖，执行`npm run dev`启动项目。

## 部署
后端修改`application-prod.yml`中的数据库配置，前端修改根目录`.env.production`文件中的接口前缀地址。
### 传统部署
后端使用`Maven`打包，前端使用`Vite`打包，打包后的文件放到服务器上即可。  
启动后端项目：
```shell
java -jar usercenter-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
前端部署，以 Nginx 为例，依据实际情况更改：
```nginx
server {
    listen 9002;
    server_name localhost;
    try_files $uri $uri/ /index.html; # history路由支持刷新后不报错
    index index.php index.html index.htm default.php default.htm default.html;

    location ^~ /api/ {
        rewrite ^/api/(.*)$ /$1 break;
        proxy_pass http://服务器地址:8080;
    }
}
```
### Docker 部署
注意：暂未进行过测试，仅供参考。  
后端和前端使用`Dockerfile`构建镜像。  
建议：后端构建镜像时 maven 打包很慢，建议修改 Dockerfile 文件，在本地打包好后再构建镜像，直接 COPY 本地打包好的 jar 包要快得多。前端的 Dockerfile 主要就是一个 COPY 打包好的`dist`目录，还有`nginx.conf`。

## 🌟 支持项目
欢迎提出问题和改进建议！  
如果你觉得这个项目对你有帮助，请点一个 Star ⭐️，这将对我非常有帮助！