package com.holland.db.service.impl.mysql

object MysqlUtil {

    fun dbType2JavaType(type: String): String {
        return dbType2JavaType[type] ?: "Object"
    }

    fun javaType2DbType(type: String): String {
        return dbType2JavaType.values.first { it == type }
    }

    val dbType2JavaType = mapOf(
        Pair("varchar", "String"),
        Pair("char", "String"),
        Pair("text", "String"),
        Pair("mediumtext", "String"),
        Pair("longtext", "String"),
        Pair("String", "String"),

        Pair("datetime", "Date"),
        Pair("timestamp", "Date"),
        Pair("date", "Date"),

        Pair("tinyint", "Long"),
        Pair("int", "Long"),
        Pair("bigint", "Long"),

        Pair("double", "BigDecimal"),
        Pair("float", "BigDecimal"),
    )
}
