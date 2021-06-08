package com.holland.db.service.impl.oracle

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.FetchColumns

class OracleFetchColumnsImpl(private val dbController: DBController) : FetchColumns {

    override fun execute(tableName: String): List<ColumnTemplate> {
        val result = mutableListOf<ColumnTemplate>()

        val statement =
            dbController.connection.prepareStatement("select * from user_tab_columns t inner join user_col_comments c on t.COLUMN_NAME=c.COLUMN_NAME and c.TABLE_NAME=? where t.TABLE_NAME=? order by COLUMN_ID")
        statement.setString(1, tableName)
        statement.setString(2, tableName)
        statement.execute()
        val resultSet = statement.resultSet

        while (resultSet.next()) {
            val dataDefault = resultSet.getString("data_default")
            resultSet.run {
                ColumnTemplate(
                    getString("COLUMN_NAME"),
                    getString("data_type"),
                    OracleUtil.dbType2JavaType(getString("data_type")),
                    getLong("char_length"),
                    "Y" == getString("nullable"),
                    dataDefault,
                    getString("comments"),
                    false
                )
            }.also { result.add(it) }
        }

        getPk(result, dbController, tableName)

        try {
            resultSet.close()
        } finally {
            statement.close()
        }

        return result
    }

    private fun getPk(result: List<ColumnTemplate>, dbController: DBController, tableName: String) {
        val statement =
            dbController.connection.prepareStatement("select COLUMN_NAME from user_cons_columns a, user_constraints b where a.constraint_name = b.constraint_name and b.constraint_type = 'P' and a.table_name = ?")
        statement.setString(1, tableName)
        statement.execute()
        val resultSet = statement.resultSet

        while (resultSet.next()) {
            resultSet.run {
                result.first { it.columnName == getString("COLUMN_NAME") }.pk = true
            }
        }

        try {
            resultSet.close()
        } finally {
            statement.close()
        }
    }
}
