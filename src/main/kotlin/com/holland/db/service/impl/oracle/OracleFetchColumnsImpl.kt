package com.holland.db.service.impl.oracle

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.FetchColumns

class OracleFetchColumnsImpl(private val dbController: DBController) : FetchColumns {

    private fun dataTypeConvert(dataType: String): String {
        return when (dataType) {
            "CHAR", "VARCHAR2", "NVARCHAR2" -> "String"
            "DATE" -> "Date"
            "NUMBER" -> "Long"
            "CLOB" -> "Object"
            else -> "Object"
        }
    }

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
                    dataTypeConvert(getString("data_type")),
                    getLong("char_length"),
                    "Y" == getString("nullable"),
                    dataDefault,
                    getString("comments"),
                    false
                )
            }.also { result.add(it) }
        }

        try {
            resultSet.close()
        } finally {
            statement.close()
        }

        return result
    }
}
