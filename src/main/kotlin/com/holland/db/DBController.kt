package com.holland.db

import com.holland.db.service.FetchTables
import com.holland.db.service.ModelGenerator
import com.holland.db.service.ServiceGenerator
import com.holland.util.FileWriteUtil
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

@Suppress("unused", "JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
class DBController(val dataSource: String, host: String, port: String, user: String, pwd: String) {
    lateinit var `package`: String
    lateinit var tableName: String
    var schema: String? = null

    var connection: Connection
    private val classPrefix: String

    init {
        FileWriteUtil.mkdir(rootPath)
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
    }

    fun fetchTables(): DBController {
        with(classPrefix) {
            Class.forName("com.holland.db.service.impl.${this.toLowerCase()}.${this}FetchTablesImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .apply {
                    this as FetchTables
                    execute()
                }
        }
        return this
    }

    fun generateAll() = this.generateFE().generateBE()

    fun generateFE() = this

    fun generateBE() = this.generateModel().generateService()

    fun generateModel(): DBController {
        FileWriteUtil.mkdir("$rootPath${File.separatorChar}$pojo")
        with(classPrefix) {
            Class.forName("com.holland.db.service.impl.${this.toLowerCase()}.${this}ModelGeneratorImpl")
                .getDeclaredConstructor(this@DBController::class.java)
                .newInstance(this@DBController)
                .apply {
                    this as ModelGenerator
                    execute()
                }
        }
        return this
    }

    fun generateService(): DBController {
        FileWriteUtil.mkdir("$rootPath${File.separatorChar}$dao")
        FileWriteUtil.mkdir("$rootPath${File.separatorChar}$dao${File.separatorChar}impl")
        ServiceGenerator(this).execute()
        return this
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

    companion object {
        const val rootPath = "generate"
        const val pojo = "pojo"
        const val dao = "service"
    }
}
