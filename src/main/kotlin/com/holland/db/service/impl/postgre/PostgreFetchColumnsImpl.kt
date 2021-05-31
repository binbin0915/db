package com.holland.db.service.impl.postgre

import com.holland.db.DBController
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.FetchColumns

class PostgreFetchColumnsImpl(private val dbController: DBController) : FetchColumns {

    override fun execute(tableName: String): List<ColumnTemplate> {
        val result = mutableListOf<ColumnTemplate>()
        var statement =
            dbController.connection.prepareStatement("SELECT col_description(a.attrelid,a.attnum) as comment,format_type(a.atttypid,a.atttypmod) as type,a.attname as name, a.attnotnull as notnull FROM pg_class as c,pg_attribute as a where c.relname = '${tableName}' and a.attrelid = c.oid and a.attnum>0")
        statement.execute()
        var resultSet = statement.resultSet

        while (resultSet.next()) {
            resultSet.run {
                result.add(
                    ColumnTemplate(
                        getString("column_name"),
                        getString("data_type"),
                        PostgreUtil.dbType2JavaType(getString("data_type")),
                        0,
                        "f" == getString("notnull"),
                        null,
                        getString("column_comment"),
                        false
                    )
                )
            }
        }

        //查主键
        statement =
            dbController.connection.prepareStatement(
                """select pg_attribute.attname as colname
                        from pg_constraint  inner join pg_class 
                        on pg_constraint.conrelid = pg_class.oid 
                        inner join pg_attribute on pg_attribute.attrelid = pg_class.oid 
                        and  pg_attribute.attnum = pg_constraint.conkey[1]
                        inner join pg_type on pg_type.oid = pg_attribute.atttypid
                        where pg_class.relname = '${tableName}' and pg_constraint.contype='p'"""
            )
        statement.execute()
        resultSet = statement.resultSet
        while (resultSet.next()) {
            resultSet.apply {
                result
                    .find { columnTemplate -> columnTemplate.columnName == getString("colname") }
                    ?.pk = true
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
