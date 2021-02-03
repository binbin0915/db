package com.holland.util

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

object UiUtil {
    fun openFolderChooser(primaryStage: Stage): File? {
        val folderChooser = DirectoryChooser()
        folderChooser.title = "选择文件夹"
        // TODO: 2021/2/3 记录上一次打开的位置
//        val lastFile = File("")
//        if (lastFile != null) folderChooser.initialDirectory = lastFile
        val selectedfolder = folderChooser.showDialog(primaryStage)
//        FileWriteUtil.newLine2File(selectedfolder.path,"conf","record.conf")
        return if (selectedfolder != null) {
//            lastFile = selectedfolder
            selectedfolder
        } else null
    }
}