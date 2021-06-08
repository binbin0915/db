package com.holland.ui

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.holland.db.DBController
import com.holland.db.DataSource
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

        val pane = GridPane().apply {
            alignment = Pos.CENTER
            hgap = 10.0
            vgap = 10.0
            padding = Insets(25.0, 25.0, 25.0, 25.0)

            val choice_database = ChoiceBox(ImmutableObservableList(*DataSource.values().copyOf()))
            choice_database.value = DataSource.ORACLE
            val choice_history =
                ChoiceBox(ImmutableObservableList("历史记录", *FileUtil.readFile("conf", "db_connect.conf")))
            add(choice_database, 0, row)
            add(choice_history, 1, row++)

            val text_host = TextField()
            add(Label("host"), 0, row)
            add(text_host, 1, row++)

            val text_port = TextField()
            add(Label("port"), 0, row)
            add(text_port, 1, row++)

            val text_user = TextField()
            add(Label("user"), 0, row)
            add(text_user, 1, row++)

            val text_password = TextField()
            add(Label("password"), 0, row)
            add(text_password, 1, row++)

            val text_database = TextField()
            add(Label("database"), 0, row)
            add(text_database, 1, row++)

            val btn_2_code = Button("生成代码")
            val btn_2_table = Button("生成表")
            val hbBtn = HBox(10.0)
            hbBtn.alignment = Pos.BOTTOM_RIGHT
            /**
             * 关闭'生成表'功能
             */
//        hbBtn.children.addAll(btn_2_code, btn_2_table)
            hbBtn.children.addAll(btn_2_code)
            add(hbBtn, 1, row++)

            btn_2_code.onAction =
                onConnectCode(
                    text_host,
                    text_port,
                    choice_database,
                    text_user,
                    text_password,
                    text_database,
                    primaryStage
                )
            btn_2_table.onAction =
                onConnectTable(
                    text_host,
                    text_port,
                    choice_database,
                    text_user,
                    text_password,
                    text_database,
                    primaryStage
                )

            choice_history.apply {
                maxWidth = 80.0
                value = "历史记录"
                onAction = EventHandler {
                    if ("历史记录" == selectionModel.selectedItem) {
                        return@EventHandler
                    }
                    val jsonElement = JsonParser.parseString(selectionModel.selectedItem) as JsonObject
                    choice_database.value =
                        DataSource.values().find { it.name == jsonElement.get("dataSource").asString }
                    text_host.text = jsonElement.get("host").asString
                    text_port.text = jsonElement.get("port").asString
                    text_user.text = jsonElement.get("user").asString
                    text_password.text = jsonElement.get("password").asString
                    text_database.text = jsonElement.get("database").asString
                }
            }
        }

        primaryStage!!.scene = Scene(pane, 330.0, 250.0)
        primaryStage.title = "数据库代码生成工具"
        primaryStage.show()
    }

    private fun onConnectCode(
        text_host: TextField,
        text_port: TextField,
        choice_database: ChoiceBox<DataSource>,
        text_user: TextField,
        text_password: TextField,
        text_database: TextField,
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
                    choice_database.value,
                    text_host.text,
                    text_port.text,
                    text_user.text,
                    text_password.text,
                    text_database.text
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
        choice_database: ChoiceBox<DataSource>,
        text_user: TextField,
        text_password: TextField,
        text_database: TextField,
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
                choice_database.value,
                text_host.text,
                text_port.text,
                text_user.text,
                text_password.text,
                text_database.text
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
