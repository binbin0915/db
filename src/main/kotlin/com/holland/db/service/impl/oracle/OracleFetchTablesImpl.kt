package com.holland.db.service.impl.oracle

import com.holland.db.DBController
import com.holland.db.service.FetchTables
import com.holland.db.service.TableTemplate

@Suppress("unused", "SqlDialectInspection", "SqlNoDataSourceInspection")
class OracleFetchTablesImpl(private val dbController: DBController) : FetchTables {
    override fun execute(): List<TableTemplate> {
        val result = mutableListOf<TableTemplate>()
        val statement = dbController.connection.prepareStatement("select * from user_tab_comments")
        statement.execute()
        statement.resultSet.apply {
            while (next()) {
                result.add(
                    TableTemplate(
                        getString("table_name"),
                        getString("table_type"),
                        getString("comments")
                    )
                )
            }
        }

        try {
            statement.resultSet.close()
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