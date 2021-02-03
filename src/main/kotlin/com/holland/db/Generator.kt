package com.holland.db

import com.google.common.base.CaseFormat.*
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import java.io.File

object Generator {
    fun generatePojo(path: String, `package`: String, table: TableTemplate, columns: List<ColumnTemplate>) {
        val pojoBuilder = StringBuilder()
        val className = UPPER_UNDERSCORE.to(UPPER_CAMEL, table.name)

        pojoBuilder.append(
            """
        package ${`package`}.pojo;

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
                .append(if ("Date" == it.dataType) "\t@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n" else "")
                .append("\t@Size(max = ${it.charLength}, message = \"${it.columnName} 长度不能大于${it.charLength}\")\n")
                .append("\tprivate ${it.dataType} ${UPPER_UNDERSCORE.to(LOWER_CAMEL, it.columnName)};\n")
        }

        pojoBuilder.append("}")

        FileUtil.newLine2File(
            pojoBuilder.toString(),
            if (path.isEmpty()) "temp" else path + File.separatorChar + `package`,
            "$className.java"
        )
    }
}