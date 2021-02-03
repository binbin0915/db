package com.holland.util

import java.io.File

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

    fun newLine2File(string: String?, path: String, fileName: String) {
        mkdir(path)
        val file = File("$path${File.separatorChar}$fileName")
        when (file.exists()) {
            true -> println("rebuild file: ${file.path}")
            false -> {
                println("create file: ${file.path}")
                file.createNewFile()
            }
        }
        string?.let { file.appendText(it + System.getProperty("line.separator")) }
    }
}