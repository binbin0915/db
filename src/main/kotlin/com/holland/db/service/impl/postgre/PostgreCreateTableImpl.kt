package com.holland.db.service.impl.postgre

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.CreateTable
import com.holland.db.service.TableTemplate

class PostgreCreateTableImpl(private val dbController: DBController) : CreateTable {
    override fun execute(
        tableTemplate: TableTemplate,
        columns: List<ColumnTemplate>,
        incrementId: Boolean,
        db: String?
    ) {
//        val builder = StringBuilder()
//        cTableName(builder, tableTemplate, db!!)
//        cColumn(builder, columns, incrementId)
//        end(builder, tableTemplate)
//        val statement = dbController.connection.prepareStatement(builder.toString())
//        statement.execute()
//
//        try {
//            statement.close()
//        } finally {
//        }
    }

    private fun cTableName(builder: StringBuilder, tableTemplate: TableTemplate, db: String) {
        builder.append("create table $db.").append(tableTemplate.name).append("(")
    }

    private fun cColumn(builder: StringBuilder, columns: List<ColumnTemplate>, incrementId: Boolean) {
        val pks = arrayListOf<String>()
        if (incrementId) {
            pks.add("id")
            builder.appendLine("`id` bigint(0) NOT NULL AUTO_INCREMENT,")
        }
        columns.forEach {
            val dataAndType = getDataType(it)
            val nullable = if (it.nullable) "NULL" else "NOT NULL"
            val default = if (it.dataDefault!!.isEmpty()) "DEFAULT NULL" else "DEFAULT ${it.dataDefault}"
            builder.appendLine("`${it.columnName}` $dataAndType CHARACTER SET utf8 COLLATE utf8_general_ci $nullable $default COMMENT '${it.comments}',")
            if (it.pk) pks.add(it.columnName)
        }
        builder.appendLine("PRIMARY KEY ${pks.joinToString("`,`", "(`", "`)")} USING BTREE")
    }

    private fun end(builder: StringBuilder, template: TableTemplate) {
        builder.appendLine(")ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '").append(template.comment).append("'")
    }

    private fun getDataType(column: ColumnTemplate): String {
        return "${column.dbDataType}(${column.charLength})"
    }
}