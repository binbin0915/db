package com.holland.ui

import com.google.common.base.CaseFormat
import com.holland.db.DBController
import com.holland.db.DataSource.MYSQL
import com.holland.db.DataSource.POSTGRE
import com.holland.db.service.ColumnTemplate
import com.holland.db.service.TableTemplate
import com.holland.util.FileUtil
import com.holland.util.UiUtil
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage
import java.io.File


@Suppress("PrivatePropertyName")
class GenerateCode : Application() {
    private lateinit var menu_bar: MenuBar
    private lateinit var refresh_table: Button
    private lateinit var choice_table: ChoiceBox<String>
    private lateinit var list_table: ListView<String>
    private lateinit var list_column: TableView<ColumnTemplate>
    private lateinit var text_fe: TextField
    private lateinit var text_be: TextField
    private lateinit var text_package: TextField
    private lateinit var btn_fe: Button
    private lateinit var btn_be: Button

    private lateinit var btn_gr_pojo: Button
    private lateinit var btn_gr_mapper: Button
    private lateinit var btn_gr_service: Button
    private lateinit var btn_gr_control: Button
    private lateinit var btn_gr_be: Button
    private lateinit var btn_gr_fe: Button

    private lateinit var btn_show_params: Button

    private lateinit var tables: List<TableTemplate>
    private var table: TableTemplate? = null
    private lateinit var columns: List<ColumnTemplate>

