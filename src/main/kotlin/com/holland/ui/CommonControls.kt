package com.holland.ui

import javafx.event.ActionEvent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import kotlin.system.exitProcess

object CommonControls {

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

    private val menus = mapOf(
        Pair(
            "连接", mapOf<String, (ActionEvent?) -> Unit>(
                Pair("新的连接", {
                    Connect().start(Stage())
                }),
                Pair("退出", { exitProcess(1) })
            )
        ), Pair(
            "关于", mapOf<String, (ActionEvent?) -> Unit>(
                Pair("关于", { }),
                Pair("其他", { })
            )
        )
    )
}