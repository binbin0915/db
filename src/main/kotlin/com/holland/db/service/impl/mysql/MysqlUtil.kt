package com.holland.db.service.impl.mysql

object MysqlUtil {

    fun dbType2JavaType(type: String): String {
        return dbType2JavaType[type] ?: "Object"
    }

    fun javaType2DbType(type: String): String {
        return dbType2JavaType.values.first { it == type }
    }

    val dbType2JavaType = mapOf(
        "varchar" to "String",
        "char" to "String",
        "text" to "String",
        "mediumtext" to "String",
        "longtext" to "String",
        "String" to "String",

        "datetime" to "Date",
        "timestamp" to "Date",
        "date" to "Date",

        "tinyint" to "Long",
        "int" to "Long",
        "bigint" to "Long",

        "double" to "BigDecimal",
        "float" to "BigDecimal",
    )
}
