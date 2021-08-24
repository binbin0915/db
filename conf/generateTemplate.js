/**
 * 参数解释
 * @param path                          后端路径 || "./test"
 * @param package                       包名
 * @param table                         table对象 {name: String, type: String, comment: String}
 *                                          命名规则 name: UPPER_UNDERSCORE,
 *                                          type = 'TABLE' or 'VIEW'
 * @param columns                       List<Column> {columnName: String, dbDataType: String, javaDataType: String, charLength: Long, nullable: Boolean, dataDefault: String, comments: String, pk: Boolean}
 *                                          命名规则 columnName: 大写下划线命名
 *                                          columnName_LOWER_CAMEL: 小写驼峰命名
 *                                          columnName_UPPER_CAMEL: 大写驼峰命名
 *                                          dbDataType: 数据库中字段类型
 *                                          javaDataType: 对应java字段类型
 *                                          charLength: 最大长度
 *                                          nullable: 可空
 *                                          dataDefault: 默认值
 *                                          comments: 备注
 *                                          pk: 是否主键
 * @param tableName_UPPER_UNDERSCORE    表名 TABLE_NAME
 * @param tableName_UPPER_CAMEL         表名 TableName
 * @param tableName_LOWER_CAMEL         表名 tableName
 * @param pk_name_LOWER_CAMELE          主键名称 primaryKey
 * @param pk_name_UPPER_UNDERSCORE      主键名称 PRIMARY_KEY
 * @param pk_javaType                   主键字段类型
 * @param pk_comment                    主键备注
 * @returns {string}                    返回字符串模板
 */

/**
 * datasync 模块特供 !!!
 */
function generateControl(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var datasync_parseTask = '\n\n-----datasync_parseTask-----'
        .appendLine('                    case "' + tableName_UPPER_UNDERSCORE + '":\n' +
            '                        final ' + tableName_UPPER_CAMEL + ' ' + tableName_LOWER_CAMEL + ' = JSON.parseObject(it.getDataInfo(), ' + tableName_UPPER_CAMEL + '.class);\n' +
            '                        ' + tableName_LOWER_CAMEL + '.setDataState("-");\n' +
            '                        if (opType == 3) {\n' +
            '                            ' + tableName_LOWER_CAMEL + 'Service.delete(' + tableName_LOWER_CAMEL + '.getSyncId());\n' +
            '                        } else {\n' +
            '                            if (' + tableName_LOWER_CAMEL + 'Service.isExist(' + tableName_LOWER_CAMEL + '.getSyncId())) {\n' +
            '                                ' + tableName_LOWER_CAMEL + 'Service.update(' + tableName_LOWER_CAMEL + ');\n' +
            '                            } else {\n' +
            '                                ' + tableName_LOWER_CAMEL + 'Service.add(' + tableName_LOWER_CAMEL + ');\n' +
            '                            }\n' +
            '                        }\n' +
            '                        break;')
    return datasync_parseTask
}

function serviceInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return 'package ' + package + '.service;\n'
        .appendLine('import ' + package + '.pojo.' + tableName_UPPER_CAMEL + ';\n' +
            'import com.github.pagehelper.PageInfo;\n' +
            'import java.util.Map;\n' +
            'import java.util.List;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' * Holland\'s DB_generate_utils Special Supply HWTDL\n' +
            ' */')
        .appendLine('public interface I' + tableName_UPPER_CAMEL + 'Service {')
        .appendLine('    boolean isExist(String syncId);\n')
        .appendLine('    List<' + tableName_UPPER_CAMEL + '> selectAll(Map map);\n')
        .appendLine('    void delete(String syncId);\n')
        .appendLine('    void update(' + tableName_UPPER_CAMEL + ' record);\n')
        .appendLine('    void add(' + tableName_UPPER_CAMEL + ' record);\n')
        .appendLine('}')
}

