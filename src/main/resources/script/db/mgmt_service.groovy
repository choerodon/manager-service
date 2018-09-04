package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_servicervice.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-09-mgmt_service') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'MGMT_SERVICE_S', startValue:"1")
        }
        createTable(tableName: 'MGMT_SERVICE') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MGMT_SERVICE')
            }
            column(name: 'NAME', type: 'VARCHAR(128)', remarks: '服务名') {
                constraints(nullable: false, uniqueConstraintName: 'UK_MGMT_SERVICE_U1')
                constraints(unique: true)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}