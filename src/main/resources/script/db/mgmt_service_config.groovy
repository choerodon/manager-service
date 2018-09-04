package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_service_configonfig.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-09-service-config') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'MGMT_SERVICE_CONFIG_S', startValue:"1")
        }
        createTable(tableName: 'MGMT_SERVICE_CONFIG') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MGMT_SERVICE_CONFIG')
            }
            column(name: "NAME", type: 'VARCHAR(64)', remarks: '配置名，对应前端的配置id') {
                constraints(nullable: false)
            }
            column(name: 'CONFIG_VERSION', type: 'VARCHAR(128)', remarks: '配置版本') {
                constraints(nullable: false)
            }
            column(name: 'IS_DEFAULT', type: "TINYINT(1)", defaultValue: '0', remarks: '是否为默认版本,0表示不是，1表示是') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE_ID', type: 'BIGINT UNSIGNED', remarks: '配置所属服务Id') {
                constraints(nullable: false)
            }
            column(name: 'VALUE', type: "LONGTEXT", remarks: '配置集合') {
                constraints(nullable: false)
            }
            column(name: 'SOURCE', type: 'VARCHAR(64)', remarks: '配置来源，工具生成或者页面生成')
            column(name: 'PUBLIC_TIME', type: 'DATETIME', remarks: '配置发布时间') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'MGMT_SERVICE_CONFIG', columnNames: 'SERVICE_ID,CONFIG_VERSION', constraintName: 'UK_MGMT_SERVICE_CONFIG_U1')
    }

    changeSet(author: 'jcalaz@163.com', id: '2018-06-11-alter-nullable') {
        dropNotNullConstraint(tableName: 'MGMT_SERVICE_CONFIG', columnName: 'CONFIG_VERSION', columnDataType: 'VARCHAR(128)')
        addNotNullConstraint(tableName: 'MGMT_SERVICE_CONFIG', columnName: 'SOURCE', columnDataType: 'VARCHAR(64)')
    }

}