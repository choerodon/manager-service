package script.db

databaseChangeLog(logicalFilePath: 'script/db/mgmt_group_label.groovy') {
    changeSet(author: 'superleader8@gmail.com', id: '2018-03-21-mgmt-group-label') {
        createTable(tableName: "mgmt_group_label") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'group_code', type: 'VARCHAR(32)', remarks: '组id') {
                constraints(nullable: false)
            }
            column(name: 'label_value', type: 'VARCHAR(32)', remarks: '标志') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: "mgmt_group_label", columnNames: "group_code, label_value")
    }
}