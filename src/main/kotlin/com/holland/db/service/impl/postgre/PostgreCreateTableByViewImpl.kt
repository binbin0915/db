package com.holland.db.service.impl.postgre

import com.holland.db.service.ColumnTemplate
import com.holland.db.service.CreateTableByView
import com.holland.db.service.TableTemplate

class PostgreCreateTableByViewImpl : CreateTableByView {
    override fun execute(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>): String {
        TODO("Not yet implemented")
    }
}