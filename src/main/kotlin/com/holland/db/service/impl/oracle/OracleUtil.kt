package com.holland.db.service.impl.oracle

object OracleUtil {

    fun dbType2JavaType(type: String): String {
        return dbType2JavaType[type] ?: "Object"
    }

    fun javaType2DbType(type: String): String {
        return dbType2JavaType.values.first { it == type }
    }

    val dbType2JavaType = mapOf(
        "CHAR" to "String",
        "VARCHAR2" to "String",
        "NVARCHAR2" to "String",

        "DATE" to "Date",

        "NUMBER" to "Long",

        "BLOB" to "Object",
        "CLOB" to "Object",
    )
}