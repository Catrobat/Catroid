/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.visualplacement

import android.content.res.Resources
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.visualplacement.model.Size
import org.koin.java.KoinJavaComponent.inject

class LayoutComputer {
    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    private fun getVirtualScreenSize() = Size(
        projectManager.currentProject.xmlHeader.virtualScreenWidth.toFloat(),
        projectManager.currentProject.xmlHeader.virtualScreenHeight.toFloat(),
    )

    private fun getActualScreenSize() = Size(
        Resources.getSystem().displayMetrics.widthPixels.toFloat(),
        Resources.getSystem().displayMetrics.heightPixels.toFloat(),
    )

    fun getLayoutRatio(): Size {
        val layoutSize = getLayoutSize()
        val virtualScreenSize = getVirtualScreenSize()

        return Size(
            layoutSize.width / virtualScreenSize.width,
            layoutSize.height / virtualScreenSize.height,
        )
    }

    fun getLayoutParams(): ViewGroup.LayoutParams {
        val layoutSize = getLayoutSize()

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        layoutParams.gravity = Gravity.CENTER
        layoutParams.width = layoutSize.width.toInt()
        layoutParams.height = layoutSize.height.toInt()

        return layoutParams
    }

    fun getLayoutSize(): Size {
        val virtualScreenSize = getVirtualScreenSize()
        val actualScreenSize = getActualScreenSize()
        val screenMode = projectManager.currentProject.xmlHeader.getScreenMode()

        val virtualAspectRatio = virtualScreenSize.width / virtualScreenSize.height
        val screenAspectRatio = actualScreenSize.width / actualScreenSize.height

        val xRatio = actualScreenSize.width / virtualScreenSize.width
        val yRatio = actualScreenSize.height / virtualScreenSize.height

        return when {
            screenMode == ScreenModes.MAXIMIZE && virtualAspectRatio < screenAspectRatio -> Size(
                actualScreenSize.height * yRatio / xRatio,
                actualScreenSize.width
            )

            screenMode == ScreenModes.MAXIMIZE && virtualAspectRatio > screenAspectRatio -> Size(
                actualScreenSize.width * xRatio / yRatio,
                actualScreenSize.height,
            )

            screenMode == ScreenModes.MAXIMIZE || screenMode == ScreenModes.STRETCH -> Size(
                actualScreenSize.width,
                actualScreenSize.height,
            )

            else -> throw IllegalArgumentException("$screenMode is an invalid ScreenMode")
        }
    }
}
