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
 * @param pk_name_UPPER_CAMELE          主键名称 PrimaryKey
 * @param pk_name_UPPER_UNDERSCORE      主键名称 PRIMARY_KEY
 * @param pk_javaType                   主键字段类型
 * @param pk_comment                    主键备注
 * @returns {string}                    返回字符串模板
 */

/**
 * jdbcTemplate!!!
 * jdbcTemplate 模块特供 !!!
 * jdbcTemplate 模块特供 !!!
 *
 * oracle写法
 */
function generateControl(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function serviceInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function serviceImplement(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function mapperInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var sql_insert = []
    var sql_insert_1 = []
    var sql_insert_2 = []
    var sql_insertAutoIncrement = []
    var sql_insertAutoIncrement_1 = []
    var sql_insertAutoIncrement_2 = []
    var sql_update = []
    var sql_update_1 = []
    var len = columns.length
    for (var i = 0; i < len; i++) {
        sql_insert.push(columns[i].columnName)
        sql_insert_1.push('?')
        sql_insert_2.push(tableName_LOWER_CAMEL + '.get' + columns[i].columnName_UPPER_CAMEL + '()')

        //oracle写法
        sql_insertAutoIncrement.push(columns[i].columnName)
        if (!columns[i].pk) sql_insertAutoIncrement_1.push('?')
        else sql_insertAutoIncrement_1.push('SEQ_' + tableName_UPPER_UNDERSCORE + '.nextval')
        if (!columns[i].pk) sql_insertAutoIncrement_2.push(tableName_LOWER_CAMEL + '.get' + columns[i].columnName_UPPER_CAMEL + '()')

        if (!columns[i].pk) sql_update.push(columns[i].columnName + '=?')
        if (!columns[i].pk) sql_update_1.push(tableName_LOWER_CAMEL + '.get' + columns[i].columnName_UPPER_CAMEL + '()')
    }

    return 'package ' + package + '.mapper;\n\n'
        .appendLine('import ' + package + '.pojo.' + tableName_UPPER_CAMEL + ';\n' +
            'import org.springframework.jdbc.core.JdbcTemplate;\n' +
            'import org.springframework.stereotype.Repository;\n' +
            '\n' +
            'import javax.annotation.Resource;\n' +
            'import java.util.List;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' */')
        .appendLine('@Repository')
        .appendLine('public class ' + tableName_UPPER_CAMEL + 'Mapper {')
        .appendLine('    @Resource\n' +
            '    private JdbcTemplate jdbcTemplate;\n' +
            '\n' +
            '    public List<' + tableName_UPPER_CAMEL + '> selectAll() {\n' +
            '        return jdbcTemplate.query("SELECT * from ' + tableName_UPPER_UNDERSCORE + '", new ' + tableName_UPPER_CAMEL + '());\n' +
            '    }\n' +
            '\n' +
            '    public int insert(' + tableName_UPPER_CAMEL + ' ' + tableName_LOWER_CAMEL + ') {\n' +
            '        return jdbcTemplate.update("INSERT INTO ' + tableName_UPPER_UNDERSCORE + '(' + sql_insert.join(', ') + ')' + ' values(' + sql_insert_1.join(', ') + ')"\n' +
            '                , ' + sql_insert_2.join(', ') + ');\n' +
            '    }\n' +
            '\n' +
            '    public int insertAutoIncrement(' + tableName_UPPER_CAMEL + ' ' + tableName_LOWER_CAMEL + ') {\n' +
            '        return jdbcTemplate.update("INSERT INTO ' + tableName_UPPER_UNDERSCORE + '(' + sql_insertAutoIncrement.join(', ') + ')' + ' values(' + sql_insertAutoIncrement_1.join(', ') + ')"\n' +
            '                , ' + sql_insertAutoIncrement_2.join(', ') + ');\n' +
            '    }\n' +
            '\n' +
            '    public int update(' + tableName_UPPER_CAMEL + ' ' + tableName_LOWER_CAMEL + ') {\n' +
            '        return jdbcTemplate.update("UPDATE ' + tableName_UPPER_UNDERSCORE + ' SET ' + sql_update.join(', ') + ' where ' + pk_name_UPPER_UNDERSCORE + '=?"\n' +
            '                , ' + sql_update_1.join(', ') + '\n' +
            '                , ' + tableName_LOWER_CAMEL + '.get' + pk_name_UPPER_CAMELE + '());\n' +
            '    }\n' +
            '\n' +
            '    public int delete(' + pk_javaType + ' ' + pk_name_LOWER_CAMELE + ') {\n' +
            '        return jdbcTemplate.update("DELETE from ' + tableName_UPPER_UNDERSCORE + ' where ' + pk_name_UPPER_UNDERSCORE + ' = ?"\n' +
            '                , ' + pk_name_LOWER_CAMELE + ');\n' +
            '    }')
        .appendLine('}')
}

function mapperXml(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function generatePojo(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var itemContent = ''
    var func_mapping = '    public static ' + tableName_UPPER_CAMEL + ' mapping(Map<String, Object> map) {'
        .appendLine('        if (map==null) return null;')
        .appendLine('        return new ' + tableName_UPPER_CAMEL + '()')
    var fun_mapRow = '    @Override\n' +
        '    public ' + tableName_UPPER_CAMEL + ' mapRow(ResultSet resultSet, int i) throws SQLException {\n' +
        '        return new ' + tableName_UPPER_CAMEL + '()\n'
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
        if (columns[i].javaDataType === 'Long') func_mapping += '                .set' + columns[i].columnName_UPPER_CAMEL + '((' + 'map.get("' + columns[i].columnName + '") == null ? null : ((BigDecimal) map.get("' + columns[i].columnName + '")).longValue()' + '))\n'
        else func_mapping += '                .set' + columns[i].columnName_UPPER_CAMEL + '((' + columns[i].javaDataType + ') map.get("' + columns[i].columnName + '"))\n'

        if (columns[i].javaDataType === 'Date') fun_mapRow += '                .set' + columns[i].columnName_UPPER_CAMEL + '(resultSet.getTimestamp("' + columns[i].columnName + '"))\n'
        else fun_mapRow += '                .set' + columns[i].columnName_UPPER_CAMEL + '(resultSet.get' + columns[i].javaDataType + '("' + columns[i].columnName + '"))\n'
    }
    func_mapping += '    ;'.appendLine('}')
    fun_mapRow += '    ;'.appendLine('}')
    return 'package ' + package + '.pojo;\n'
        .appendLine('import org.springframework.jdbc.core.RowMapper;\n' +
            '\n' +
            'import java.sql.ResultSet;\n' +
            'import java.sql.SQLException;\n' +
            'import java.util.Date;')
        .appendLine('\n/**\n' +
            ' * ' + table.comment + '\n' +
            ' */')
        .appendLine('public class ' + tableName_UPPER_CAMEL + ' implements RowMapper<' + tableName_UPPER_CAMEL + '> {')
        .appendLine(itemContent)
        .appendLine(fun_mapRow)
        // .appendLine(func_mapping)
        .appendLine(itemGet)
        .appendLine(itemSet)
        .appendLine('}')
}

function generateCustom(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    var fileName = tableName_UPPER_UNDERSCORE.toLowerCase() + '.txt'//这里定义文件名
    var prefix = ''//这里定义id前缀
    var suffix = ''//这里定义id后缀
    var len = columns.length
    var content = ''
    for (var i = 0; i < len; i++) {
        content += '<td class="right">' + columns[i].comments + '</td>\n' +
            '<td id="' + prefix + columns[i].columnName_LOWER_CAMEL + suffix + '"></td>\n\n'
    }

    return {fileName: fileName, content: content}
}

String.prototype.appendLine = function (newLine) {
    return this + '\n' + newLine
}