# Choerodon Manager Service
Choerodon Manager Service是猪齿鱼微服务框架的服务管理中心，主要功能包括配置管理，路由管理，和swagger管理

## Introduction

## Add Helm chart repository

``` bash    
helm repo add choerodon https://openchart.choerodon.com.cn/choerodon/c7n
helm repo update
```

## Installing the Chart

```bash
$ helm install c7n/manager-service --name manager-service
```

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Uninstalling the Chart

```bash
$ helm delete manager-service
```

## Configuration

Parameter | Description	| Default
--- |  ---  |  ---  
`replicaCount` | Replicas count | `1`
`preJob.timeout` | job超时时间 | `300`
`preJob.preInitDB.enabled` | 是否初始化数据库 | `true`
`preJob.preInitDB.datasource.url` | 初始化数据库连接地址 | `jdbc:mysql://localhost:3306/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true`
`preJob.preInitDB.datasource.username` | 初始化数据库用户名 | `choerodon`
`preJob.preInitDB.datasource.password` | 初始化数据库用户密码 | `password`
`deployment.managementPort` | 服务管理端口 | `8964`
`env.open.SPRING_DATASOURCE_URL` | 数据库连接地址 | ` jdbc:mysql://127.0.0.1/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true`
`env.open.SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `choerodon`
`env.open.SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `password`
`env.open.SPRING_REDIS_HOST` | redis主机地址 | `localhost`
`env.open.SPRING_REDIS_PORT` | redis端口 | `6379`
`env.open.SPRING_REDIS_DATABASE` | redis db | `4`
`env.open.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | 注册服务地址 | `http://register-server:8000/eureka/`
`env.open.CHOERODON_SWAGGER_OAUTH_URL` |  swagger认证服务地址，应该为`网关/oauth/oauth/authorize` | ``
`env.open.CHOERODON_GATEWAY_DOMAIN` | 网关服务地址 | `127.0.0.1:8080`
`service.port` | service端口 | `8963`
`metrics.path` | 收集应用的指标数据路径 | ``
`metrics.group` | 性能指标应用分组 | `spring-boot`
`logs.parser` | 日志收集格式 | `spring-boot`
`resources.limits` | k8s中容器能使用资源的资源最大值 | `1.5Gi`
`resources.requests` | k8s中容器使用的最小资源需求 | `1Gi`

### SkyWalking Configuration
Parameter | Description
--- |  --- 
`javaagent` | SkyWalking 代理jar包(添加则开启 SkyWalking，删除则关闭)
`skywalking.agent.application_code` | SkyWalking 应用名称
`skywalking.agent.sample_n_per_3_secs` | SkyWalking 采样率配置
`skywalking.agent.namespace` | SkyWalking 跨进程链路中的header配置
`skywalking.agent.authentication` | SkyWalking 认证token配置
`skywalking.agent.span_limit_per_segment` | SkyWalking 每segment中的最大span数配置
`skywalking.agent.ignore_suffix` | SkyWalking 需要忽略的调用配置
`skywalking.agent.is_open_debugging_class` | SkyWalking 是否保存增强后的字节码文件
`skywalking.collector.backend_service` | SkyWalking OAP 服务地址和端口配置

```bash
$ helm install c7n/api-gateway \
    --set env.open.SKYWALKING_OPTS="-javaagent:/agent/skywalking-agent.jar -Dskywalking.agent.application_code=manager-service  -Dskywalking.agent.sample_n_per_3_secs=-1 -Dskywalking.collector.backend_service=oap.skywalking:11800" \
    --name manager-service
```

## 验证部署
```bash
curl -s $(kubectl get po -n c7n-system -l choerodon.io/release=manager-service -o jsonpath="{.items[0].status.podIP}"):8964/actuator/health | jq -r .status
```
出现以下类似信息即为成功部署

```bash
UP
```
