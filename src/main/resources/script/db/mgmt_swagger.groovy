package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_swaggeragger.groovy') {

    changeSet(author: 'superleader8@gmail.com', id: '2018-03-09-mgmt_swagger') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'MGMT_SWAGGER_S', startValue:"1")
        }
        createTable(tableName: "MGMT_SWAGGER") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_MGMT_SWAGGER')
            }
            column(name: 'SERVICE_NAME', type: 'VARCHAR(128)', remarks: '服务名,如hap-user-service') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE_VERSION', type: 'VARCHAR(64)', defaultValue: "0.0.0", remarks: '服务版本') {
                constraints(nullable: false)
            }
            column(name: 'IS_DEFAULT', type: 'TINYINT(1)', defaultValue: '0', remarks: '是否为默认版本,0表示不是，1表示是') {
                constraints(nullable: false)
            }
            column(name: 'VALUE', type: 'MEDIUMTEXT', remarks: '接口文档json数据') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-11-07-add-index'){
        createIndex(indexName: 'IDX_MGMT_SWAGGER_I1', tableName:'MGMT_SWAGGER'){
            column(name: 'SERVICE_NAME', type: 'VARCHAR(128)')
            column(name: 'SERVICE_VERSION', type: 'VARCHAR(64)')
        }
    }
}
