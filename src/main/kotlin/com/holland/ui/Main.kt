package com.holland.ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class Main : Application() {
    override fun start(primaryStage: Stage?) {
        val dbController = primaryStage!!.properties["dbController"]

        val label = Label("新窗口")

        val secondPane = StackPane(label)
        primaryStage.scene = Scene(secondPane, 600.0, 400.0)
        primaryStage.title = "Database Util"
        primaryStage.show()
    }
}