    @Suppress("UNCHECKED_CAST")
    override fun start(primaryStage: Stage?) {

        val dbController = primaryStage!!.properties["dbController"] as DBController
        val pane = FXMLLoader.load<Parent>(javaClass.getResource("/GenerateCode.fxml"))
        primaryStage.scene = Scene(pane, 700.0, 450.0)
        primaryStage.title = "数据库代码生成工具"
        primaryStage.show()

        menu_bar = pane.lookup("#menu") as MenuBar
        refresh_table = pane.lookup("#refresh_table") as Button
        choice_table = pane.lookup("#choice_table") as ChoiceBox<String>
        list_table = pane.lookup("#list_table") as ListView<String>
        list_column = pane.lookup("#list_column") as TableView<ColumnTemplate>

        text_fe = pane.lookup("#text_fe") as TextField
        text_be = pane.lookup("#text_be") as TextField
        text_package = pane.lookup("#text_package") as TextField
        btn_fe = pane.lookup("#btn_fe") as Button
        btn_be = pane.lookup("#btn_be") as Button

        btn_gr_pojo = pane.lookup("#btn_gr_pojo") as Button
        btn_gr_mapper = pane.lookup("#btn_gr_mapper") as Button
        btn_gr_service = pane.lookup("#btn_gr_service") as Button
        btn_gr_control = pane.lookup("#btn_gr_control") as Button
        btn_gr_be = pane.lookup("#btn_gr_be") as Button
        btn_gr_fe = pane.lookup("#btn_gr_fe") as Button

        btn_show_params = pane.lookup("#btn_show_params") as Button

        with(TableColumn<ColumnTemplate, String>("字段名")) {
            cellValueFactory = PropertyValueFactory("columnName")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("字段类型")) {
            cellValueFactory = PropertyValueFactory("dbDataType")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("字段长度")) {
            cellValueFactory = PropertyValueFactory("charLength")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("是否可空")) {
            cellValueFactory = PropertyValueFactory("nullable")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("默认值")) {
            cellValueFactory = PropertyValueFactory("dataDefault")
            list_column.columns.add(this)
        }
        with(TableColumn<ColumnTemplate, String>("备注")) {
            cellValueFactory = PropertyValueFactory("comments")
            list_column.columns.add(this)
        }

        btn_gr_pojo.onAction =
            EventHandler {
                if (!isChooseTable()) return@EventHandler
                dbController.generatePojo(text_be.text, text_package.text, table!!, columns)
            }
        btn_gr_mapper.onAction =
            EventHandler {
                if (!isChooseTable()) return@EventHandler
                dbController.generateMapper(text_be.text, text_package.text, table!!, columns)
            }
        btn_gr_service.onAction =
            EventHandler {
                if (!isChooseTable()) return@EventHandler
                dbController.generateService(text_be.text, text_package.text, table!!, columns)
            }
        btn_gr_control.onAction =
            EventHandler {
                if (!isChooseTable()) return@EventHandler
                dbController.generateControl(text_be.text, text_package.text, table!!, columns)
            }
        btn_gr_be.onAction =
            EventHandler {
                if (!isChooseTable()) return@EventHandler
                dbController.generateBe(text_be.text, text_package.text, table!!, columns)
            }

        btn_fe.onAction = EventHandler { text_fe.text = UiUtil.openFolderChooser(primaryStage)?.path }
        btn_be.onAction = EventHandler { text_be.text = UiUtil.openFolderChooser(primaryStage)?.path }

        btn_show_params.onAction = EventHandler {
            FileUtil.newFile(run {
                if (!isChooseTable()) return@EventHandler
                """path                            ${text_be.text}
                    |package                       ${text_package.text}
                    |table                         $table
                    |tableName_UPPER_UNDERSCORE    ${table!!.name}
                    |tableName_UPPER_CAMEL         ${
                    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
                        table!!.name)
                }
                    |tableName_LOWER_CAMEL         ${
                    CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,
                        table!!.name)
                }
                """.trimMargin()
                    .let {
                        val pkColumn = columns.firstOrNull { it.pk } ?: columns.getOrNull(0)
                        it + """
                            |
                            |pk_name_LOWER_CAMELE          ${
                            if (null == pkColumn) "key" else CaseFormat.UPPER_UNDERSCORE.to(
                                CaseFormat.LOWER_CAMEL, pkColumn.columnName)
                        }
                            |pk_name_UPPER_UNDERSCORE      ${pkColumn?.columnName ?: "KEY"}
                            |pk_javaType                   ${pkColumn?.javaDataType ?: "String"}
                            |pk_comment                    ${pkColumn?.comments ?: "null"}
                            |columns                       ${
                            columns.map { it.toString() }
                                .reduce { acc, columnTemplate -> "$acc\n                              $columnTemplate" }
                        }
                        """.trimMargin()
                    }
            }, ".${File.separatorChar}temp", "params.txt")
            Runtime.getRuntime()
                .exec(arrayOf("cmd", "/C", "start ${".${File.separatorChar}temp${File.separatorChar}params.txt"}"))
        }

        UiUtil.initMenu(menu_bar)

        tables = dbController.fetchTables().let {
            it.forEach { table -> list_table.items.add(table.name) }
            it
        }

        refresh_table.onAction = EventHandler {
            tables = dbController.fetchTables()
            list_table.items.clear()
            tables.forEach { table -> list_table.items.add(table.name) }
            return@EventHandler
        }

        if (MYSQL == dbController.dataSource) {
            choice_table.items.addAll(dbController.fetchDbs())
            choice_table.onAction = EventHandler {
                dbController.schema = choice_table.value
                refresh_table.fire()
            }
            choice_table.value = "mysql"
            refresh_table.fire()
        }

        if (POSTGRE == dbController.dataSource) {
            choice_table.items.addAll(dbController.fetchDbs())
            choice_table.onAction = EventHandler {
                dbController.schema = choice_table.value
                refresh_table.fire()
            }
            choice_table.value = "public"
            refresh_table.fire()
        }

        list_table.selectionModel.selectedItemProperty().addListener { _, oldValue, newValue ->
            run {
                table = tables.find { it.name == newValue ?: oldValue }!!
                columns = dbController.fetchColumns(table!!.name)
                list_column.items.clear()
                list_column.items.addAll(columns)
            }
        }
    }

    private fun isChooseTable(): Boolean {
        return if (table == null) {
            Alert(Alert.AlertType.ERROR).apply {
                contentText = "未选择表"
                show()
            }
            false
        } else true
    }
}
