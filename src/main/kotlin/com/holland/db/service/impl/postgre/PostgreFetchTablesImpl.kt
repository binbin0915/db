package com.holland.db.service.impl.postgre

import com.holland.db.DBController
import com.holland.db.service.FetchTables
import com.holland.db.service.TableTemplate

@Suppress("unused", "SqlDialectInspection", "SqlNoDataSourceInspection")
class PostgreFetchTablesImpl(private val dbController: DBController) : FetchTables {
    override fun execute(): List<TableTemplate> {
        val result = mutableListOf<TableTemplate>()
        val statement =
            dbController.connection.prepareStatement("select relkind,relname as table_name,cast(obj_description(relfilenode,'pg_class') as varchar) as table_comment from pg_class c where relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname")
        statement.execute()
        statement.resultSet.apply {
            while (next()) {
                val type = getString("relkind")
                result.add(
                    TableTemplate(
                        getString("table_name"),
                        if ("v" == type) "VIEW" else "TABLE",
                        getString("table_comment")
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