package com.holland.util

import com.holland.javafx.Connect
import javafx.event.ActionEvent
import javafx.scene.control.Alert
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

object UiUtil {
    fun openFolderChooser(primaryStage: Stage): File? {
        val folderChooser = DirectoryChooser()
        folderChooser.title = "选择文件夹"

        val readFile = FileUtil.readFile4Line("conf", "recent_file.conf")
        val selectedFolder = if (readFile.isEmpty()) {
            folderChooser.showDialog(primaryStage)
        } else {
            val path = File(readFile[0])
            if (path.exists()) folderChooser.initialDirectory = path
            folderChooser.showDialog(primaryStage)
        }

        return if (selectedFolder != null) {
            FileUtil.newFile(selectedFolder.path, "conf", "recent_file.conf")
            selectedFolder
        } else null
    }

    fun initIcon(primaryStage: Stage) {
        try {
            primaryStage.icons.add(Image("./img/Psyduck.jpg"))
        } catch (e: Exception) {

        }
    }

    fun initMenu(menuBar: MenuBar) {
        menus.forEach { (k, v) ->
            val menu = Menu(k)
            v.forEach { (kItem, vItem) ->
                val item = MenuItem(kItem)
                menu.items.add(item.apply { setOnAction { run { vItem.invoke(it) } } })
            }
            menuBar.menus.add(menu)
        }
    }

    private val menus: Map<String, Map<String, (ActionEvent?) -> Unit>>
        get() = mapOf(
            "连接" to mapOf(
                "新的连接" to { Connect().start(Stage()) },
                "退出" to { exitProcess(1) }
            ),
            "关于" to mapOf(
                "关于" to {
                    Alert(Alert.AlertType.INFORMATION).apply {
                        contentText = """|微信: Senor_Zhang
                                         |邮箱: zhn.pop@gmail.com""".trimMargin()
                        show()
                    }
                },
                "版本" to {
                    Alert(Alert.AlertType.INFORMATION).apply {
                        contentText = "版本日期 2021年10月28日"
                        show()
                    }
                }
            )
        )
}