function serviceImplement(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return 'package ' + package + '.service.impl;\n'
        .appendLine('import com.github.pagehelper.PageHelper;\n' +
            'import com.github.pagehelper.PageInfo;\n' +
            'import ' + package + '.mapper.' + tableName_UPPER_CAMEL + 'Mapper;\n' +
            'import ' + package + '.pojo.' + tableName_UPPER_CAMEL + ';\n' +
            'import ' + package + '.service.I' + tableName_UPPER_CAMEL + 'Service;\n' +
            'import org.springframework.beans.factory.annotation.Autowired;\n' +
            'import org.springframework.stereotype.Service;\n' +
            'import org.springframework.util.StringUtils;\n' +
            'import java.util.List;\n' +
            'import java.util.Map;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' * Holland\'s DB_generate_utils Special Supply HWTDL\n' +
            ' */')
        .appendLine('@Service')
        .appendLine('public class ' + tableName_UPPER_CAMEL + 'ServiceImpl implements I' + tableName_UPPER_CAMEL + 'Service {\n')
        .appendLine('    @Autowired\n' +
            '    private ' + tableName_UPPER_CAMEL + 'Mapper ' + tableName_LOWER_CAMEL + 'Mapper;')
        .appendLine('\n    @Override\n' +
            '    public boolean isExist(String syncId) {\n' +
            '        return ' + tableName_LOWER_CAMEL + 'Mapper.count(syncId) > 0;\n' +
            '    }')
        .appendLine('\n    @Override\n' +
            '    public List<' + tableName_UPPER_CAMEL + '> selectAll(Map map) {\n' +
            '        return ' + tableName_LOWER_CAMEL + 'Mapper.selectAllByMap(map);\n' +
            '    }')
        .appendLine('\n    @Override\n' +
            '    public void delete(String syncId) {\n' +
            '        ' + tableName_LOWER_CAMEL + 'Mapper.deleteBySyncId(syncId);\n' +
            '    }')
        .appendLine('\n    @Override\n' +
            '    public void update(' + tableName_UPPER_CAMEL + ' record) {\n' +
            '        ' + tableName_LOWER_CAMEL + 'Mapper.updateBySyncIdSelective(record);\n' +
            '    }')
        .appendLine('\n    @Override\n' +
            '    public void add(' + tableName_UPPER_CAMEL + ' record) {\n' +
            '        ' + tableName_LOWER_CAMEL + 'Mapper.insertSelective(record);\n' +
            '    }')
        .appendLine('}')
}

function mapperInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return 'package ' + package + '.mapper;\n'
        .appendLine('import ' + package + '.pojo.' + tableName_UPPER_CAMEL + ';\n' +
            'import org.apache.ibatis.annotations.Mapper;\n' +
            'import java.util.List;\n' +
            'import java.util.Map;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' * Holland\'s DB_generate_utils Special Supply HWTDL\n' +
            ' */')
        .appendLine('@Mapper')
        .appendLine('public interface ' + tableName_UPPER_CAMEL + 'Mapper {')
        .appendLine('    int count(String syncId);\n')
        .appendLine('    List<' + tableName_UPPER_CAMEL + '> selectAllByMap(Map map);\n')
        .appendLine('    int deleteBySyncId(String syncId);\n')
        .appendLine('    int updateBySyncIdSelective(' + tableName_UPPER_CAMEL + ' record);\n')
        .appendLine('    int insertSelective(' + tableName_UPPER_CAMEL + ' record);')
        .appendLine('}')
}

