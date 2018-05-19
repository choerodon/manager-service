package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_routeroute.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-21-mgmt_route') {
        createTable(tableName: "mgmt_route") {
            column(name: 'id', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '服务id，zuulRoute的标识，对应zuulRoute的id字段') {
                constraints(unique: true)
                constraints(nullable: false)
            }
            column(name: 'path', type: 'VARCHAR(128)', remarks: '服务路径') {
                constraints(unique: true)
                constraints(nullable: false)
            }
            column(name: "service_id", type: "VARCHAR(128)", remarks: '服务名') {
                constraints(nullable: false)
            }
            column(name: 'url', type: 'VARCHAR(255)', remarks: '物理路径')
            column(name: 'strip_prefix', type: 'INT UNSIGNED', remarks: '是否去前缀')
            column(name: 'retryable', type: 'INT UNSIGNED', remarks: '是否支持路由重试')
            column(name: 'custom_sensitive_headers', type: 'INT UNSIGNED', remarks: '是否定义敏感头部')
            column(name: 'sensitive_headers', type: 'TEXT', remarks: '敏感头部列表')
            column(name: 'helper_service', type: 'VARCHAR(64)', remarks: '配置经过的gateway heler服务名')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}