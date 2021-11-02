package com.holland.javafx

import com.holland.db.DBController
import com.holland.db.DataSource.MYSQL
import com.holland.db.service.TableTemplate
import com.holland.db.service.impl.mysql.MysqlUtil
import com.holland.db.service.impl.oracle.OracleUtil
import com.holland.util.UiUtil
import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.converter.LongStringConverter

@Suppress("PrivatePropertyName")
class GenerateTable : Application() {
    private lateinit var menu_bar: MenuBar
    private lateinit var list_column: TableView<ColumnTemplate>
    private lateinit var btn_add: Button
    private lateinit var btn_del: Button
    private lateinit var btn_submit: Button
    private lateinit var btn_import: Button
    private lateinit var btn_down: Button
    private lateinit var btn_up: Button
    private lateinit var choice_table: ChoiceBox<String>

    private lateinit var text_table_name: TextField
    private lateinit var text_table_comment: TextField
    private lateinit var box_increment_id: CheckBox

    @Suppress("UNCHECKED_CAST")
    override fun start(primaryStage: Stage?) {
        val dbController = primaryStage!!.properties["dbController"] as DBController
        val pane = FXMLLoader.load<Parent>(javaClass.getResource("/GenerateTable.fxml"))
        primaryStage.scene = Scene(pane, 800.0, 400.0)
        primaryStage.title = "数据库代码生成工具"
        UiUtil.initIcon(primaryStage)
        primaryStage.show()

        menu_bar = pane.lookup("#menu_bar") as MenuBar
        list_column = pane.lookup("#list_column") as TableView<ColumnTemplate>
        btn_add = pane.lookup("#btn_add") as Button
        btn_del = pane.lookup("#btn_del") as Button
        btn_submit = pane.lookup("#btn_submit") as Button
        btn_import = pane.lookup("#btn_import") as Button
        btn_down = pane.lookup("#btn_down") as Button
        btn_up = pane.lookup("#btn_up") as Button
        choice_table = pane.lookup("#choice_table") as ChoiceBox<String>
        text_table_name = pane.lookup("#text_table_name") as TextField
        text_table_comment = pane.lookup("#text_table_comment") as TextField
        box_increment_id = pane.lookup("#box_increment_id") as CheckBox

        UiUtil.initMenu(menu_bar)
        list_column.isEditable = true

        // TODO: 2021/2/5 必须要回车才能有值
        with(TableColumn<ColumnTemplate, Int>("序号")) {
            cellValueFactory = PropertyValueFactory("index")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("字段名")) {
            cellValueFactory = PropertyValueFactory("columnName")
            cellFactory = TextFieldTableCell.forTableColumn()
            onEditCommit =
                EventHandler { t -> t.tableView.items[t.tablePosition.row].columnName = t.newValue }
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, ChoiceBox<String>>("字段类型")) {
            cellValueFactory = PropertyValueFactory("dbDataType")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, Long>("字段长度")) {
            cellValueFactory = PropertyValueFactory("charLength")
            cellFactory = TextFieldTableCell.forTableColumn(LongStringConverter())
            onEditCommit =
                EventHandler { t -> t.tableView.items[t.tablePosition.row].charLength = t.newValue }
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, CheckBox>("是否可空")) {
            cellValueFactory = PropertyValueFactory("nullable")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, CheckBox>("是否主键")) {
            cellValueFactory = PropertyValueFactory("pk")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("默认值")) {
            cellValueFactory = PropertyValueFactory("dataDefault")
            cellFactory = TextFieldTableCell.forTableColumn()
            onEditCommit =
                EventHandler { t -> t.tableView.items[t.tablePosition.row].dataDefault = t.newValue }
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("备注")) {
            cellValueFactory = PropertyValueFactory("comments")
            cellFactory = TextFieldTableCell.forTableColumn()
            onEditCommit =
                EventHandler { t -> t.tableView.items[t.tablePosition.row].comments = t.newValue }
            list_column.columns.add(this)
        }

        val dbTypes = when (dbController.dataSource) {
            MYSQL -> MysqlUtil.dbType2JavaType.keys.toTypedArray()
            else -> OracleUtil.dbType2JavaType.keys.toTypedArray()
        }
        btn_add.onAction = EventHandler {
            list_column.items.add(
                ColumnTemplate(
                    list_column.items.size + 1,
                    "",
                    ChoiceBox(ImmutableObservableList(*dbTypes)),
                    null,
                    CheckBox(),
                    "",
                    "",
                    CheckBox()
                )
            )
        }
        btn_del.onAction = EventHandler {
            list_column.items.remove(list_column.selectionModel.selectedItem)
        }
        btn_submit.onAction = EventHandler {
            if (text_table_name.text == "") {
                Alert(Alert.AlertType.INFORMATION).apply {
                    contentText = "表名不能为空"
                    show()
                }
                return@EventHandler
            }
            if (MYSQL == dbController.dataSource && choice_table.value.isEmpty()) {
                Alert(Alert.AlertType.INFORMATION).apply {
                    contentText = "数据库不能为空"
                    show()
                    return@EventHandler
                }
            }
            dbController.createTable(
                TableTemplate(text_table_name.text, null, text_table_comment.text),
                list_column.items.map {
                    com.holland.db.service.ColumnTemplate(
                        it.columnName,
                        it.dbDataType.value,
                        when (dbController.dataSource) {
                            MYSQL -> MysqlUtil.dbType2JavaType(it.dbDataType.value)
                            else -> OracleUtil.dbType2JavaType(it.dbDataType.value)
                        },
                        it.charLength ?: 0,
                        it.nullable.isSelected,
                        it.dataDefault,
                        it.comments,
                        it.pk.isSelected
                    )
                }, box_increment_id.isSelected,
                if (dbController.dataSource == MYSQL) choice_table.value else null
            )
            Alert(Alert.AlertType.INFORMATION).apply {
                contentText = "表${text_table_name.text} 创建成功"
                show()
            }
        }
        btn_import.onAction = EventHandler {
            // TODO: 2021/2/3 外部导入数据
        }
        btn_down.onAction = EventHandler {
            if (list_column.selectionModel.isEmpty.not()) {
                val selectedIndex = list_column.selectionModel.selectedIndex
                if (selectedIndex < list_column.items.size - 1) {
                    val currentTemplate = list_column.selectionModel.selectedItem
                    currentTemplate.index++
                    val nextTemplate = list_column.items[selectedIndex + 1]
                    nextTemplate.index--
                    list_column.items[selectedIndex] = nextTemplate
                    list_column.items[selectedIndex + 1] = currentTemplate
                }
            }
        }
        btn_up.onAction = EventHandler {
            if (list_column.selectionModel.isEmpty.not()) {
                val selectedIndex = list_column.selectionModel.selectedIndex
                if (selectedIndex > 0) {
                    val currentTemplate = list_column.selectionModel.selectedItem
                    currentTemplate.index--
                    val prefixTemplate = list_column.items[selectedIndex - 1]
                    prefixTemplate.index++
                    list_column.items[selectedIndex] = prefixTemplate
                    list_column.items[selectedIndex - 1] = currentTemplate
                }
            }
        }

        if (MYSQL == dbController.dataSource) {
            choice_table.items.addAll(dbController.fetchDbs())
            choice_table.onAction = EventHandler { dbController.schema = choice_table.value }
        }
    }

    class ColumnTemplate(
        var index: Int,
        var columnName: String,
        var dbDataType: ChoiceBox<String>,
        var charLength: Long?,
        var nullable: CheckBox,
        var dataDefault: String?,
        var comments: String?,
        var pk: CheckBox,
    ) {
        override fun toString(): String {
            return "ColumnTemplate(index=$index, columnName='$columnName', dataType='$dbDataType', charLength=$charLength, nullable=${nullable.isSelected}, dataDefault=$dataDefault, comments=$comments, pk=${pk.isSelected})"
        }
    }
}
