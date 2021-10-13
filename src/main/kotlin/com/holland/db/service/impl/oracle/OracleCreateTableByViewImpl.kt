package com.holland.db.service.impl.oracle

import com.holland.db.service.ColumnTemplate
import com.holland.db.service.CreateTableByView
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import java.io.File

class OracleCreateTableByViewImpl : CreateTableByView {
    override fun execute(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        /**
         * template
         *
        create table EXP_BASE
        (
        EXP_ID         NUMBER not null,
        HAS_VIDEO      CHAR(1) default '0',
        EXP_FLOW_STATE VARCHAR2(2),
        CREATE_TIME    DATE default sysdate,
        UPDATE_TIME    DATE default sysdate
        )
        ;
        comment on column EXP_BASE.EXP_TYPE is '异常类型';
        alter table EXP_DATA add constraint PK_EXP_DATA primary key (DATA_ID);
         */
        val list1 = arrayListOf<String>()
        val list2 = arrayListOf<String>()
        var pksql = ""
        columns.forEach {
            list1.add(
                "\t" + it.columnName + " "
                        + when (it.dbDataType) {
                    "DATE", "NUMBER", "TIMESTAMP(6)", "TIMESTAMP" -> it.dbDataType
                    else -> it.dbDataType + "(" + it.charLength + ")"
                }
                        + if (it.nullable) " " else " not null "
                        + if (it.dataDefault == null) " " else " default " + it.dataDefault
            )
            if (it.comments != null) list2.add("""comment on column ${table.name}.${it.columnName} is '${it.comments}';""")

            if (it.pk) pksql =
                """alter table ${table.name} add constraint PK_${table.name} primary key (${it.columnName});"""
        }

        FileUtil.newFile(run {
            """
                |create table ${table.name}
                |(
                |${list1.joinToString(",\n")}
                |)
                |;
                |
                |$pksql
                |
                |${list2.joinToString("\n")}
            """.trimMargin()
        }, ".${File.separatorChar}temp", "temp_sql.txt")
        Runtime.getRuntime()
            .exec(arrayOf("cmd", "/C", "start ${".${File.separatorChar}temp${File.separatorChar}temp_sql.txt"}"))
    }
}