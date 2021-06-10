package com.holland.db

import com.google.common.base.CaseFormat.*
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import java.io.File

@Suppress("PrivatePropertyName")
class Generator(
    private var path: String,
    private var `package`: String,
    private val table: TableTemplate,
    private val columns: List<ColumnTemplate>
) {
    private val tableName_UPPER_UNDERSCORE: String
    private val tableName_UPPER_CAMEL: String
    private val tableName_LOWER_CAMEL: String

    private val pk_name_LOWER_CAMELE: String
    private val pk_name_UPPER_UNDERSCORE: String
    private val pk_javaType: String
    private val pk_comment: String

    init {
        path = composePath(path, `package`)
        `package` = composePackage(`package`)
        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)

        tableName_UPPER_UNDERSCORE = table.name
        tableName_UPPER_CAMEL = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)
        tableName_LOWER_CAMEL = UPPER_CAMEL.to(LOWER_CAMEL, tableName_UPPER_CAMEL)

        pk_name_LOWER_CAMELE = if (null == pkColumn) "key" else UPPER_UNDERSCORE.to(LOWER_CAMEL, pkColumn.columnName)
        pk_name_UPPER_UNDERSCORE = pkColumn?.columnName ?: "KEY"
        pk_javaType = pkColumn?.javaDataType ?: "String"
        pk_comment = pkColumn?.comments ?: "null"
    }

    fun generateControl() {
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.controller;

               |import ${`package`}.pojo.$tableName_UPPER_CAMEL;
               |import ${`package`}.pojo.Response;
               |import ${`package`}.service.I${tableName_UPPER_CAMEL}Service;
               |import com.github.pagehelper.PageInfo;
               |import io.swagger.annotations.Api;
               |import org.springframework.web.bind.annotation.*;
               |import javax.annotation.Resource;

               |@Api(tags = "${table.comment ?: tableName_UPPER_CAMEL}", value = "${table.comment ?: tableName_UPPER_CAMEL}")
               |@RestController
               |@RequestMapping("${UPPER_CAMEL.to(LOWER_CAMEL, tableName_UPPER_CAMEL)}")
               |public class ${tableName_UPPER_CAMEL}Controller {
               |
               |    @Resource
               |    private I${tableName_UPPER_CAMEL}Service ${tableName_LOWER_CAMEL + "Service"};
               |
               |    @GetMapping("{$pk_name_LOWER_CAMELE}")
               |    public Response<$tableName_UPPER_CAMEL> find(@PathVariable("$pk_name_LOWER_CAMELE") $pk_javaType $pk_name_LOWER_CAMELE) {
               |        return Response.<$tableName_UPPER_CAMEL>success(${tableName_LOWER_CAMEL + "Service"}.getModel($pk_name_LOWER_CAMELE))
               |    }

               |    @GetMapping("list")
               |    public Response<$tableName_UPPER_CAMEL> list(@RequestBody $tableName_UPPER_CAMEL record, Integer page, Integer limit) {
               |        final PageInfo<$tableName_UPPER_CAMEL> list = ${tableName_LOWER_CAMEL + "Service"}.getList(record, page, limit);
               |        return Response.<$tableName_UPPER_CAMEL>success(list.getList(), list.getTotal());
               |    }

               |    @DeleteMapping("{$pk_name_LOWER_CAMELE}")
               |    public Response<$tableName_UPPER_CAMEL> delete(@PathVariable("$pk_name_LOWER_CAMELE") $pk_javaType $pk_name_LOWER_CAMELE) {
               |        return Response.<$tableName_UPPER_CAMEL>success(${tableName_LOWER_CAMEL + "Service"}.delModel($pk_name_LOWER_CAMELE));
               |    }

               |    @PutMapping
               |    public Response<$tableName_UPPER_CAMEL> update(@RequestBody $tableName_UPPER_CAMEL record) {
               |        return Response.<$tableName_UPPER_CAMEL>success(${tableName_LOWER_CAMEL + "Service"}.updateModel(record));
               |    }

               |    @PostMapping
               |    public Response<$tableName_UPPER_CAMEL> add(@RequestBody $tableName_UPPER_CAMEL record) {
               |        return Response.<$tableName_UPPER_CAMEL>success(${tableName_LOWER_CAMEL + "Service"}.addModel(record));
               |    }
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "${tableName_UPPER_CAMEL}Controller.java"
                )
            }
    }

    fun generateService() {
        serviceInterface()
        serviceImplement()
    }

    private fun serviceInterface() {
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.service;

               |import ${`package`}.pojo.$tableName_UPPER_CAMEL;
               |import com.github.pagehelper.PageInfo;

               |public interface I${tableName_UPPER_CAMEL}Service {
               |    $tableName_UPPER_CAMEL getModel($pk_javaType $pk_name_LOWER_CAMELE);

               |    PageInfo<$tableName_UPPER_CAMEL> getList($tableName_UPPER_CAMEL record, Integer page, Integer limit);

               |    int delModel($pk_javaType $pk_name_LOWER_CAMELE);
                    
               |    int updateModel($tableName_UPPER_CAMEL record);
                    
               |    int addModel($tableName_UPPER_CAMEL record);
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "I${tableName_UPPER_CAMEL}Service.java"
                )
            }
    }

    private fun serviceImplement() {
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.service.impl;

               |import ${`package`}.pojo.$tableName_UPPER_CAMEL;
               |import ${`package`}.service.I${tableName_UPPER_CAMEL}Service;
               |import com.github.pagehelper.PageHelper;
               |import com.github.pagehelper.PageInfo;
               |import javax.annotation.Resource;
               |import org.springframework.stereotype.Service;

               |import java.util.List;

               |@Service
               |public class ${tableName_UPPER_CAMEL}ServiceImpl implements I${tableName_UPPER_CAMEL}Service {

               |    @Resource
               |    private ${tableName_UPPER_CAMEL}Mapper ${tableName_LOWER_CAMEL + "Mapper"};

               |    @Override
               |    public $tableName_UPPER_CAMEL getModel($pk_javaType $pk_name_LOWER_CAMELE) {
               |        return ${tableName_LOWER_CAMEL + "Mapper"}.selectByPrimaryKey($pk_name_LOWER_CAMELE);
               |    }

               |    @Override
               |    public PageInfo<$tableName_UPPER_CAMEL> getList($tableName_UPPER_CAMEL record, Integer page, Integer limit) {
               |        if (page == null || page <= 0) page = 1;
               |        if (limit == null || limit <= 0) limit = 10;
               |        PageHelper.startPage(page, limit);
               |        final List<$tableName_UPPER_CAMEL> list = ${tableName_LOWER_CAMEL + "Mapper"}.listByCondition(record, page, limit);
               |        return new PageInfo<>(list);
               |    }

               |    @Override
               |    public int delModel($pk_javaType $pk_name_LOWER_CAMELE) {
               |        return ${tableName_LOWER_CAMEL + "Mapper"}.deleteByPrimaryKey($pk_name_LOWER_CAMELE);
               |    }
                    
               |    @Override
               |    public int updateModel($tableName_UPPER_CAMEL record) {
               |        return ${tableName_LOWER_CAMEL + "Mapper"}.updateByPrimaryKeySelective(record);
               |    }
                    
               |    @Override
               |    public int addModel($tableName_UPPER_CAMEL record) {
               |        return ${tableName_LOWER_CAMEL + "Mapper"}.insertSelective(record);
               |    }
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "${tableName_UPPER_CAMEL}ServiceImpl.java"
                )
            }
    }

    fun generateMapper() {
        mapperXml()
        mapperInterface()
    }

    private fun mapperInterface() {
        StringBuffer()
            .apply {
                append(
                    """|package ${`package`}.mapper;

               |import ${`package`}.pojo.$tableName_UPPER_CAMEL;
               |import org.apache.ibatis.annotations.Mapper;
        
               |import java.util.List;

               |@Mapper
               |public interface ${tableName_UPPER_CAMEL}Mapper {
               |    $tableName_UPPER_CAMEL selectByPrimaryKey($pk_javaType $pk_name_LOWER_CAMELE);

               |    List<$tableName_UPPER_CAMEL> listByCondition($tableName_UPPER_CAMEL record, int page, int limit);

               |    int deleteByPrimaryKey($pk_javaType $pk_name_LOWER_CAMELE);
                    
               |    int updateByPrimaryKeySelective(${tableName_UPPER_CAMEL} record);
                    
               |    int insertSelective($tableName_UPPER_CAMEL record);
               |}""".trimMargin()
                )
            }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
                    "${tableName_UPPER_CAMEL}Mapper.java"
                )
            }
    }

    private fun mapperXml() {
        StringBuilder().apply {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine("""<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">""")
            appendLine("""<mapper namespace="${`package`}.mapper.$tableName_UPPER_CAMEL">""")

            appendLine("""  <resultMap id="BaseResultMap" type="${`package`}.pojo.$tableName_UPPER_CAMEL">""")
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

            appendLine("""  <select id="selectByPrimaryKey" parameterType="$pk_javaType" resultMap="BaseResultMap">""")
            appendLine("""    select """)
            appendLine("""    <include refid="Base_Column_List" />""")
            appendLine("""    from ${table.name}""")
            appendLine("""    where $pk_name_UPPER_UNDERSCORE = #{$pk_name_LOWER_CAMELE}""")
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

            appendLine("""  <delete id="deleteByPrimaryKey" parameterType="$pk_javaType">""")
            appendLine("""    delete from ${table.name}""")
            appendLine("""    where $pk_name_UPPER_UNDERSCORE = #{$pk_name_LOWER_CAMELE}""")
            appendLine("""  </delete>""")

            appendLine("""  <update id="updateByPrimaryKeySelective" parameterType="$pk_javaType">""")
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
            appendLine("""    where $pk_name_UPPER_UNDERSCORE = #{$pk_name_LOWER_CAMELE}""")
            appendLine("""  </update>""")

            appendLine("""  <insert id="insertSelective" parameterType="${`package`}.pojo.$tableName_UPPER_CAMEL">""")
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
                "${tableName_UPPER_CAMEL}Mapper.xml"
            )
        }
    }

    fun generatePojo() {
        StringBuilder().apply {
            append(
                """|package ${`package`}.pojo;
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
               | * comment: ${table.comment ?: tableName_UPPER_CAMEL}
               | */
               |@Data
               |@Accessors(chain = true)
               |public class $tableName_UPPER_CAMEL {
               |""".trimMargin()
            )
            columns.forEach {
                append(
                    """|    /**
                   |     * ${it.comments ?: it.columnName}
                   |     */
                   |    @ApiModelProperty(value = "${it.comments ?: it.columnName}")${if (it.nullable) "" else "\n\t@NotNull"}${if ("Date" == it.javaDataType) "\n\t@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")" else ""}${if (it.charLength > 0) "\n\t@Size(max = ${it.charLength}, message = \"${it.columnName} 长度不能大于${it.charLength}\")" else ""}
                   |    private ${it.javaDataType} ${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)};
                   |
                   |""".trimMargin()
                )
            }
            append("}")
        }
            .let {
                FileUtil.newFile(
                    it.toString(),
                    path,
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
        if (path.isEmpty()) "temp" else path + File.separatorChar + `package`.replace(".", File.separator)

    private fun composePackage(`package`: String) = `package`.replace(File.separator, ".")
}
