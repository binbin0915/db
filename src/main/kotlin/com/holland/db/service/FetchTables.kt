package com.holland.db.service

interface FetchTables {
    fun execute(): List<TableTemplate>
}

class TableTemplate {

    var name: String
    var type: String?
    var comment: String?

    constructor() {
        this.name = ""
        this.type = null
        this.comment = null
    }

    constructor(name: String, type: String?, comment: String?) {
        this.name = name
        this.type = type
        this.comment = comment
    }

    override fun toString(): String {
        return "TableTemplate(name='$name', type='$type', comment=$comment)"
    }
}