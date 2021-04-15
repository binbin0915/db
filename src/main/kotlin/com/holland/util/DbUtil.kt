package com.holland.util

import com.google.common.base.CaseFormat
import java.sql.ResultSet

object DbUtil {

    /**
     * @param resultSet     resultSet from sql query
     * @param mappingFrom   what kind of database naming rule
     * @param mappingTo     what kind of pojo naming rule
     * @param clazz         Need NoArgsConstructor
     */
    fun <T> getResult(
        resultSet: ResultSet,
        mappingFrom: CaseFormat,
        mappingTo: CaseFormat,
        clazz: Class<T>
    ): List<T> {
        val result = mutableListOf<T>()
        while (resultSet.next()) {
            val item = clazz.getConstructor().newInstance()
            for (i in 1..resultSet.metaData.columnCount) {
                val columnName = mappingFrom.to(mappingTo, resultSet.metaData.getColumnName(i))
                clazz.getDeclaredField(columnName).run {
                    isAccessible = true
                    set(item, resultSet.getObject(i))
                }
            }
            result.add(item)
        }
        return result.toList()
    }

    /**
     * @param resultSet     resultSet from sql query
     * @param resultMap     convert map, database name to pojo name
     * @param clazz         Need NoArgsConstructor
     */
    fun <T> getResult(
        resultSet: ResultSet,
        resultMap: Map<String, String>,
        clazz: Class<T>
    ): List<T> {
        val result = mutableListOf<T>()
        while (resultSet.next()) {
            val item = clazz.getConstructor().newInstance()
            resultMap.forEach {
                val columnName = it.key
                clazz.getDeclaredField(columnName).run {
                    isAccessible = true
                    set(item, resultSet.getObject(it.value))
                }
            }
            result.add(item)
        }
        return result.toList()
    }
}