/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.content.Project

class ScreenValues private constructor() {
    companion object {
        private const val DEFAULT_SCREEN_WIDTH = 1280
        private const val DEFAULT_SCREEN_HEIGHT = 768

        // CHECKSTYLE DISABLE StaticVariableNameCheck FOR 2 LINES
        var screenWidth = 0
        var screenHeight = 0
        const val CAST_SCREEN_WIDTH = 1280
        const val CAST_SCREEN_HEIGHT = 720
        @JvmStatic
		val aspectRatio: Float
            get() {
                if (screenWidth == 0 || screenHeight == 0) {
                    setToDefaultScreenSize()
                }
                return screenWidth.toFloat() / screenHeight.toFloat()
            }

        @JvmStatic
		fun setToDefaultScreenSize() {
            screenWidth = DEFAULT_SCREEN_WIDTH
            screenHeight = DEFAULT_SCREEN_HEIGHT
        }

        @JvmStatic
		fun getScreenHeightForProject(project: Project): Int {
            return if (project.isCastProject) {
                CAST_SCREEN_HEIGHT
            } else screenHeight
        }

        @JvmStatic
		fun getScreenWidthForProject(project: Project): Int {
            return if (project.isCastProject) {
                CAST_SCREEN_WIDTH
            } else screenWidth
        }
    }

    init {
        throw AssertionError()
    }
}