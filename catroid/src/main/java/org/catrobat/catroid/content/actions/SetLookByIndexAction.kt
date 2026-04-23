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

import android.util.Log
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

open class SetLookByIndexAction : SetLookAction() {
    var formula: Formula? = null
    override var sprite: Sprite?
        get() = super.sprite
        set(value) {
            super.sprite = value
        }
    var scope: Scope? = null

    override fun getEventId(): EventId? {
        try {
            if (sprite != null && scope?.sprite != null && scope?.sequence != null) {
                val lookPosition = formula?.interpretInteger(scope) ?: 0
                lookData = sprite!!.lookList?.getOrNull(lookPosition - 1)
            }
        } catch (e: InterpretationException) {
            Log.d(javaClass.simpleName, "Formula Interpretation for look index failed", e)
        }
        return super.getEventId()
    }
}
