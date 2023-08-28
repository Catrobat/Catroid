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
package org.catrobat.catroid.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.ShowTextLoader.ShowTextParameter
import org.catrobat.catroid.utils.ShowTextUtils
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider
import java.util.Locale

class ShowTextActor : Actor {
    private var textSize: Float
    private var xPosition: Int
    private var yPosition: Int
    private var color: String
    private var variableToShow: UserVariable
    var variableNameToCompare: String
        private set
    private var alignment: Int
    var sprite: Sprite?
        private set
    private var androidStringProvider: AndroidStringProvider?

    constructor(
        userVariable: UserVariable,
        xPosition: Int,
        yPosition: Int,
        relativeSize: Float,
        color: String,
        sprite: Sprite?,
        alignment: Int,
        androidStringProvider: AndroidStringProvider?
    ) {
        this.variableToShow = userVariable
        this.variableNameToCompare = variableToShow.name
        this.xPosition = xPosition
        this.yPosition = yPosition
        this.textSize = ShowTextUtils.DEFAULT_TEXT_SIZE * relativeSize
        this.color = color
        this.sprite = sprite
        this.alignment = alignment
        this.androidStringProvider = androidStringProvider
    }

    constructor(
        userVariable: UserVariable,
        xPosition: Int,
        yPosition: Int,
        relativeSize: Float,
        color: String,
        sprite: Sprite?,
        androidStringProvider: AndroidStringProvider?
    ) {
        this.variableToShow = userVariable
        this.variableNameToCompare = variableToShow.name
        this.xPosition = xPosition
        this.yPosition = yPosition
        this.textSize = ShowTextUtils.DEFAULT_TEXT_SIZE * relativeSize
        this.color = color
        this.sprite = sprite
        this.alignment = DEFAULT_ALIGNMENT
        this.androidStringProvider = androidStringProvider
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawVariables(ProjectManager.getInstance().currentProject.userVariables, batch)
        drawVariables(ProjectManager.getInstance().currentProject.multiplayerVariables, batch)
        drawVariables(sprite?.userVariables, batch)
    }

    override fun clear() {
        unLoadText(ProjectManager.getInstance().currentProject.userVariables)
        unLoadText(ProjectManager.getInstance().currentProject.multiplayerVariables)
        unLoadText(sprite?.userVariables)
        super.clear()
    }

    private fun unload(text: String) {
        if (ProjectManager.getInstance().assetManager.contains(
                text + color,
                ShowText::class.java
            )
        ) {
            ProjectManager.getInstance().assetManager.unload(text + color)
        }
    }

    private fun unLoadText(variableList: List<UserVariable>?) {
        processVariables(variableList, ::unload)
    }

    private fun drawText(batch: Batch): (input: String) -> Unit = {
            text ->
        loadAndDrawText(batch, text, xPosition.toFloat(), yPosition.toFloat(), color)
    }

    private fun drawVariables(variableList: List<UserVariable>?, batch: Batch) {
        processVariables(variableList, drawText(batch))
    }

    private fun processVariables(
        variableList: List<UserVariable>?,
        actionToDrawText: (input: String) -> Unit
    ) {
        if (variableList == null) {
            return
        }
        if (variableToShow.isDummy) {
            actionToDrawText(
                CatroidApplication.getAppContext().getString(R.string.no_variable_selected)
            )
            return
        }

        val variable = variableList.firstOrNull { v -> v.name == variableToShow.name } ?: return
        val variableValueString: String = getVariableValueString(variable)
        if (variableValueString.isEmpty()) {
            return
        }
        if (variable.visible) {
            actionToDrawText(getVariableToDraw(variableValueString))
        }
    }

    private fun getVariableToDraw(variable: String): String {
        return if (ShowTextUtils.isNumberAndInteger(variable)) {
            ShowTextUtils.getStringAsInteger(variable)
        } else {
            variable
        }
    }

    private fun getVariableValueString(variable: UserVariable): String {
        val value = variable.value
        return if (value is Boolean && androidStringProvider != null) {
            androidStringProvider!!.getTrueOrFalse(value)
        } else {
            variable.value.toString()
        }
    }

    private fun loadAndDrawText(batch: Batch, text: String, posX: Float, posY: Float, color: String) {
        val amFileId = text + color + textSize + ShowTextLoader.ID_POSTFIX
        val am = ProjectManager.getInstance().assetManager
        setLoader(am)
        if (!am.isLoaded(amFileId)) {
            val params = ShowTextParameter()
            params.alignment = alignment
            params.text = text
            params.xPosition = xPosition
            params.yPosition = yPosition
            params.color = color
            params.textSize = textSize
            am.load(amFileId, ShowText::class.java, params)
            am.finishLoading()
        }

        val showText = am.get<ShowText>(amFileId)
        if (ShowTextUtils.isValidColorString(color)) {
            val rgb: IntArray = ShowTextUtils.calculateColorRGBs(color.toUpperCase(Locale.getDefault()))
            batch.setColor(
                rgb[0].toFloat() / MAX_COLOR,
                rgb[1].toFloat() / MAX_COLOR,
                rgb[2].toFloat() / MAX_COLOR,
                1f
            )
        }
        val xOffset = ShowTextUtils.DEFAULT_X_OFFSET + showText.canvasWidth
        batch.draw(showText.tex, posX - xOffset, posY - showText.textSizeInPx)
        batch.flush()
    }

    private fun setLoader(am: AssetManager) {
        if (am.getLoader(ShowText::class.java) == null) {
            am.setLoader(ShowText::class.java, ShowTextLoader(AbsoluteFileHandleResolver()))
        }
    }

    fun setPositionX(xPosition: Int) {
        this.xPosition = xPosition
    }

    fun setPositionY(yPosition: Int) {
        this.yPosition = yPosition
    }

    companion object {
        private const val DEFAULT_ALIGNMENT = ShowTextUtils.ALIGNMENT_STYLE_CENTERED
        private const val MAX_COLOR = 255f
    }
}
