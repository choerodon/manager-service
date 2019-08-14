# Manager Service
本服务是猪齿鱼微服务框架的服务管理中心，主要功能包括配置管理，路由管理，和swagger管理


## 依赖

- mysql: 5.6+
- redis: 3.0+

## 服务配置

- application.yml配置
```
spring:
  datasource:
    url: jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
    username: choerodon
    password: 123456
  redis:
    host: localhost
    port: 6379
    #使用和gateway-helper同一个redis数据库，存储在gateway-helper,查询在manager-service
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
- bootstrap.yml配置
```
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

## 安装和运行
* 在mysql数据库中创建`manager_service`数据库,并在该数据库执行下面的SQL语句来创建choerodon用户，并赋予权限。  
```sql 
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456"; 
CREATE DATABASE manager_service DEFAULT CHARACTER SET utf8; 
GRANT ALL PRIVILEGES ON manager_service.* TO choerodon@'%'; 
FLUSH PRIVILEGES;
```   
* 在`manager_service`项目的根文件目录下创建`init-local-database.sh` 脚本,并写入下面的代码。
```sh
#!/usr/bin/env bash
MAVEN_LOCAL_REPO=$(cd / && mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
TOOL_GROUP_ID=io.choerodon
TOOL_ARTIFACT_ID=choerodon-tool-liquibase
TOOL_VERSION=0.11.0.RELEASE
TOOL_JAR_PATH=${MAVEN_LOCAL_REPO}/${TOOL_GROUP_ID/\./\/}/${TOOL_ARTIFACT_ID}/${TOOL_VERSION}/${TOOL_ARTIFACT_ID}-${TOOL_VERSION}.jar
mvn org.apache.maven.plugins:maven-dependency-plugin:get \
 -Dartifact=${TOOL_GROUP_ID}:${TOOL_ARTIFACT_ID}:${TOOL_VERSION} \
 -Dtransitive=false

java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true" \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -jar ${TOOL_JAR_PATH}
```
* 在`manager_service`根文件目录下执行上述创建的`init-local-database.sh`文件
```sh
sh init-local-database.sh
```
* 运行`manager_service`程序
```sh
mvn spring-boot:run
```

## 链接
* [修改日志](./CHANGELOG.zh-CN.md)

## 用法
1. 对于`Configuration management`: 
 * 该服务可以提供配置的new，update和delete操作
 * 可以使用json，yml或properties的文件格式
 * 可以为一个配置版本创建或者修改配置项
 * 在更新配置后，可以通知`config-server`服务，从而让与之对应的服务拉去新的配置信息
2. 对于`Route Management`
 * 可以通过初始化`api-gateway`服务的配置信息来获取初始路由
 * 可以创建和编辑路由
 * 在修改路由后，可以通知`config-server`服务并让`api-gateway`服务重新拉出路由

## 如何提交修改

如果你也想参与这个项目的开发和修改， [点击](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) 去了解如何参与