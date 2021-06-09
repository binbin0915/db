package com.holland.db

import com.google.common.base.CaseFormat.*
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import java.io.File

object Generator {

    fun generateControl(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)
        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)

        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        val service = UPPER_CAMEL.to(LOWER_CAMEL, className) + "Service"
        val pk = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        val javaType = pkColumn?.javaDataType ?: "String"

        StringBuffer()
            .apply {
                append(
                    """|package ${composePackage}.controller;

               |import ${composePackage}.pojo.$className;
               |import ${composePackage}.pojo.Response;
               |import ${composePackage}.service.I${className}Service;
               |import com.github.pagehelper.PageInfo;
               |import io.swagger.annotations.Api;
               |import org.springframework.web.bind.annotation.*;
               |import javax.annotation.Resource;

               |@Api(tags = "${table.comment ?: className}", value = "${table.comment ?: className}")
               |@RestController
               |@RequestMapping("${UPPER_CAMEL.to(LOWER_CAMEL, className)}")
               |public class ${className}Controller {
               |
               |    @Resource
               |    private I${className}Service $service;
               |
               |    @GetMapping("{$pk}")
               |    public Response<$className> find(@PathVariable("$pk") $javaType $pk) {
               |        return Response.<$className>success($service.getModel($pk))
               |    }

               |    @GetMapping("list")
               |    public Response<$className> list(@RequestBody $className record, Integer page, Integer limit) {
               |        final PageInfo<$className> list = $service.getList(record, page, limit);
               |        return Response.<$className>success(list.getList(), list.getTotal());
               |    }

               |    @DeleteMapping("{$pk}")
               |    public Response<$className> delete(@PathVariable("$pk") $javaType $pk) {
               |        return Response.<$className>success($service.delModel($pk));
               |    }

               |    @PutMapping
               |    public Response<$className> update(@RequestBody $className record) {
               |        return Response.<$className>success($service.updateModel(record));
               |    }

               |    @PostMapping
               |    public Response<$className> add(@RequestBody $className record) {
               |        return Response.<$className>success($service.addModel(record));
               |    }
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    composePath,
                    "${className}Controller.java"
                )
            }
    }

    fun generateService(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)
        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)

        serviceInterface(composePath, table, composePackage, pkColumn)
        serviceImplement(composePath, table, composePackage, columns, pkColumn)
    }

    private fun serviceInterface(
        path: String,
        table: TableTemplate,
        `package`: String,
        pkColumn: ColumnTemplate?
    ) {
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        val pk = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        val javaType = pkColumn?.javaDataType ?: "String"
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.service;

               |import ${`package`}.pojo.$className;
               |import com.github.pagehelper.PageInfo;

               |public interface I${className}Service {
               |    $className getModel($javaType $pk);

               |    PageInfo<$className> getList($className record, Integer page, Integer limit);

               |    int delModel($javaType $pk);
                    
               |    int updateModel($className record);
                    
               |    int addModel($className record);
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "I${className}Service.java"
                )
            }
    }

    private fun serviceImplement(
        path: String,
        table: TableTemplate,
        `package`: String,
        columns: List<ColumnTemplate>,
        pkColumn: ColumnTemplate?
    ) {
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        val mapper = UPPER_CAMEL.to(LOWER_CAMEL, className) + "Mapper"
        val pk = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        val javaType = pkColumn?.javaDataType ?: "String"
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.service.impl;

               |import ${`package`}.pojo.$className;
               |import ${`package`}.service.I${className}Service;
               |import com.github.pagehelper.PageHelper;
               |import com.github.pagehelper.PageInfo;
               |import javax.annotation.Resource;
               |import org.springframework.stereotype.Service;

               |import java.util.List;

               |@Service
               |public class ${className}ServiceImpl implements I${className}Service {

               |    @Resource
               |    private ${className}Mapper $mapper;

               |    @Override
               |    public $className getModel($javaType $pk) {
               |        return $mapper.selectByPrimaryKey($pk);
               |    }

               |    @Override
               |    public PageInfo<$className> getList($className record, Integer page, Integer limit) {
               |        if (page == null || page <= 0) page = 1;
               |        if (limit == null || limit <= 0) limit = 10;
               |        PageHelper.startPage(page, limit);
               |        final List<$className> list = $mapper.listByCondition(record, page, limit);
               |        return new PageInfo<>(list);
               |    }

               |    @Override
               |    public int delModel($javaType $pk) {
               |        return $mapper.deleteByPrimaryKey($pk);
               |    }
                    
               |    @Override
               |    public int updateModel($className record) {
               |        return $mapper.updateByPrimaryKeySelective(record);
               |    }
                    
               |    @Override
               |    public int addModel($className record) {
               |        return $mapper.insertSelective(record);
               |    }
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "${className}ServiceImpl.java"
                )
            }
    }

    /**
     * 多个pk的情况默认只做操第一个
     */
    fun generateMapper(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)
        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)

        mapperXml(composePath, table, composePackage, columns, pkColumn)
        mapperInterface(composePath, table, composePackage, pkColumn)
    }

    private fun mapperInterface(
        path: String,
        table: TableTemplate,
        `package`: String,
        pkColumn: ColumnTemplate?,
    ) {
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        val pk = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        val javaType = pkColumn?.javaDataType ?: "String"
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.mapper;

               |import ${`package`}.pojo.$className;
               |import org.apache.ibatis.annotations.Mapper;
        
               |import java.util.List;

               |@Mapper
               |public interface ${className}Mapper {
               |    $className selectByPrimaryKey($javaType $pk);

               |    List<$className> listByCondition($className record, int page, int limit);

               |    int deleteByPrimaryKey($javaType $pk);
                    
               |    int updateByPrimaryKeySelective(${className} record);
                    
               |    int insertSelective($className record);
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "${className}Mapper.java"
                )
            }
    }

    private fun mapperXml(
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
            appendLine("""<mapper namespace="${`package`}.mapper.$className">""")

            appendLine("""  <resultMap id="BaseResultMap" type="${`package`}.pojo.$className">""")
            columns.forEach {
                appendLine(
                    """    <id column="${it.columnName}" property="${
                        UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)
                    }" />"""
                )
            }
            appendLine("""  </resultMap>""")

            appendLine("""  <sql id="Base_Column_List">""")
            appendLine("""    ${columns.joinToString(", ") { it.columnName }}""")
            appendLine("""  </sql>""")

            val parameterType = if (pkColumn == null) "String" else getParameterType(pkColumn.javaDataType)
            appendLine("""  <select id="selectByPrimaryKey" parameterType="$parameterType" resultMap="BaseResultMap">""")
            appendLine("""    select """)
            appendLine("""    <include refid="Base_Column_List" />""")
            appendLine("""    from ${table.name}""")
            if (pkColumn != null) appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName}}""")
            appendLine("""  </select>""")

            appendLine("""  <select id="listByCondition" resultMap="BaseResultMap">""")
            appendLine("""    select """)
            appendLine("""    <include refid="Base_Column_List" />""")
            appendLine("""    from ${table.name}""")
            appendLine(
                columns.joinToString(
                    "\n",
                    "    <where>\n",
                    "\n   </where>"
                ) {
                    val lowerCamel = UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)
                    "       <if test=\"$lowerCamel != null and $lowerCamel !=''\">AND ${it.columnName} = #{$lowerCamel}</if>"
                })
            appendLine("""  </select>""")

            appendLine("""  <delete id="deleteByPrimaryKey" parameterType="$parameterType">""")
            appendLine("""    delete from ${table.name}""")
            if (pkColumn != null) appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName}}""")
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
                }},</if>"""
            })
            if (pkColumn != null) appendLine("""    where ${pkColumn.columnName} = #{${pkColumn.columnName}}""")
            appendLine("""  </update>""")

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
                    } != null">#{${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)}},</if>"""
                })
            appendLine("""  </insert>""")
            appendLine("""</mapper>""")
        }.run {
            FileUtil.newFile(
                this.toString(),
                path,
                "${className}Mapper.xml"
            )
        }
    }

    fun generatePojo(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val composePath = composePath(path, `package`)
        val composePackage = composePackage(`package`)

        val pojoBuilder = StringBuilder()
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)

        pojoBuilder.append(
            """|package $composePackage.pojo;
               |
               |import lombok.Data;
               |import lombok.experimental.Accessors;
               |import org.springframework.format.annotation.DateTimeFormat;
               |import javax.validation.constraints.*;
               |import java.math.BigDecimal;
               |import java.util.Date;
               |import io.swagger.annotations.ApiModelProperty;
               |
               |/**
               | * comment: ${table.comment ?: className}
               | */
               |@Data
               |@Accessors(chain = true)
               |public class $className {
               |""".trimMargin()
        )

        columns.forEach {
            pojoBuilder.append(
                """|    /**
                   |     * ${it.comments ?: it.columnName}
                   |     */
                   |    @ApiModelProperty(value = "${it.comments ?: it.columnName}")${if (it.nullable) "" else "\n\t@NotNull"}${if ("Date" == it.javaDataType) "\n\t@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")" else ""}${if (it.charLength > 0) "\n\t@Size(max = ${it.charLength}, message = \"${it.columnName} 长度不能大于${it.charLength}\")" else ""}
                   |    private ${it.javaDataType} ${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)};
                   |
                   |""".trimMargin()
            )
        }

        pojoBuilder.append("}")

        FileUtil.newFile(
            pojoBuilder.toString(),
            composePath,
            "$className.java"
        )
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
        if (path.isEmpty()) "temp" else path + File.separatorChar + `package`.replace(".", File.separator)

    private fun composePackage(`package`: String) = `package`.replace(File.separator, ".")
}
