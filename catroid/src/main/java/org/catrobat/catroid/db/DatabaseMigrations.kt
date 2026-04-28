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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    private const val DATABASE_VERSION_1 = 1
    private const val DATABASE_VERSION_2 = 2
    private const val DATABASE_VERSION_3 = 3

    private const val CREATE_FEATURED_PROJECT_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS `featured_project` (`id` TEXT NOT NULL, `project_id` TEXT NOT NULL, `project_url` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `featured_image` TEXT NOT NULL, PRIMARY KEY(`id`))"

    private const val CREATE_PROJECT_CATEGORY_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS `project_category` (`type` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`type`))"

    private const val CREATE_PROJECT_RESPONSE_TABLE_V2_SQL =
        "CREATE TABLE IF NOT EXISTS `project_response` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `version` TEXT NOT NULL, `views` INTEGER NOT NULL, `download` INTEGER NOT NULL, `flavor` TEXT NOT NULL, `tags` TEXT NOT NULL, `uploaded` INTEGER NOT NULL, `uploadedString` TEXT NOT NULL, `screenshotLarge` TEXT NOT NULL, `screenshotSmall` TEXT NOT NULL, `projectUrl` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `fileSize` REAL NOT NULL, `categoryType` TEXT NOT NULL, PRIMARY KEY(`id`, `categoryType`))"

    private const val CREATE_PROJECT_RESPONSE_TABLE_V3_SQL =
        "CREATE TABLE IF NOT EXISTS `project_response` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `version` TEXT NOT NULL, `views` INTEGER NOT NULL, `download` INTEGER NOT NULL, `private` INTEGER NOT NULL, `flavor` TEXT NOT NULL, `tags` TEXT NOT NULL, `uploaded` INTEGER NOT NULL, `uploadedString` TEXT NOT NULL, `screenshotLarge` TEXT NOT NULL, `screenshotSmall` TEXT NOT NULL, `projectUrl` TEXT NOT NULL, `downloadUrl` TEXT NOT NULL, `fileSize` REAL NOT NULL, `categoryType` TEXT NOT NULL, PRIMARY KEY(`id`, `categoryType`))"

    val MIGRATION_1_2 = object : Migration(DATABASE_VERSION_1, DATABASE_VERSION_2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(CREATE_PROJECT_CATEGORY_TABLE_SQL)
            database.execSQL(CREATE_PROJECT_RESPONSE_TABLE_V2_SQL)
        }
    }

    val MIGRATION_2_3 = object : Migration(DATABASE_VERSION_2, DATABASE_VERSION_3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Featured projects are synced from network; rebuilding these cache tables prevents
            // startup crashes for legacy v2 schemas with a mismatching Room identity hash.
            database.execSQL("DROP TABLE IF EXISTS `featured_project`")
            database.execSQL("DROP TABLE IF EXISTS `project_response`")
            database.execSQL("DROP TABLE IF EXISTS `project_category`")

            database.execSQL(CREATE_FEATURED_PROJECT_TABLE_SQL)
            database.execSQL(CREATE_PROJECT_CATEGORY_TABLE_SQL)
            database.execSQL(CREATE_PROJECT_RESPONSE_TABLE_V3_SQL)
        }
    }

}
