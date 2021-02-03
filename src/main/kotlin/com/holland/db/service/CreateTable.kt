package com.holland.db.service

interface CreateTable {
    fun execute(tableTemplate: TableTemplate, columns: List<ColumnTemplate>,incrementId: Boolean, db: String?)
}
