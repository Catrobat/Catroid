/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

class DeleteLookAction : SpriteEventAction() {

    private var lookController = LookController()
    private var xstreamSerializer: XstreamSerializer = XstreamSerializer.getInstance()

    override fun getEventId(): EventId? {
        sprite?.apply {
            val lookData = look?.lookData
            if (lookList.isNullOrEmpty() || lookData == null) {
                return null
            }
            val indexOfLookData = lookList.indexOf(look?.lookData)
            if (indexOfLookData == -1) {
                return null
            }
            val lookDataToDelete = look?.lookData
            setNewLookData(indexOfLookData + 1)
            lookController.delete(lookDataToDelete)
            lookList?.removeAt(indexOfLookData)
            xstreamSerializer.saveProject(ProjectManager.getInstance().currentProject)
        }
        return SetLookEventId(sprite, sprite?.look?.lookData)
    }

    private fun setNewLookData(indexOfLookData: Int) {
        sprite?.apply {
            if (lookList?.size!! > 1) {
                if (lookList?.last() == look?.lookData) {
                    look?.lookData = lookList?.first()
                } else {
                    look?.lookData = lookList?.get(indexOfLookData)
                }
            } else {
                look?.lookData = null
            }
        }
    }

    @VisibleForTesting
    fun setLookController(lookController: LookController) {
        this.lookController = lookController
    }

    @VisibleForTesting
    fun setXStreamSerializer(xstreamSerializer: XstreamSerializer) {
        this.xstreamSerializer = xstreamSerializer
    }
}
