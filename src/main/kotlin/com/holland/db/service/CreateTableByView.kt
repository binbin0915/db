package com.holland.db.service


/**
 * 通过视图生成创建表的sql语句
 */
interface CreateTableByView {
    fun execute(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>)
}
