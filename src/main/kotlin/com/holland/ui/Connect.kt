package com.holland.ui

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.holland.db.DBController
import com.holland.util.FileUtil
import com.holland.util.RegUtil
import com.holland.util.TimeUtil
import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage

@Suppress("LocalVariableName")
class Connect : Application() {

    override fun start(primaryStage: Stage?) {
        var row = 0

        val pane = GridPane()
        pane.alignment = Pos.CENTER
        pane.hgap = 10.0
        pane.vgap = 10.0
        pane.padding = Insets(25.0, 25.0, 25.0, 25.0)

        val choice_database = ChoiceBox(ImmutableObservableList("oracle", "mysql"/*, "postgre"*/))
        choice_database.value = "oracle"
        val choice_history = ChoiceBox(ImmutableObservableList("历史记录", *FileUtil.readFile("conf", "db_connect.conf")))
        pane.add(choice_database, 0, row)
        pane.add(choice_history, 1, row++)

        val label_host = Label("host")
        val text_host = TextField()
        pane.add(label_host, 0, row)
        pane.add(text_host, 1, row++)

        val label_port = Label("port")
        val text_port = TextField()
        pane.add(label_port, 0, row)
        pane.add(text_port, 1, row++)

        val label_user = Label("user")
        val text_user = TextField()
        pane.add(label_user, 0, row)
        pane.add(text_user, 1, row++)

        val label_password = Label("password")
        val text_password = TextField()
        pane.add(label_password, 0, row)
        pane.add(text_password, 1, row++)

        val btn_2_code = Button("生成代码")
        val btn_2_table = Button("生成表")
        val hbBtn = HBox(10.0)
        hbBtn.alignment = Pos.BOTTOM_RIGHT
        /**
         * 关闭'生成表'功能
         */
//        hbBtn.children.addAll(btn_2_code, btn_2_table)
        hbBtn.children.addAll(btn_2_code)
        pane.add(hbBtn, 1, row++)
        btn_2_code.onAction =
            onConnectCode(text_host, text_port, choice_database, text_user, text_password, primaryStage)
        btn_2_table.onAction =
            onConnectTable(text_host, text_port, choice_database, text_user, text_password, primaryStage)

        choice_history.apply {
            maxWidth = 80.0
            value = "历史记录"
            onAction = EventHandler {
                val jsonElement = JsonParser.parseString(selectionModel.selectedItem) as JsonObject
                choice_database.value = jsonElement.get("dataSource").asString.toLowerCase()
                text_host.text = jsonElement.get("host").asString
                text_port.text = jsonElement.get("port").asString
                text_user.text = jsonElement.get("user").asString
                text_password.text = jsonElement.get("password").asString
            }
        }

        primaryStage!!.scene = Scene(pane, 330.0, 250.0)
        primaryStage.title = "数据库代码生成工具"
        primaryStage.show()
    }

    private fun onConnectCode(
        text_host: TextField,
        text_port: TextField,
        choice_database: ChoiceBox<String>,
        text_user: TextField,
        text_password: TextField,
        primaryStage: Stage?,
    ): EventHandler<ActionEvent> = EventHandler {
        if (RegUtil.hostCheck(text_host.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "host 为空"
                show()
                return@EventHandler
            }
        }
        if (RegUtil.portCheck(text_port.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "port 为空"
                show()
                return@EventHandler
            }
        }

        try {
            TimeUtil.printMethodTime("创建数据库连接") {
                val dbController = DBController(
                    choice_database.value.toUpperCase(),
                    text_host.text,
                    text_port.text,
                    text_user.text,
                    text_password.text
                )
                Runtime.getRuntime().addShutdownHook(Thread {
                    dbController.close()
                })
                if (dbController.ping()) {
                    GenerateCode().start(Stage()
                        .apply {
                            properties["dbController"] = dbController
                        }
                    )
                    primaryStage!!.close()
                } else {
                    Alert(Alert.AlertType.ERROR).apply {
                        contentText = "数据库连接异常!"
                        show()
                    }
                }
            }
            return@EventHandler
        } catch (e: Exception) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "${e.javaClass.name}: ${e.message}"
                show()
                return@EventHandler
            }
        }
    }

    private fun onConnectTable(
        text_host: TextField,
        text_port: TextField,
        choice_database: ChoiceBox<String>,
        text_user: TextField,
        text_password: TextField,
        primaryStage: Stage?,
    ): EventHandler<ActionEvent> = EventHandler {
        if (RegUtil.hostCheck(text_host.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "host 为空"
                show()
                return@EventHandler
            }
        }
        if (RegUtil.portCheck(text_port.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "port 为空"
                show()
                return@EventHandler
            }
        }

        try {
            val dbController = DBController(
                choice_database.value.toUpperCase(),
                text_host.text,
                text_port.text,
                text_user.text,
                text_password.text
            )
            Runtime.getRuntime().addShutdownHook(Thread {
                dbController.close()
            })
            if (dbController.ping()) {
                GenerateTable().start(Stage()
                    .apply {
                        properties["dbController"] = dbController
                    }
                )
                primaryStage!!.close()
            } else {
                Alert(Alert.AlertType.ERROR).apply {
                    contentText = "数据库连接异常!"
                    show()
                    return@EventHandler
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "${e.javaClass.name}: ${e.message}"
                show()
                return@EventHandler
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Connect::class.java, *args)
}
