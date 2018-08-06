package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_swaggeragger.groovy') {

    changeSet(author: 'superleader8@gmail.com', id: '2018-03-09-mgmt_swagger') {
        createTable(tableName: "mgmt_swagger") {
            column(name: 'id', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'service_name', type: 'VARCHAR(128)', remarks: '服务名,如hap-user-service') {
                constraints(nullable: false)
            }
            column(name: 'service_version', type: 'VARCHAR(64)', defaultValue: "0.0.0", remarks: '服务版本') {
                constraints(nullable: false)
            }
            column(name: 'is_default', type: 'TINYINT(1)', defaultValue: '0', remarks: '是否为默认版本,0表示不是，1表示是') {
                constraints(nullable: false)
            }
            column(name: 'value', type: 'MEDIUMTEXT', remarks: '接口文档json数据') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}
