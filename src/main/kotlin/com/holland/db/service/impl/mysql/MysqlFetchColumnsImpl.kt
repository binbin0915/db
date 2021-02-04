package com.holland.db.service.impl.mysql

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.FetchColumns

class MysqlFetchColumnsImpl(private val dbController: DBController) : FetchColumns {

    override fun execute(tableName: String): List<ColumnTemplate> {
        val result = mutableListOf<ColumnTemplate>()
        val statement =
            dbController.connection.prepareStatement("select column_name,is_nullable,data_type,character_maximum_length,column_comment,column_key from information_schema.columns where table_schema=? and table_name=?")
        statement.setString(1, dbController.schema)
        statement.setString(2, tableName)
        statement.execute()
        val resultSet = statement.resultSet

        while (resultSet.next()) {
            resultSet.run {
                result.add(
                    ColumnTemplate(
                        getString("column_name"),
                        getString("data_type"),
                        MysqlUtil.dbType2JavaType(getString("data_type")),
                        getLong("character_maximum_length"),
                        "YES" == getString("is_nullable"),
                        null,
                        getString("column_comment"),
                        "PRI" == getString("column_key")
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
