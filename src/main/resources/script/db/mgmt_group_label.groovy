package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_group_label.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-21-mgmt-group-label') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'MGMT_GROUP_LABEL_S', startValue:"1")
        }
        createTable(tableName: "MGMT_GROUP_LABEL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MGMT_GROUP_LABEL')
            }
            column(name: 'GROUP_CODE', type: 'VARCHAR(32)', remarks: '组id') {
                constraints(nullable: false)
            }
            column(name: 'LABEL_VALUE', type: 'VARCHAR(32)', remarks: '标志') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: "MGMT_GROUP_LABEL", columnNames: "GROUP_CODE, LABEL_VALUE", constraintName: 'UK_MGMT_GROUP_LABEL_U1')
    }
}