package com.holland.db

import com.google.common.base.CaseFormat.*
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import jdk.nashorn.api.scripting.NashornScriptEngine
import java.io.File
import java.io.FileReader
import javax.script.ScriptEngineManager

@Suppress("PrivatePropertyName")
class GeneratorJS(
    private var path: String,
    private var `package`: String,
    private val table: TableTemplate,
    private val columns: List<ColumnTemplate>,
    private val choice_code_template: String,
) {
    private val package_file_separatorChar: String

    private val tableName_UPPER_UNDERSCORE: String
    private val tableName_UPPER_CAMEL: String
    private val tableName_LOWER_CAMEL: String

    private val pk_name_LOWER_CAMELE: String
    private val pk_name_UPPER_CAMELE: String
    private val pk_name_UPPER_UNDERSCORE: String
    private val pk_javaType: String
    private val pk_comment: String

    init {
        path = composePath(path, `package`)
        `package` = composePackage(`package`)
        package_file_separatorChar = `package`.replace(".", File.separator)
        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)

        tableName_UPPER_UNDERSCORE = table.name
        tableName_UPPER_CAMEL = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        tableName_LOWER_CAMEL = UPPER_CAMEL.to(LOWER_CAMEL, tableName_UPPER_CAMEL)

        pk_name_LOWER_CAMELE = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        pk_name_UPPER_CAMELE = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(UPPER_CAMEL, pkColumn.columnName)
        pk_name_UPPER_UNDERSCORE = pkColumn?.columnName ?: "KEY"
        pk_javaType = pkColumn?.javaDataType ?: "String"
        pk_comment = pkColumn?.comments ?: "null"
    }

    fun generateControl() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "generateControl",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }
            .let {
                FileUtil.newFile(
                    it?.toString(),
                    path + File.separatorChar + "java" + File.separatorChar + package_file_separatorChar + File.separatorChar + "controller",
                    "${tableName_UPPER_CAMEL}Controller.java"
                )
            }
    }

    fun generateService() {
        serviceInterface()
        serviceImplement()
    }

    private fun serviceInterface() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "serviceInterface",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path + File.separatorChar + "java" + File.separatorChar + package_file_separatorChar + File.separatorChar + "service",
                    "I${tableName_UPPER_CAMEL}Service.java"
                )
            }
    }

    private fun serviceImplement() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "serviceImplement",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path + File.separatorChar + "java" + File.separatorChar + package_file_separatorChar + File.separatorChar + "service" + File.separatorChar + "impl",
                    "${tableName_UPPER_CAMEL}ServiceImpl.java"
                )
            }
    }

    fun generateMapper() {
        mapperXml()
        mapperInterface()
    }

    private fun mapperInterface() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "mapperInterface",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path + File.separatorChar + "java" + File.separatorChar + package_file_separatorChar + File.separatorChar + "mapper",
                    "${tableName_UPPER_CAMEL}Mapper.java"
                )
            }
    }

    private fun mapperXml() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "mapperXml",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }.run {
                FileUtil.newFile(
                    this.toString(),
                    path + File.separatorChar + "resources" + File.separatorChar + "mapper",
                    "${tableName_UPPER_CAMEL}Mapper.xml"
                )
            }
    }

    fun generatePojo() {
        ScriptEngineManager().getEngineByName("javascript")
            .let {
                it.eval(FileReader("conf/${choice_code_template}"))
                val invokeFunction = (it as NashornScriptEngine).invokeFunction(
                    "generatePojo",
                    path,
                    `package`,
                    table,
                    columns,
                    tableName_UPPER_UNDERSCORE,
                    tableName_UPPER_CAMEL,
                    tableName_LOWER_CAMEL,
                    pk_name_LOWER_CAMELE,
                    pk_name_UPPER_CAMELE,
                    pk_name_UPPER_UNDERSCORE,
                    pk_javaType,
                    pk_comment
                )
                invokeFunction
            }
            .let {
                FileUtil.newFile(
                    it?.toString(),
                    path + File.separatorChar + "java" + File.separatorChar + package_file_separatorChar + File.separatorChar + "pojo",
                    "$tableName_UPPER_CAMEL.java"
                )
            }
    }

    /**
     * javaType -> jdbcType
     */
    private fun getParameterType(javaType: String): String {
        return when (javaType) {
            "String" -> "java.lang.String"
            "Date", "LocalDate", "LocalTime", "LocalDateTime" -> "java.util.Date"
            "Long", "long", "Integer", "int" -> "java.lang.Long"
            "BigDecimal" -> "java.math.BigDecimal"
            else -> "java.lang.Object"
        }
    }

    private fun composePath(path: String, `package`: String) =
        if (path.isEmpty()) "temp" else path /*+ File.separatorChar + `package`.replace(".", File.separator)*/

    private fun composePackage(`package`: String) = `package`.replace(File.separator, ".")
}
