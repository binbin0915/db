package com.holland.db.service

interface FetchColumns {
    fun execute(tableName: String): List<ColumnTemplate>
}

@Suppress("MemberVisibilityCanBePrivate")
class ColumnTemplate(
    var columnName: String,
    val dbDataType: String,
    val javaDataType: String,
    val charLength: Long,
    val nullable: Boolean,
    val dataDefault: String?,
    val comments: String?,
    var pk: Boolean,
) {
    override fun toString(): String {
        return "ColumnTemplate(columnName='$columnName', dataType='$dbDataType', charLength=$charLength, nullable=$nullable, dataDefault=$dataDefault, comments=$comments, pk=$pk)"
    }
}