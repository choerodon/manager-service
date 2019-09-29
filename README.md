# Manager Service
Choerodon Manager Service是猪齿鱼微服务框架的服务管理中心，主要功能包括配置管理、swagger管理、API统计和菜单统计。

## 服务配置

- `application.yml`

  ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
        username: choerodon
        password: 123456
      redis:
        host: localhost
        port: 6379
        # 使用和api-gateway同一个redis数据库，因为api调用统计存储在api-gateway,查询在manager-service
        database: 4
    mybatis:
      mapperLocations: classpath*:/mapper/*.xml
      configuration:
        mapUnderscoreToCamelCase: true
    eureka:
      instance:
        preferIpAddress: true
        leaseRenewalIntervalInSeconds: 1
        leaseExpirationDurationInSeconds: 3
      client:
        serviceUrl:
          defaultZone: ${EUREKA_DEFAULT_ZONE:http://localhost:8000/eureka/}
    choerodon:
      eureka:
        event:
          max-cache-size: 300
          retry-time: 5
          retry-interval: 3
          skip-services: config**, **register-server, **gateway**, zipkin**, hystrix**, oauth**
      swagger:
        client: client
        oauth-url: http://localhost:8080/oauth/oauth/authorize
      gateway:
        domain: 127.0.0.1:8080
        names: api-gateway, gateway-helper
      register:
        executetTime: 100
      profiles:
        active: sit
  ```

- `bootstrap.yml`

  ```yaml
    server:
      port: 8963
    spring:
      application:
        name: manager-service
      mvc:
        static-path-pattern: /**
      resources:
        static-locations: classpath:/static,classpath:/public,classpath:/resources,classpath:/META-INF/resources,file:/dist
    management:
      endpoint:
        health:
          show-details: ALWAYS
      server:
        port: 8964
      endpoints:
        web:
          exposure:
            include: '*'
    feign:
      hystrix:
        enabled: false
  ```
## 环境需求

- mysql 5.6+
- redis 3.0+
- 该项目是一个 Eureka Client 项目，启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤

- 运行 `eureka-server`，[代码库地址](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。

- 拉取当前项目到本地，执行如下命令：

  ```sh
   git clone https://code.choerodon.com.cn/choerodon-framework/manager-service.git
  ```

- 创建数据库，本地创建 `manager_service` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE manager_service DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON manager_service.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `asgard_service` 数据库，运行项目根目录下的 `init-local-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-local-database.sh
  ```
  
- 本地启动 redis-server

- 启动项目，项目根目录下执行如下命令：

  ```sh
   mvn spring-boot:run
  ```
  
## 更新日志

* [更新日志](./CHANGELOG.zh-CN.md)

## 如何参与

欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。