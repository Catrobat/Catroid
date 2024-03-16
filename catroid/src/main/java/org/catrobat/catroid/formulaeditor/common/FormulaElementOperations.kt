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
package org.catrobat.catroid.formulaeditor.common

import android.content.res.Resources
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.sensing.CollisionDetection
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.utils.NumberFormats.Companion.trimTrailingCharacters
import java.lang.Double.valueOf
import kotlin.math.round

object FormulaElementOperations {

    @JvmStatic
    fun equalsDoubleIEEE754(left: Double, right: Double) =
        left.isNaN() && right.isNaN() || !left.isNaN() && !right.isNaN() && left >= right && left <= right

    @JvmStatic
    fun interpretOperatorEqual(left: Any, right: Any): Boolean {
        val leftString = left.toString()
        val rightString = right.toString()
        return try {
            equalsDoubleIEEE754(leftString.toDouble(), rightString.toDouble())
        } catch (_: NumberFormatException) {
            leftString == rightString
        }
    }

    @JvmStatic
    fun tryInterpretDoubleValue(obj: Any): Double {
        return when (obj) {
            is String -> try {
                valueOf(obj)
            } catch (_: NumberFormatException) {
                Double.NaN
            }
            else -> obj as Double
        }
    }

    @JvmStatic
    fun normalizeDegeneratedDoubleValues(value: Any?): Any {
        return when (value) {
            is String,
            is Char -> value
            is Boolean -> Conversions.booleanToDouble(value)
            is Int -> value.toDouble()
            is Double -> when (value) {
                Double.NEGATIVE_INFINITY -> -Double.MAX_VALUE
                Double.POSITIVE_INFINITY -> Double.MAX_VALUE
                else -> value
            }
            else -> 0.0
        }
    }

    @JvmStatic
    fun isInteger(value: Double) = value.isFinite() && value == round(value)

    @JvmStatic
    fun tryGetLookNumber(lookData: LookData?, lookDataList: List<LookData>) =
        lookData?.let { lookDataList.indexOf(it) + 1.0 } ?: 1.0

    @JvmStatic
    fun getAllClones(sprite: Sprite, stageListener: StageListener?): List<Sprite> {
        return stageListener?.let {
            listOf(sprite) + it.getAllClonesOfSprite(sprite)
        } ?: listOf(sprite)
    }

    @JvmStatic
    fun interpretUserList(userList: UserList?): Any {
        return userList?.let {
            when {
                it.value.isEmpty() -> ""
                it.value.size == 1 -> it.value[0]
                else -> interpretMultipleItemsUserList(it.value)
            }
        } ?: Conversions.FALSE
    }

    private fun booleanToLocalizedString(value: Boolean): String {
        return if (value) {
            CatroidApplication.getAppContext().getString(R.string.formula_editor_true)
        } else {
            CatroidApplication.getAppContext().getString(R.string.formula_editor_false)
        }
    }

    private fun interpretMultipleItemsUserList(userListValues: List<Any>): Any {
        val userListStringValues = userListValues.map {
            trimTrailingCharacters(
                when (it) {
                    is Int -> it.toString()
                    is Double -> it.toString()
                    is Boolean -> booleanToLocalizedString(it)
                    is Char -> it.toString()
                    else -> it as String
                }
            )
        }
        val stringBuilder = StringBuilder(userListStringValues.size)
        val separator = if (listConsistsOfSingleCharacters(userListStringValues)) "" else " "
        for (userListStringValue in userListStringValues) {
            stringBuilder.append(trimTrailingCharacters(userListStringValue))
            stringBuilder.append(separator)
        }
        return stringBuilder.toString().trim { it <= ' ' }
    }

    private fun listConsistsOfSingleCharacters(userListStringValues: List<String>) =
        userListStringValues.none { it.length > 1 }

    @JvmStatic
    fun interpretUserVariable(userVariable: UserVariable?) =
        userVariable?.value ?: Conversions.FALSE

    @JvmStatic
    fun interpretUserDefinedBrickInput(userDefinedBrickInput: UserData<Any>?): Any {
        return userDefinedBrickInput?.let {
            when {
                it is UserVariable -> interpretUserVariable(it)
                it is UserList -> interpretUserList(it)
                else -> 0
            }
        } ?: Conversions.FALSE
    }

    @JvmStatic
    fun interpretLookCollision(look: Look, looks: List<Look>): Double {
        val collides = looks.any {
            look != it && CollisionDetection.checkCollisionBetweenLooks(look, it)
        }
        return Conversions.booleanToDouble(collides)
    }

    @JvmStatic
    fun toLooks(sprites: List<Sprite>) = sprites.map { it.look }

    @JvmStatic
    fun interpretSensor(
        sprite: Sprite,
        currentlyEditedScene: Scene,
        currentProject: Project,
        value: String?
    ): Any {
        SensorHandler.setObjectData(sprite, currentlyEditedScene, currentProject)
        val sensor = Sensors.getSensorByValue(value)
        return sensor.getSensor().getSensorValue()
    }

    @JvmStatic
    fun tryFindSprite(scene: Scene, spriteName: String?): Sprite? {
        return try {
            scene.getSprite(spriteName)
        } catch (_: Resources.NotFoundException) {
            null
        }
    }

    @JvmStatic
    fun interpretCollision(
        firstLook: Look,
        secondSpriteName: String?,
        currentlyPlayingScene: Scene,
        stageListener: StageListener?
    ): Double {
        val secondSprite =
            tryFindSprite(currentlyPlayingScene, secondSpriteName) ?: return Conversions.FALSE
        val sprites = when (secondSprite) {
            is GroupSprite -> GroupSprite.getSpritesFromGroupWithGroupName(
                secondSpriteName,
                currentlyPlayingScene.spriteList
            )
            else -> getAllClones(secondSprite, stageListener)
        }
        return interpretLookCollision(firstLook, toLooks(sprites))
    }

    @Suppress("TooGenericExceptionCaught")
    @JvmStatic
    fun tryInterpretCollision(
        firstLook: Look,
        secondSpriteName: String?,
        currentlyPlayingScene: Scene,
        stageListener: StageListener?
    ): Double {
        return try {
            interpretCollision(firstLook, secondSpriteName, currentlyPlayingScene, stageListener)
        } catch (_: Exception) {
            Conversions.FALSE
        }
    }

    @JvmStatic
    fun tryInterpretElementRecursive(element: FormulaElement, scope: Scope?):
        Any {
        return try {
            element.interpretRecursive(scope)
        } catch (numberFormatException: NumberFormatException) {
            Double.NaN
        }
    }

    @JvmStatic
    fun tryParseIntFromObject(value: Any): Int {
        return when (value) {
            is String -> tryParseIntFromString(value)
            else -> (value as Double).toInt()
        }
    }

    @JvmStatic
    private fun tryParseIntFromString(value: String): Int {
        return try {
            valueOf(value).toInt()
        } catch (_: NumberFormatException) {
            0
        }
    }
}
