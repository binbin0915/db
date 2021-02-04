package com.holland.db.service.impl.oracle

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.CreateTable
import com.holland.db.service.TableTemplate

class OracleCreateTableImpl(private val dbController: DBController) : CreateTable {
    override fun execute(
        tableTemplate: TableTemplate,
        columns: List<ColumnTemplate>,
        incrementId: Boolean,
        db: String?
    ) {
        val property = arrayListOf<String>()
        val comment = StringBuilder()
        columns.forEach {
            property.add("${it.columnName} ${getDataType(it)} ${if (it.nullable) "" else "not null"}")
            comment.appendLine("/\ncomment on column ${tableTemplate.name}.${it.columnName} is '${it.comments}'")
        }

        val builder = StringBuilder().apply {
            appendLine("create table ${tableTemplate.name}(")
            appendLine(property.joinToString(",\n"))
            appendLine(")\n/\ncomment on table ${tableTemplate.name} is '${tableTemplate.comment}'")
            appendLine(comment)
            if (incrementId) {
                appendLine("/\ncreate sequence SEQ_${tableTemplate.name}\nnocache")
            }
            appendLine("/\ncommit;")
        }

        val statement = dbController.connection.prepareStatement(builder.toString())
        println(builder.toString())
        statement.execute()

        try {
            statement.close()
        } finally {
        }
    }

    private fun getDataType(column: ColumnTemplate): String {
        return "${column.dbDataType}(${column.charLength})"
    }
}