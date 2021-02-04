package com.holland.db.service.impl.oracle

object OracleUtil {

    fun dbType2JavaType(type: String): String {
        return dbType2JavaType[type] ?: "Object"
    }

    fun javaType2DbType(type: String): String {
        return dbType2JavaType.values.first { it == type }
    }

    val dbType2JavaType = mapOf(
        Pair("CHAR", "String"),
        Pair("VARCHAR2", "String"),
        Pair("NVARCHAR2", "String"),

        Pair("DATE", "Date"),

        Pair("NUMBER", "Long"),

        Pair("BLOB", "Object"),
        Pair("CLOB", "Object"),
    )
}