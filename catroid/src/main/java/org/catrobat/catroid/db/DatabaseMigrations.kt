/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `project_category` (`type` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`type`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `project_response` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `version` TEXT NOT NULL, `views` INTEGER NOT NULL, `download` INTEGER NOT NULL, `flavor` TEXT NOT NULL, `tags` TEXT NOT NULL, `uploaded` INTEGER NOT NULL, `uploadedString` TEXT NOT NULL, `screenshotLarge` TEXT NOT NULL, `screenshotSmall` TEXT NOT NULL, `projectUrl` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `fileSize` REAL NOT NULL, `categoryType` TEXT NOT NULL, PRIMARY KEY(`id`, `categoryType`))")
        }
    }
}
