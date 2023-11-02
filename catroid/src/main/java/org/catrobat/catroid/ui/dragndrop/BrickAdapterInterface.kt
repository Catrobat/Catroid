/*
 * Catroid: An on-device visual programming system for Android devices
<<<<<<<< HEAD:catroid/src/main/java/org/catrobat/catroid/ui/dragndrop/BrickAdapterInterface.kt
 * Copyright (C) 2010-2022 The Catrobat Team
========
 * Copyright (C) 2010-2023 The Catrobat Team
>>>>>>>> c04672eb2 (IDE-89 Redesign project upload (#4752)):catroid/src/main/java/org/catrobat/catroid/content/actions/CloneAction.kt
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

package org.catrobat.catroid.ui.dragndrop

import org.catrobat.catroid.content.bricks.Brick

interface BrickAdapterInterface {
    fun setItemVisible(position: Int, visible: Boolean)
    fun setAllPositionsVisible()
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun moveItemTo(position: Int, brickToMove: Brick?)
    fun getItem(position: Int): Brick?
    fun getPosition(brick: Brick?): Int
    fun removeItems(items: List<Brick>): Boolean
}
