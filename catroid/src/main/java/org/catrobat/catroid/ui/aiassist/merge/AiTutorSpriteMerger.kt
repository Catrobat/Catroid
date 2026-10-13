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

package org.catrobat.catroid.ui.aiassist.merge

import org.catrobat.catroid.content.Sprite

object AiTutorSpriteMerger {
    @JvmStatic
    fun preserveOriginalIdentityAndMedia(originalSprite: Sprite, aiSprite: Sprite) {
        aiSprite.name = originalSprite.name

        val aiLookFileNames = aiSprite.lookList.map { it.xstreamFileName }
        originalSprite.lookList
            .filter { it.file != null && it.file.name !in aiLookFileNames }
            .forEach { aiSprite.lookList.add(it) }

        val aiSoundFileNames = aiSprite.soundList.map { it.xstreamFileName }
        originalSprite.soundList
            .filter { it.file != null && it.file.name !in aiSoundFileNames }
            .forEach { aiSprite.soundList.add(it) }
    }
}
