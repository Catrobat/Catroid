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
package org.catrobat.catroid.stage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor

class TextActor(private var text: String, private var posX: Int, private var posY: Int) : Actor() {
    private var font: BitmapFont? = null

    init {
        init()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        font!!.draw(batch, text, posX.toFloat(), posY.toFloat())
    }

    private fun init() {
        font = BitmapFont()
        font!!.setColor(1.0f, 0.0f, 0.0f, 1.0f)
    }

    fun setPosX(posX: Int) {
        this.posX = posX
    }

    fun setPosY(posY: Int) {
        this.posY = posY
    }

    fun setText(text: String) {
        this.text = text
    }
}
