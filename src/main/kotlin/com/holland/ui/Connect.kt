package com.holland.ui

import com.holland.db.DBController
import com.holland.util.RegUtil
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

        val databaseList = ImmutableObservableList("oracle", "mysql")
        val choice_database = ChoiceBox<String>()
        choice_database.setItems(databaseList)
        choice_database.setValue("oracle")
        pane.add(choice_database, 0, row++)

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
        hbBtn.children.addAll(btn_2_code, btn_2_table)
        pane.add(hbBtn, 1, row++)
        btn_2_code.onAction =
            onConnectCode(text_host, text_port, choice_database, text_user, text_password, primaryStage)
        btn_2_table.onAction =
            onConnectTable(text_host, text_port, choice_database, text_user, text_password, primaryStage)

        // TODO: 2021/2/2 测试数据 ORACLE
        choice_database.value = "oracle"
        text_host.text = "11.101.2.195"
        text_port.text = "1521"
        text_user.text = "yb_acd"
        text_password.text = "yb_acd"

        // TODO: 2021/2/2 测试数据 MYSQL
//        choice_database.value = "mysql"
//        text_host.text = "localhost"
//        text_port.text = "3306"
//        text_user.text = "root"
//        text_password.text = "root"

        primaryStage!!.scene = Scene(pane, 270.0, 200.0)
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
                contentText = "host format error"
                show()
                return@EventHandler
            }
        }
        if (RegUtil.portCheck(text_port.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "port format error"
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
                GenerateCode().start(Stage()
                    .apply {
                        properties["dbController"] = dbController
                    }
                )
                primaryStage!!.close()
            } else {
                Alert(Alert.AlertType.ERROR).apply {
                    contentText = "database connect error!"
                    show()
                    return@EventHandler
                }
            }
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
                contentText = "host format error"
                show()
                return@EventHandler
            }
        }
        if (RegUtil.portCheck(text_port.text).not()) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "port format error"
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
                    contentText = "database connect error!"
                    show()
                    return@EventHandler
                }
            }
        } catch (e: Exception) {
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
