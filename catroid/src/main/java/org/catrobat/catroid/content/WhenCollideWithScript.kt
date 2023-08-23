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

package org.catrobat.catroid.content

import android.content.Context
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.WhenCollideWithBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.WhenConditionEventId
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors

class WhenCollideWithScript : Script() {
    private enum class CollisionType {
        FINGER, EDGE, SPRITE
    }

    private lateinit var collisionObjectName: String

    @XStreamAlias("formula")
    private var formula: Formula? = null

    init {
        setDefaultCollision()
    }

    override fun getScriptBrick(): ScriptBrick {
        if (scriptBrick == null) {
            scriptBrick = WhenCollideWithBrick(this)
        }
        return scriptBrick
    }

    fun getSpriteToCollideWithName(): String {
        if (formula?.containsElement(FormulaElement.ElementType.COLLISION_FORMULA) == true) {
            collisionObjectName = formula!!.formulaTree.value
        }
        return collisionObjectName
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Script {
        val clone = super.clone() as WhenCollideWithScript
        clone.formula = formula?.clone()
        return clone
    }

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet?) {
        formula?.addRequiredResources(requiredResourcesSet)
        for (brick in brickList) {
            brick.addRequiredResources(requiredResourcesSet)
        }
    }

    fun getFormula(): Formula? = formula

    private fun getBackgroundSprite(): Sprite? {
        val thisScene = ProjectManager.getInstance()?.currentlyEditedScene
        return thisScene?.backgroundSprite
    }

    private fun setDefaultCollision() {
        val collisionObject = getBackgroundSprite()
        if (collisionObject != null) {
            formula = Formula(
                getFormulaElement(
                    CollisionType.SPRITE,
                    collisionObject.name
                )
            )
            collisionObjectName = collisionObject.name
        } else {
            val context = ProjectManager.getInstance().applicationContext
            collisionObjectName = context.getString(R.string.default_project_background_name)
            formula = null
        }
    }

    fun setSpriteToCollideWithName(collideWithName: String?) {
        collisionObjectName = collideWithName.toString()
        formula = null
    }

    fun setSpriteToCollideWith(collideWithSprite: Sprite?) {
        if (collideWithSprite != null) {
            formula = Formula(
                getFormulaElement(
                    CollisionType.SPRITE,
                    collideWithSprite.name
                )
            )
            collisionObjectName = collideWithSprite.name.toString()
        } else {
            setDefaultCollision()
        }
    }

    private fun getFormulaElement(choice: CollisionType, name: String?): FormulaElement {
        return when (choice) {
            CollisionType.FINGER -> FormulaElement(
                FormulaElement.ElementType.SENSOR,
                Sensors.COLLIDES_WITH_FINGER.name, null
            )
            CollisionType.EDGE -> FormulaElement(
                FormulaElement.ElementType.SENSOR,
                Sensors.COLLIDES_WITH_EDGE.name, null
            )
            CollisionType.SPRITE -> FormulaElement(
                FormulaElement.ElementType.COLLISION_FORMULA,
                name, null
            )
        }
    }

    override fun createEventId(sprite: Sprite?): EventId {
        val context: Context = ProjectManager.getInstance().applicationContext
        if (collisionObjectName == context.getString(R.string.touches_finger)) {
            formula = Formula(getFormulaElement(CollisionType.FINGER, null))
        } else if (collisionObjectName == context.getString(R.string.touches_edge)) {
            formula = Formula(getFormulaElement(CollisionType.EDGE, null))
        }
        if (formula == null) {
            formula = Formula(getFormulaElement(CollisionType.SPRITE, collisionObjectName))
        }
        return WhenConditionEventId(formula)
    }
}
