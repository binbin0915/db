package com.holland.db

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.holland.db.service.*
import com.holland.util.FileUtil
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

@Suppress("unused", "JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
class DBController(val dataSource: String, host: String, port: String, user: String, pwd: String) {
    var schema: String? = null

    var connection: Connection
    private val classPrefix: String

    init {
        Class.forName(
            when (dataSource) {
                "ORACLE" -> {
                    classPrefix = "Oracle"
                    connection = DriverManager.getConnection("jdbc:oracle:thin:@${host}:${port}/orcl", user, pwd)
                    "oracle.jdbc.driver.OracleDriver"
                }
                "MYSQL" -> {
                    classPrefix = "Mysql"
                    connection =
                        DriverManager.getConnection(
                            "jdbc:mysql://${host}:${port}/mysql?useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&serverTimezone=Asia/Shanghai",
                            user,
                            pwd
                        )
                    "com.mysql.cj.jdbc.Driver"
                }
                else -> {
                    throw RuntimeException("not support [$dataSource]")
                }
            }
        )

        val path = "conf"
        val fileName = "db_connect.conf"
        FileUtil.mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        val dbConf = when (file.exists()) {
            true -> file.readLines()
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
                null
            }
        }

        FileUtil.newLine2File(JsonObject().let {
            it.addProperty("dataSource", dataSource)
            it.addProperty("host", host)
            it.addProperty("port", port)
            it.addProperty("user", user)
            it.addProperty("password", pwd)
            val json = Gson().toJson(it)
            if (dbConf?.contains(json) == true) null else json
        }, path, fileName)
    }

    /**
     * @param db:mysql 需要db
     */
    fun createTable(table: TableTemplate, columns: List<ColumnTemplate>, incrementId: Boolean, db: String?) {
        with(classPrefix) {
            Class.forName("com.holland.db.service.impl.${this.toLowerCase()}.${this}CreateTableImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .let {
                    it as CreateTable
                    it.execute(table, columns, incrementId, db)
                }
        }
    }

    fun fetchTables(): List<TableTemplate> {
        return with(classPrefix) {
            Class.forName("com.holland.db.service.impl.${this.toLowerCase()}.${this}FetchTablesImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .let {
                    it as FetchTables
                    it.execute()
                }
        }
    }

    fun fetchDbs(): List<String> {
        val result = mutableListOf<String>()
        val statement =
            connection.prepareStatement("SELECT SCHEMA_NAME AS `Database` FROM INFORMATION_SCHEMA.SCHEMATA;")
        statement.execute()
        val resultSet = statement.resultSet
        while (resultSet.next()) {
            result.add(resultSet.getString(1))
        }

        try {
            statement.resultSet.close()
        } finally {
            try {
                statement.close()
            } finally {
            }
        }

        return result
    }

    fun fetchColumns(tableName: String): List<ColumnTemplate> {
        return with(classPrefix) {
            Class.forName("com.holland.db.service.impl.${this.toLowerCase()}.${this}FetchColumnsImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .let {
                    it as FetchColumns
                    it.execute(tableName)
                }
        }
    }

    fun close() {
        if (connection.isClosed.not()) connection.close()
        println("connection is ${if (connection.isClosed.not()) "not" else ""}closed")
    }

    /**
     * heart connect
     */
    fun ping(): Boolean {
        return if (connection.isClosed) {
            false
        } else {
            val statement = connection.prepareStatement("select 1 from dual")
            statement.execute()
            statement.resultSet.let {
                it.next()
                it.getInt(1)
            } == 1
        }
    }

    fun generatePojo(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) =
        Generator.generatePojo(path, `package`, table, columns)

}
