/**
 * 参数解释
 * @param path                          后端路径 || "./test"
 * @param package                       包名
 * @param table                         table对象 {name: String, type: String, comment: String}
 *                                          命名规则 name: UPPER_UNDERSCORE,
 *                                          type = 'TABLE' or 'VIEW'
 * @param columns                       List<Column> {columnName: String, dbDataType: String, javaDataType: String, charLength: Long, nullable: Boolean, dataDefault: String, comments: String, pk: Boolean}
 *                                          命名规则 columnName: UPPER_UNDERSCORE
 *                                          dbDataType: 数据库重字段类型
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
function generateControl(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
        .appendLine('@RestController')
        .appendLine('@RequestMapping("' + tableName_LOWER_CAMEL + '")')
        .appendLine('public class ' + tableName_UPPER_CAMEL + 'Controller {')
        .appendLine('    @Resource')
        .appendLine('    private I' + tableName_UPPER_CAMEL + 'Service ' + tableName_LOWER_CAMEL + 'Service;')
        .appendLine('')
        .appendLine('    @GetMapping("' + pk_name_LOWER_CAMELE + '")')
        .appendLine('    public ' + tableName_UPPER_CAMEL + ' find(@PathVariable("' + pk_name_LOWER_CAMELE + '") ' + pk_javaType + ' ' + pk_name_LOWER_CAMELE + ') {')
        .appendLine('        return ' + tableName_LOWER_CAMEL + 'Service.getModel(' + pk_name_LOWER_CAMELE + ')')
        .appendLine('    }')
        .appendLine('}')
}

function serviceInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
        .appendLine('a')
        .appendLine('b')
}

function serviceImplement(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function mapperInterface(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function mapperXml(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

function generatePojo(path, package, table, columns, tableName_UPPER_UNDERSCORE, tableName_UPPER_CAMEL, tableName_LOWER_CAMEL, pk_name_LOWER_CAMELE, pk_name_UPPER_UNDERSCORE, pk_javaType, pk_comment) {
    return ''
}

String.prototype.appendLine = function (newLine) {
    return this + '\n' + newLine
}