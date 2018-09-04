package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_routeroute.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-21-mgmt_route') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'MGMT_ROUTE_S', startValue:"1")
        }
        createTable(tableName: "MGMT_ROUTE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_MGMT_ROUTE')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '服务id，zuulRoute的标识，对应zuulRoute的id字段') {
                constraints(unique: true, uniqueConstraintName: 'UK_MGMT_ROUTE_U1')
                constraints(nullable: false)
            }
            column(name: 'PATH', type: 'VARCHAR(128)', remarks: '服务路径') {
                constraints(unique: true, uniqueConstraintName: 'UK_MGMT_ROUTE_U2')
                constraints(nullable: false)
            }
            column(name: "SERVICE_ID", type: "VARCHAR(128)", remarks: '服务名') {
                constraints(nullable: false)
            }
            column(name: 'URL', type: 'VARCHAR(255)', remarks: '物理路径')
            column(name: 'STRIP_PREFIX', type: 'INT UNSIGNED', remarks: '是否去前缀')
            column(name: 'RETRYABLE', type: 'INT UNSIGNED', remarks: '是否支持路由重试')
            column(name: 'CUSTOM_SENSITIVE_HEADERS', type: 'INT UNSIGNED', remarks: '是否定义敏感头部')
            column(name: 'SENSITIVE_HEADERS', type: 'TEXT', remarks: '敏感头部列表')
            column(name: 'HELPER_SERVICE', type: 'VARCHAR(64)', remarks: '配置经过的gateway helper服务名')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'jcalaz@163.com', id: '2018-05-24-add_column_is_built_in') {
        addColumn(tableName: 'MGMT_ROUTE') {
            column(name: 'IS_BUILT_IN', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否为内置服务。1表示是，0表示不是') {
                constraints(nullable: false)
            }
        }
    }

}