package com.holland.db.service

import com.google.common.base.CaseFormat

interface FetchColumns {
    fun execute(tableName: String): List<ColumnTemplate>
}

@Suppress("MemberVisibilityCanBePrivate")
class ColumnTemplate(
    /**
     * UPPER_UNDERSCORE
     */
    var columnName: String,
    val dbDataType: String,
    val javaDataType: String,
    val charLength: Long,
    val nullable: Boolean,
    val dataDefault: String?,
    val comments: String?,
    var pk: Boolean,
) {
    var columnName_LOWER_CAMEL: String = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName)
    var columnName_UPPER_CAMEL: String = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, columnName)

    override fun toString(): String {
        return "ColumnTemplate(columnName='$columnName', columnName_LOWER_CAMEL='$columnName_LOWER_CAMEL', columnName_UPPER_CAMEL='$columnName_UPPER_CAMEL', dbDataType='$dbDataType', javaDataType='$javaDataType', charLength=$charLength, nullable=$nullable, dataDefault=$dataDefault, comments=$comments, pk=$pk)"
    }
}