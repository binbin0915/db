package com.holland.db.service

import com.holland.util.FileUtil
import java.io.File


/**
 * 通过视图生成创建表的sql语句
 */
interface CreateTableByView {
    fun execute(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>): String

    fun doIt(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        execute(path, `package`, table, columns)
            .run {
                FileUtil.newFile(
                    this,
                    ".${File.separatorChar}temp${File.separatorChar}sql",
                    "${table.name}.txt"
                )
                Runtime.getRuntime()
                    .exec(
                        arrayOf(
                            "cmd",
                            "/C",
                            "start ${".${File.separatorChar}temp${File.separatorChar}sql${File.separatorChar}${table.name}.txt"}"
                        )
                    )
            }
    }

}
