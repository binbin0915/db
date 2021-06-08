package com.holland.db

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.holland.db.DataSource.*
import com.holland.db.service.*
import com.holland.util.FileUtil
import com.holland.util.TimeUtil
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

@Suppress("unused", "JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
class DBController(
    val dataSource: DataSource,
    private val host: String,
    private val port: String,
    private val user: String,
    private val pwd: String,
    private val database: String
) {
    var schema: String? = null

    var connection: Connection

    init {
        when (dataSource) {
            ORACLE -> {
                Class.forName("oracle.jdbc.driver.OracleDriver")
                connection = DriverManager.getConnection("jdbc:oracle:thin:@${host}:${port}/orcl", user, pwd)
            }
            MYSQL -> {
                Class.forName("com.mysql.cj.jdbc.Driver")
                connection =
                    DriverManager.getConnection(
                        "jdbc:mysql://${host}:${port}/mysql?useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&serverTimezone=Asia/Shanghai",
                        user,
                        pwd
                    )
            }
            POSTGRE -> {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection("jdbc:postgresql://${host}:${port}/${database}", user, pwd)
            }
        }

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

        FileUtil.append2File(JsonObject().let {
            it.addProperty("dataSource", dataSource.name)
            it.addProperty("host", host)
            it.addProperty("port", port)
            it.addProperty("user", user)
            it.addProperty("password", pwd)
            it.addProperty("database", database)
            val json = Gson().toJson(it)
            if (dbConf?.contains(json) == true) null else json
        }, path, fileName)
    }

    /**
     * @param db:mysql 需要db
     */
    fun createTable(table: TableTemplate, columns: List<ColumnTemplate>, incrementId: Boolean, db: String?) {
        Class.forName("com.holland.db.service.impl.${dataSource.lowerCase}.${dataSource.upperCamelCase}CreateTableImpl")
            .getDeclaredConstructor(this@DBController::class.java)
            .newInstance(this@DBController)
            .let {
                it as CreateTable
                it.execute(table, columns, incrementId, db)
            }
    }

    fun fetchTables(): List<TableTemplate> {
        return TimeUtil.printMethodTime("fetchTables") {
            Class.forName("com.holland.db.service.impl.${dataSource.lowerCase}.${dataSource.upperCamelCase}FetchTablesImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .let {
                    it as FetchTables
                    it.execute()
                }
        }
    }

    /**
     * MYSQL:   获取 DATABASE
     * POSTGRE: 获取 SCHEMA
     */
    fun fetchDbs(): List<String> {
        return TimeUtil.printMethodTime("fetchDbs") {
            return@printMethodTime when (dataSource) {
                ORACLE -> arrayListOf()
                MYSQL -> {
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
                    return@printMethodTime result
                }
                POSTGRE -> {
                    val result = mutableListOf<String>()
                    val statement =
                        connection.prepareStatement("SELECT schema_name FROM information_schema.schemata where schema_name <> 'information_schema' and schema_name <> 'pg_catalog' and schema_name <> 'pg_toast';")
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
                    return@printMethodTime result
                }
            }
        }
    }

    fun fetchColumns(tableName: String): List<ColumnTemplate> {
        return TimeUtil.printMethodTime("fetchColumns") {
            Class.forName("com.holland.db.service.impl.${dataSource.lowerCase}.${dataSource.upperCamelCase}FetchColumnsImpl")
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
            val statement = when (dataSource) {
                POSTGRE -> connection.prepareStatement("select 1");
                else -> connection.prepareStatement("select 1 from dual");
            }
            statement.execute()
            statement.resultSet.let {
                it.next()
                it.getInt(1)
            } == 1
        }
    }

    fun generatePojo(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) =
        Generator.generatePojo(path, `package`, table, columns)

    fun generateMapper(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) =
        Generator.generateMapper(path, `package`, table, columns)

    fun generateService(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) =
        Generator.generateService(path, `package`, table, columns)

    fun generateControl(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) =
        Generator.generateControl(path, `package`, table, columns)

    fun generateBe(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        generatePojo(path, `package`, table, columns)
        generateMapper(path, `package`, table, columns)
        generateService(path, `package`, table, columns)
        generateControl(path, `package`, table, columns)
    }
}
