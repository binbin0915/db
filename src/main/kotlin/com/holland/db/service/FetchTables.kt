package com.holland.db.service

interface FetchTables {
    fun execute(): List<TableTemplate>
}

class TableTemplate(
    val name: String,
    val type: String?,
    val comment: String?
) {
    override fun toString(): String {
        return "TableTemplate(name='$name', type='$type', comment=$comment)"
    }
}