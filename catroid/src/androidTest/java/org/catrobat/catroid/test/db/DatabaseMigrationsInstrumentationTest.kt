/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.db.DatabaseMigrations
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationsInstrumentationTest {

    @get:Rule
    val migrationHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SCHEMA_ASSETS_PATH,
        FrameworkSQLiteOpenHelperFactory()
    )

    @After
    fun cleanUp() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(TEST_DB_NAME)
    }

    @Test
    fun migration2To3_validatesSchemaHashAndAddsPrivateColumn() {
        migrationHelper.createDatabase(TEST_DB_NAME, 2).close()

        val migratedDb = migrationHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            true,
            DatabaseMigrations.MIGRATION_2_3
        )

        migratedDb.use { database ->
            assertTrue("project_response must contain private column after migration", hasColumn(database, "project_response", "private"))
        }
    }

    private fun hasColumn(database: SupportSQLiteDatabase, table: String, column: String): Boolean {
        database.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == column) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private const val TEST_DB_NAME = "database-migration-test"
        private const val SCHEMA_ASSETS_PATH = "schemas/org.catrobat.catroid.db.AppDatabase"
    }
}
