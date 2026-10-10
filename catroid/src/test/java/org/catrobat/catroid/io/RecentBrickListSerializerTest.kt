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
package org.catrobat.catroid.io

import org.catrobat.catroid.common.RecentBricksHolder
import org.catrobat.catroid.content.bricks.FaceNameTrain
import org.catrobat.catroid.content.bricks.NoteBrick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Found on the phone: the recent bricks file still named the removed "Detect face
 * name" brick. Its class no longer exists, so it was loaded as null, the list was
 * saved back with that null, and from then on the script editor crashed on every
 * start (NullPointerException in RecentBrickListManager.isNotBackgroundSpriteBrick).
 * A recent bricks list must never hand out a brick that could not be loaded.
 */
@RunWith(RobolectricTestRunner::class)
class RecentBrickListSerializerTest {

    @get:Rule
    val folder = TemporaryFolder()

    private lateinit var file: File
    private lateinit var serializer: RecentBrickListSerializer

    @Before
    fun setUp() {
        file = File(folder.root, "recent_bricks.json")
        serializer = RecentBrickListSerializer(file)
    }

    @Test
    fun savedBricksAreLoadedBack() {
        saveRecentBricks()

        val bricks = serializer.loadRecentBricks().recentBricks

        assertEquals(listOf(FaceNameTrain::class.java, NoteBrick::class.java), bricks.map { it.javaClass })
    }

    @Test
    fun aNullEntryInTheFileIsDropped() {
        saveRecentBricks()
        insertEntry("null")

        val bricks = serializer.loadRecentBricks().recentBricks

        assertFalse("A recent brick may not be null", bricks.contains(null))
        assertEquals(listOf(FaceNameTrain::class.java, NoteBrick::class.java), bricks.map { it.javaClass })
    }

    @Test
    fun aBrickWhoseClassNoLongerExistsIsDropped() {
        saveRecentBricks()
        insertEntry("""{"type": "org.catrobat.catroid.content.bricks.FaceNameDetect", "properties": {"commentedOut": false}}""")

        val bricks = serializer.loadRecentBricks().recentBricks

        assertFalse("A recent brick may not be null", bricks.contains(null))
        assertEquals(listOf(FaceNameTrain::class.java, NoteBrick::class.java), bricks.map { it.javaClass })
    }

    private fun saveRecentBricks() {
        val holder = RecentBricksHolder()
        holder.insert(NoteBrick())
        holder.insert(FaceNameTrain())
        serializer.saveRecentBricks(holder)
    }

    /** Puts an entry first in the saved list, as the phone's file had it. */
    private fun insertEntry(entry: String) {
        val json = file.readText()
        val start = json.indexOf('[') + 1
        file.writeText(json.substring(0, start) + "\n    " + entry + "," + json.substring(start))
    }
}
