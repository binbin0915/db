package com.holland.db.service.impl.postgre

object PostgreUtil {

//    fun formatDbType(type: String): String {
//        when (type) {
//            "integer" -> {
//            }
//            else -> {
//                if (type.startsWith("character")) {
//
//                } else if (type.startsWith("timestamp")) {
//
//                } else {
//                    "java."
//                }
//            }
//        }
//    }

    fun dbType2JavaType(type: String): String {
        if (type.startsWith("character")) {
            return "String"
        }
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

        "integer" to "Long",
        "tinyint" to "Long",
        "int" to "Long",
        "bigint" to "Long",

        "double" to "BigDecimal",
        "float" to "BigDecimal",

        "boolean" to "Boolean",
    )
}