function mapperXml(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var resultMap = '   <resultMap id="BaseResultMap" type="' + package + '.pojo.' + tableName_UPPER_CAMEL + '">\n'
    var len = columns.length
    var Base_Column_List = '    <sql id="Base_Column_List">'
    var sql_selectAllByMap_item = ''
    var sql_updateBySyncIdSelective_item = ''
    var sql_insertSelective_item_1 = ''
    var sql_insertSelective_item_2 = ''
    for (var i = 0; i < len; i++) {
        if (!columns[i].pk) resultMap += '      <result column="' + columns[i].columnName + '" property="' + columns[i].columnName_LOWER_CAMEL + '"/>\n'
        else resultMap += '     <id column="' + columns[i].columnName + '" property="' + columns[i].columnName_LOWER_CAMEL + '"/>\n'

        Base_Column_List += columns[i].columnName + ','

        sql_selectAllByMap_item += '          <if test="' + columns[i].columnName_LOWER_CAMEL + ' != null">AND ' + columns[i].columnName + ' = #{' + columns[i].columnName_LOWER_CAMEL + '}</if>\n'

        if (!columns[i].pk) sql_updateBySyncIdSelective_item += '          <if test="' + columns[i].columnName_LOWER_CAMEL + ' != null">' + columns[i].columnName + ' = #{' + columns[i].columnName_LOWER_CAMEL + '}</if>\n'

        sql_insertSelective_item_1 += '            <if test="' + columns[i].columnName_LOWER_CAMEL + ' != null">' + columns[i].columnName + ',</if>\n'
        sql_insertSelective_item_2 += '          <if test="' + columns[i].columnName_LOWER_CAMEL + ' != null">#{' + columns[i].columnName_LOWER_CAMEL + '},</if>\n'
    }
    resultMap += ('   </resultMap>')
    Base_Column_List = Base_Column_List.substring(0, Base_Column_List.lastIndexOf(",")) + '</sql>'

    var sql_count = '   <select id="count" resultType="java.lang.Integer" parameterType="java.lang.Long">\n' +
        '        select\n' +
        '        count(1)\n' +
        '        from ' + tableName_UPPER_UNDERSCORE + '\n' +
        '        where SYNC_ID = #{syncId}\n' +
        '    </select>'

    var sql_selectAllByMap = '    <select id="selectAllByMap" resultMap="BaseResultMap" parameterType="java.util.Map">\n' +
        '        select\n' +
        '        <include refid="Base_Column_List"/>\n' +
        '        from ' + tableName_UPPER_UNDERSCORE + '\n' +
        '        <where>\n' +
        sql_selectAllByMap_item +
        '        </where>\n' +
        '    </select>'

    var sql_deleteBySyncId = '    <delete id="deleteBySyncId" parameterType="java.lang.Long">\n' +
        '        delete  \n' +
        '        from  ' + tableName_UPPER_UNDERSCORE + '\n' +
        '        where SYNC_ID = #{syncId}\n' +
        '    </delete>'

    var sql_updateBySyncIdSelective = '    <update id="updateBySyncIdSelective" parameterType="' + package + '.pojo.' + tableName_UPPER_CAMEL + '" >\n' +
        '         update ' + tableName_UPPER_UNDERSCORE + '\n' +
        '         <set >\n' +
        sql_updateBySyncIdSelective_item +
        '         </set>\n' +
        '        where SYNC_ID = #{syncId}\n' +
        '    </update>'

    var sql_insertSelective = '    <insert id="insertSelective" parameterType="' + package + '.pojo.' + tableName_UPPER_CAMEL + '" >\n' +
        '        insert into ' + tableName_UPPER_UNDERSCORE + '\n' +
        '        <trim prefix="(" suffix=")" suffixOverrides="," >\n' +
        sql_insertSelective_item_1 +
        '        </trim>\n' +
        '        <trim prefix="values (" suffix=")" suffixOverrides="," >\n' +
        sql_insertSelective_item_2 +
        '        </trim>\n' +
        '    </insert>'

    return '<?xml version="1.0" encoding="UTF-8" ?>\n' +
        '<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >\n' +
        '<mapper namespace="' + package + '.mapper.' + tableName_UPPER_CAMEL + 'Mapper">'
            .appendLine(resultMap)
            .appendLine(Base_Column_List)
            .appendLine(sql_count)
            .appendLine(sql_selectAllByMap)
            .appendLine(sql_deleteBySyncId)
            .appendLine(sql_updateBySyncIdSelective)
            .appendLine(sql_insertSelective)
            .appendLine('</mapper>')
}

function generatePojo(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var itemContent = ''
    var itemGet = ''
    var itemSet = ''
    var len = columns.length
    for (var i = 0; i < len; i++) {
        itemContent += ('\n    /**\n' +
            '     * ' + columns[i].comments + '\n' +
            '     */\n' +
            '    private ' + columns[i].javaDataType + ' ' + columns[i].columnName_LOWER_CAMEL + ';')
        itemGet += '\n    public ' + columns[i].javaDataType + ' get' + columns[i].columnName_UPPER_CAMEL + '() {\n' +
            '        return ' + columns[i].columnName_LOWER_CAMEL + ';\n' +
            '    }'
        itemSet += '\n    public ' + tableName_UPPER_CAMEL + ' set' + columns[i].columnName_UPPER_CAMEL + '(' + columns[i].javaDataType + ' ' + columns[i].columnName_LOWER_CAMEL + ') {\n' +
            '        this.' + columns[i].columnName_LOWER_CAMEL + ' = ' + columns[i].columnName_LOWER_CAMEL + ';\n' +
            '        return this;\n' +
            '    }'
    }
    return 'package ' + package + '.pojo;\n'
        .appendLine('import org.springframework.format.annotation.DateTimeFormat;\n' +
            'import java.math.BigDecimal;\n' +
            'import java.util.Date;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' * Holland\'s DB_generate_utils Special Supply HWTDL\n' +
            ' */')
        .appendLine('public class ' + tableName_UPPER_CAMEL + ' {')
        .appendLine(itemContent)
        .appendLine(itemGet)
        .appendLine(itemSet)
        .appendLine('}')
}

String.prototype.appendLine = function (newLine) {
    return this + '\n' + newLine
}