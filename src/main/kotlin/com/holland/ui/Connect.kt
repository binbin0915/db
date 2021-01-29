package com.holland.ui

import com.holland.db.DBController
import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage


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

        val btn_connect = Button("connect")
        val hbBtn = HBox(10.0)
        hbBtn.alignment = Pos.BOTTOM_RIGHT
        hbBtn.children.add(btn_connect)
        pane.add(hbBtn, 1, row++)
        btn_connect.onAction = EventHandler {
//            if (RegUtil.hostCheck(text_host.text).not()) {
//                Alert(Alert.AlertType.ERROR).apply {
//                    contentText = "host format error"
//                    show()
//                    return@EventHandler
//                }
//            }
//            if (RegUtil.portCheck(text_port.text).not()) {
//                Alert(Alert.AlertType.ERROR).apply {
//                    contentText = "port format error"
//                    show()
//                    return@EventHandler
//                }
//            }

            try {
//                val dbController = DBController(
//                    choice_database.value.toUpperCase(),
//                    text_host.text,
//                    text_port.text,
//                    text_user.text,
//                    text_password.text
//                )
                // TODO: 2021/1/29 test data
                val dbController = DBController(
                    "ORACLE",
                    "11.101.2.195",
                    "1521",
                    "yb_acd",
                    "yb_acd"
                )
                Runtime.getRuntime().addShutdownHook(Thread {
                    println("release database connection..")
                    dbController.close()
                })
                if (dbController.ping()) {
                    Main().start(Stage()
                        .apply {
                            properties["dbController"] = dbController
                        }
                    )
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

        primaryStage!!.scene = Scene(pane, 270.0, 200.0)
        primaryStage.title = "Database Util"
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(Connect::class.java, *args)
}
