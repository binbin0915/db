package com.holland.db

enum class DataSource(val upperCamelCase: String, val lowerCase: String) {
    ORACLE("Oracle", "oracle"),
    MYSQL("Mysql", "mysql"),
    POSTGRE("Postgre", "postgre"),
    ;
}