package com.holland.db.service

interface FetchColumns {
    fun execute(tableName: String): List<ColumnTemplate>
}

@Suppress("MemberVisibilityCanBePrivate")
class ColumnTemplate(
    var columnName: String,
    val dataType: String,
    val charLength: Long,
    val nullable: Boolean,
    val dataDefault: String?,
    val comments: String?,
    val pk: Boolean,
) {
    override fun toString(): String {
        return "ColumnTemplate(columnName='$columnName', dataType='$dataType', charLength=$charLength, nullable=$nullable, dataDefault=$dataDefault, comments=$comments, pk=$pk)"
    }
}