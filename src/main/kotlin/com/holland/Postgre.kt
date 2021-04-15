package com.holland

import java.sql.DriverManager


fun main() {
    Postgre().execute()
}

class Postgre {
    fun execute() {
        Class.forName("org.postgresql.Driver")
        val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yb_acd", "yb_acd", "yb_acd")

        val statement = connection.prepareStatement("select * from schema_name.t1")
        statement.execute()
        val resultSet = statement.resultSet

        while (resultSet.next()) {
            resultSet.run {
            }
        }

        try {
            resultSet.close()
        } finally {
            statement.close()
        }
        if (connection.isClosed.not()) connection.close()
        println("connection is ${if (connection.isClosed.not()) "not" else ""}closed")
    }
}