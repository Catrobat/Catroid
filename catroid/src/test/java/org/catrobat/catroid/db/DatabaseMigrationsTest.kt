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

package org.catrobat.catroid.db

import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Test
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DatabaseMigrationsTest {

    @Test
    fun `migration 2 to 3 rebuilds featured project cache tables with private column`() {
        val database = mock(SupportSQLiteDatabase::class.java)

        DatabaseMigrations.MIGRATION_2_3.migrate(database)

        val orderedVerifier = inOrder(database)
        orderedVerifier.verify(database).execSQL("DROP TABLE IF EXISTS `featured_project`")
        orderedVerifier.verify(database).execSQL("DROP TABLE IF EXISTS `project_response`")
        orderedVerifier.verify(database).execSQL("DROP TABLE IF EXISTS `project_category`")
        orderedVerifier.verify(database).execSQL(
            "CREATE TABLE IF NOT EXISTS `featured_project` (`id` TEXT NOT NULL, `project_id` TEXT NOT NULL, `project_url` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `featured_image` TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
        orderedVerifier.verify(database).execSQL(
            "CREATE TABLE IF NOT EXISTS `project_category` (`type` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`type`))"
        )
        verify(database).execSQL(contains("CREATE TABLE IF NOT EXISTS `project_response`"))
        verify(database).execSQL(contains("`private` INTEGER NOT NULL"))
    }
}
