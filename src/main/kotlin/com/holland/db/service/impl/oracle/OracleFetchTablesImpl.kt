package com.holland.db.service.impl.oracle

import com.google.common.base.CaseFormat.LOWER_CAMEL
import com.google.common.base.CaseFormat.UPPER_UNDERSCORE
import com.holland.db.DBController
import com.holland.db.service.FetchTables
import com.holland.db.service.TableTemplate
import com.holland.util.DbUtil

@Suppress("unused", "SqlDialectInspection", "SqlNoDataSourceInspection")
class OracleFetchTablesImpl(private val dbController: DBController) : FetchTables {
    override fun execute(): List<TableTemplate> {
        val statement =
            dbController.connection.prepareStatement("select table_name name, table_type type, comments \"comment\" from user_tab_comments")
        statement.execute()
        val resultSet = statement.resultSet

        val result = DbUtil.getResult(resultSet, UPPER_UNDERSCORE, LOWER_CAMEL, TableTemplate::class.java)

        try {
            resultSet.close()
        } finally {
            try {
                statement.close()
            } finally {
//                dbController.connection.close()
            }
        }
        return result
    }
}