# manager-service

This service is the management center of the choerodon micro services framework. Its main functions include configuration management, route management, and swagger management.

## Installation and Getting Started

Create a `manager_service` database in mysql：

```sql
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
CREATE DATABASE manager_service DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON manager_service.* TO choerodon@'%';
FLUSH PRIVILEGES;
```
New file of "init-local-database.sh" in the root directory of the `manager-service` project：

```sh
mkdir -p target
if [ ! -f target/choerodon-tool-liquibase.jar ]
then
    curl http://nexus.choerodon.com.cn/repository/choerodon-release/io/choerodon/choerodon-tool-liquibase/0.5.0.RELEASE/choerodon-tool-liquibase-0.5.0.RELEASE.jar -o target/choerodon-tool-liquibase.jar
fi
java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false" \
 -Dspring.datasource.username=choerodon \
 -Dspring.datasource.password=123456 \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -jar target/choerodon-tool-liquibase.jar
```

And executed in the root directory of the `manager-service` project：

```sh
sh init-local-database.sh
```
Then run the project in the root directory of the project：

```sh
mvn spring-boot:run
```

## Usage
1. Configuration management：
    * Manager provides configuration of new, update, and delete operations.
    * You can use the `json`, `yaml`, or `properties` text formats.
    * You can create or modify a configuration item for a version of a configuration.
    * After updating a configuration, the manager informs the `config-server` service and the corresponding service pulls the new configuration.
1. Route Management：
    * The initial route can be obtained by initializing the configuration of the `api-gateway` service.
    * You can create, edit, and edit routes.
    * After modifying the route, the manager will notify the `config-server` service and let the `api-gateway` service re-pull the route
1. swagger Management：
    * The list of services displayed by swagger is judged based on the route of the database and the instance obtained by the registration service.
    * Cache swagger information based on the version of the service to the database, get the database's swagger json, or request swagger json based on the type of service.


## Dependencies

* mysql
* kafka

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).
    
## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.

## Note
`Manager-service` is the configuration management center of the choerodon micro services framework, so its configuration profile can only use "default" by default.