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

package org.catrobat.catroid.content.actions

import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.SetLookEventId
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.recyclerview.controller.LookController

class DeleteLookAction : SingleSpriteEventAction() {

    private var lookController = LookController()
    private lateinit var xStreamSerializer: XstreamSerializer

    override fun getEventId(): EventId? {
        sprite?.apply {
            val lookData = look?.lookData ?: return null
            if (lookData.isWebRequest) {
                setNewLookData(0)
                return SetLookEventId(sprite, sprite?.look?.lookData)
            }
            if (lookList.isNullOrEmpty()) {
                return null
            }
            val indexOfLookData = lookList.indexOf(lookData)
            if (indexOfLookData == -1) {
                return null
            }
            setNewLookData(indexOfLookData + 1)
            lookData.invalidate()
            lookList.removeAt(indexOfLookData)
            if (!::xStreamSerializer.isInitialized) {
                xStreamSerializer = XstreamSerializer.getInstance()
            }
            xStreamSerializer.saveProject(ProjectManager.getInstance().currentProject)
        }
        return SetLookEventId(sprite, sprite?.look?.lookData)
    }

    private fun setNewLookData(indexOfLookData: Int) {
        sprite?.apply {
            if (lookList.isNullOrEmpty()) {
                look.lookData = null
                return
            }
            look?.lookData ?: return
            if (look.lookData.isWebRequest) {
                look.lookData = lookList[indexOfLookData]
            } else if (lookList.size > 1) {
                if (lookList.last() == look.lookData) {
                    look.lookData = lookList.first()
                } else {
                    look.lookData = lookList[indexOfLookData]
                }
            } else {
                look.lookData = null
            }
        }
    }

    @VisibleForTesting
    fun setLookController(lookController: LookController) {
        this.lookController = lookController
    }

    @VisibleForTesting
    fun setXStreamSerializer(xstreamSerializer: XstreamSerializer) {
        this.xStreamSerializer = xstreamSerializer
    }
}
