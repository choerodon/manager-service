# manager-service

This service is the management center of the choerodon microservices framework. Its main functions include configuration management, route management, and swagger management.

## To get the code

```
git clone https://github.com/choerodon/mamager-service.git
```
## Installation and Getting Started

Create a manager-service database in mysql：

```sql
CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
CREATE DATABASE manager_service DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON manager_service.* TO choerodon@'%';
FLUSH PRIVILEGES;
```
Executed in the root directory of the manager-service project：

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
    * You can use the json, yaml, or properties text formats.
    * You can create or modify a configuration item for a version of a configuration.
    * After updating a configuration, the manager informs the config-server service and the corresponding service pulls the new configuration.
1. Route Management：
    * The initial route can be obtained by initializing the configuration of the api-gateway service.
    * You can create, edit, and edit routes.
    * After modifying the route, the manager will notify the config-server service and let the api-gateway service re-pull the route
1. swagger Management：
    * The list of services displayed by swagger is judged based on the route of the database and the instance obtained by the registration service.
    * Cache swagger information based on the version of the service to the database, get the database's swagger json, or request swagger json based on the type of service.
Find the information you want here
## Dependencies

* mysql
* kafka

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute
Pull requests are welcome! Follow this link for more information on how to contribute.

## Note
Manager-service is the configuration management center of the choerodon microservices framework, so its configuration profile can only use "default" by default.