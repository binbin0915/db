package com.holland.db

import com.google.common.base.CaseFormat.*
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import java.io.File

object Generator {

    fun generateControl(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {

    }

    fun generateService(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {

    }

    /**
     * 多个pk的情况默认只做操第一个
     */
    fun generateDao(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)
        val pkColumn = columns.firstOrNull { it.pk }

        daoXml(composePath, table, composePackage, columns, pkColumn)
        daoInterface(composePath, table, composePackage, pkColumn)
    }

    private fun daoInterface(
        path: String,
        table: TableTemplate,
        `package`: String,
        pkColumn: ColumnTemplate?,
    ) {
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        val pk = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        StringBuilder().append(
            """
        package ${`package`}.dao;

        import ${`package`}.pojo.$className;
        import org.apache.ibatis.annotations.Mapper;

        @Mapper
        public interface ${className}Dao {
            $className selectByPrimaryKey(String $pk);
            
            int deleteByPrimaryKey(String $pk);
            
            int updateByPrimaryKeySelective(${className} record);
            
            int insertSelective($className record);
        }
        """.trimIndent()
        )
            .run {
                FileUtil.newFile(
                    this.toString(),
                    path,
                    "${className}Dao.java"
                )
            }
    }

    private fun daoXml(
        path: String,
        table: TableTemplate,
        `package`: String,
        columns: List<ColumnTemplate>,
        pkColumn: ColumnTemplate?
    ) {
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        StringBuilder().apply {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">""")
            appendLine("""<mapper namespace="${`package`}.dao.$className">""")

            appendLine("""  <resultMap id="BaseResultMap" type="${`package`}.pojo.$className">""")
            columns.forEach {
                appendLine(
                    """    <id column="${it.columnName}" jdbcType="${it.dbDataType}" property="${
                        UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)
                    }" />"""
                )
            }
            appendLine("""  </resultMap>""")

            appendLine("""  <sql id="Base_Column_List">""")
            appendLine("""    ${columns.joinToString(", ") { it.columnName }}""")
            appendLine("""  </sql>""")

            // 没得主键，就没得增删改
            if (null != pkColumn) {
                val parameterType = getParameterType(pkColumn.javaDataType)
                appendLine("""  <select id="selectByPrimaryKey" parameterType="$parameterType" resultMap="BaseResultMap">""")
                appendLine("""    select """)
                appendLine("""    <include refid="Base_Column_List" />""")
                appendLine("""    from ${table.name}""")
                appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName},jdbcType=${pkColumn.dbDataType}}""")
                appendLine("""  </select>""")

                appendLine("""  <delete id="deleteByPrimaryKey" parameterType="$parameterType">""")
                appendLine("""    delete from ${table.name}""")
                appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName},jdbcType=${pkColumn.dbDataType}}""")
                appendLine("""  </delete>""")

                appendLine("""  <update id="updateByPrimaryKeySelective" parameterType="$parameterType">""")
                appendLine("""    update ${table.name}""")
                appendLine(columns.joinToString("\n", "    <set>\n", "\n    </set>") {
                    """      <if test="${
                        UPPER_UNDERSCORE.to(
                            LOWER_CAMEL,
                            it.columnName
                        )
                    } != null"> ${it.columnName} = #{${
                        UPPER_UNDERSCORE.to(
                            LOWER_CAMEL,
                            it.columnName
                        )
                    },jdbcType=${it.dbDataType}},</if>"""
                })
                appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName},jdbcType=${pkColumn.dbDataType}}""")
                appendLine("""  </update>""")
            }

            appendLine("""  <insert id="insertSelective" parameterType="${`package`}.pojo.$className">""")
            appendLine("""    insert into ${table.name}""")
            appendLine(
                columns.joinToString(
                    "\n",
                    "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n",
                    "\n    </trim>"
                ) {
                    """      <if test="${
                        UPPER_UNDERSCORE.to(
                            LOWER_CAMEL,
                            it.columnName
                        )
                    } != null">${it.columnName},</if>"""
                })
            appendLine(
                columns.joinToString(
                    "\n",
                    "    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n",
                    "\n    </trim>"
                ) {
                    """      <if test="${
                        UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)
                    } != null">#{${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)},jdbcType=${it.dbDataType}},</if>"""
                })
            appendLine("""  </insert>""")
            appendLine("""</mapper>""")
        }.run {
            FileUtil.newFile(
                this.toString(),
                path,
                "${className}Dao.xml"
            )
        }
    }

    fun generatePojo(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)

        val pojoBuilder = StringBuilder()
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)

        pojoBuilder.append(
            """
        package $composePackage.pojo;

        import lombok.Data;
        import lombok.experimental.Accessors;
        import org.springframework.format.annotation.DateTimeFormat;
        import javax.validation.constraints.*;
        import java.math.BigDecimal;
        import java.util.Date;
        import io.swagger.annotations.ApiModelProperty;
        """.trimIndent()
        )

        pojoBuilder.append(
            """

                        /**
                         * comment: ${table.comment}
                         */
                         """.trimIndent()
        )
            .append("\n@Data")
            .append("\n@Accessors(chain = true)")
            .append("\npublic class $className {")

        columns.forEach {

            pojoBuilder.append(
                """
    /**
     * ${it.comments}
     */
"""
            )
                .append("\t@ApiModelProperty(value=\"${it.comments}\")\n")
                .append(if (it.nullable) "" else "\t@NotNull\n")
                .append(if ("Date" == it.dbDataType) "\t@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n" else "")
                .append("\t@Size(max = ${it.charLength}, message = \"${it.columnName} 长度不能大于${it.charLength}\")\n")
                .append("\tprivate ${it.dbDataType} ${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)};\n")
        }

        pojoBuilder.append("}")

        FileUtil.newFile(
            pojoBuilder.toString(),
            composePath,
            "$className.java"
        )
    }

    private fun getParameterType(javaType: String): String {
        return when (javaType) {
            "String" -> "java.lang.String"
            "Date" -> "java.util.Date"
            "Long" -> "java.lang.Long"
            "BigDecimal" -> "java.math.BigDecimal"
            else -> "java.lang.Object"
        }
    }

    private fun composePath(path: String, `package`: String) =
        if (path.isEmpty()) "temp" else path + File.separatorChar + `package`.replace(".", File.separator)

    private fun composePackage(`package`: String) = `package`.replace(File.separator, ".")
}