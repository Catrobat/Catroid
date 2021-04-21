/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.common

import org.catrobat.catroid.content.bricks.Brick
import java.io.Serializable
import java.util.ArrayList

class RecentBricksHolder : Serializable {
    val recentBricks: MutableList<Brick>
    fun find(brick: Brick): Int {
        for (i in recentBricks.indices) {
            val b = recentBricks[i]
            if (b.javaClass == brick.javaClass) {
                return i
            }
        }
        return -1
    }

    fun size(): Int {
        return recentBricks.size
    }

    fun remove() {
        recentBricks.removeAt(size() - 1)
    }

    fun remove(index: Int) {
        recentBricks.removeAt(index)
    }

    fun insert(brick: Brick) {
        recentBricks.add(0, brick)
    }

    init {
        recentBricks = ArrayList()
    }
}