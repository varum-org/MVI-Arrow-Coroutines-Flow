package com.vtnd.duynn.data.repository.source.local.api.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by duynn100198 on 3/17/21.
 */
class MigrationManager {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // TODO: Add migration here
            }
        }
    }
}
