package com.holland.db.service.impl.mysql

import com.holland.db.service.ColumnTemplate
import com.holland.db.service.CreateTableByView
import com.holland.db.service.TableTemplate

class MysqlCreateTableByViewImpl : CreateTableByView {
    override fun execute(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>): String {
        /**
        create table `spring-cloud_gateway`.log
        (
        id int auto_increment
        primary key,
        operate_user varchar(16) null comment '操作人',
        operate_time timestamp null comment '操作时间',
        param varchar(1024) null,
        result int null,
        response varchar(1024) null
        )
        comment '用户信息表';
         */

        val i = columns.map {
            "\t" + it.columnName + " " +
                    when (it.dbDataType.toUpperCase()) {
                        "DATE", "NUMBER","DOUBLE", "TIMESTAMP(6)", "TIMESTAMP","DATETIME" -> it.dbDataType
                        else -> it.dbDataType + "(" + it.charLength + ")"
                    } +
                    if (it.pk) " auto_increment primary key " else " " +
                            if (it.nullable) " " else " not null " +
                                    if (it.dataDefault == null) " " else " default " + it.dataDefault +
                                            if (it.comments == null) " " else " comment '${it.comments}' "
        }


        return """|create table ${table.name}
           |(
           |${i.joinToString(",\n")}
           |)
           |${if (table.comment != null) "comment '${table.comment}'" else ""};
        """.trimMargin()
    }
}