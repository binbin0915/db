package com.holland.util

import java.io.File

//fun main() {
//    val readFile = FileUtil.readFile("C:\\Users\\xd\\Desktop", "result.txt")
//
//    val mutableListOf = mutableListOf("")
//    var i = 0
//    readFile.forEachIndexed { index: Int, s: String ->
//        run {
//            i++
//            if (i == 3) {
//                mutableListOf[mutableListOf.lastIndex] = mutableListOf.last() + ("$s,")
//                mutableListOf.add("")
//                i = 0
//            } else {
//                mutableListOf[mutableListOf.lastIndex] = mutableListOf.last() + ("$s,")
//            }
//        }
//    }
//
//    mutableListOf.forEachIndexed { index: Int, s: String ->
//        run {
//            if (mutableListOf.size - 1 != index) {
//                val split = s.split(",")
//                val name = split[0]
//                val nameCN = split[1]
//                val type = split[2]
//                val type_ = if (type == "char" || type == "varchar2" || type == "CHAR" || type == "Varchar2") {
//                    "String"
//                } else if (type == "date") {
//                    "Date"
//                } else if (type == "number") {
//                    "Long"
//                } else {
//                    type
//                }
//
//                println(
//                    """
//                    /**
//                    * ${nameCN}
//                    */
//                    private ${type_} ${name.toLowerCase()};
//                """.trimIndent()
//                )
//            }
//        }
//    }
//
//    val j = 0
//}

object FileUtil {

    fun mkdir(path: String) {
        val file = File(path)
        when (file.exists()) {
            true -> println("exists directory: $path")
            false -> {
                file.mkdir()
                println("create directory: $path")
            }
        }
    }

    fun newLine2File(content: String?, path: String, fileName: String) {
        mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        when (file.exists()) {
            true -> println("rebuild file: ${file.path}")
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
            }
        }
        content?.let { file.appendText(it + System.getProperty("line.separator")) }
    }

    fun newFile(content: String?, path: String, fileName: String) {
        mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        when (file.exists()) {
            true -> println("rebuild file: ${file.path}")
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
            }
        }
        content?.let { file.writeText(it + System.getProperty("line.separator")) }
    }

    fun readFile(path: String, fileName: String): Array<String> {
        val file = File("$path${File.separatorChar}$fileName")
        return if (file.exists()) {
            file.readLines().toTypedArray()
        } else arrayOf()
    }
}