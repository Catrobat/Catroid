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

package org.catrobat.catroid.io.catlang.serializer

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.UserDefinedScript

class CatrobatLanguageProjectSerializer(private val project: Project) {

    private val languageVersion = "0.1"
    private val level1IndentionLevelEnd = "${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_1)}}"
    private val level2IndentionLevelEnd = "${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}}"
    private val level3IndentionLevelEnd = "${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}}"

    private val programString: StringBuilder = StringBuilder()

    fun serialize(): String {
        programString.appendLine("#! Catrobat Language Version $languageVersion")
        programString.appendLine("Program ${CatrobatLanguageUtils.getEscapedString(project.name)} {")

        serializeMetadata()
        programString.appendLine()
        serializeStageData()
        programString.appendLine()
        serializeGlobals()
        programString.appendLine()
        serializeScenes()

        programString.appendLine("}")
        return programString.toString()
    }

    private fun serializeMetadata() {
        val metadata = project.xmlHeader

        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_1)}Metadata {")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Description: ${CatrobatLanguageUtils.getEscapedString(project.description)},")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Catrobat version: ${CatrobatLanguageUtils.getEscapedString("${Constants.CURRENT_CATROBAT_LANGUAGE_VERSION}")},")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Catrobat app version: ${CatrobatLanguageUtils.getEscapedString(metadata.applicationVersion)}")
        programString.appendLine(level1IndentionLevelEnd)
    }

    private fun serializeStageData() {
        val metadata = project.xmlHeader
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_1)}Stage {")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Landscape mode: ${CatrobatLanguageUtils.getEscapedString(metadata.islandscapeMode().toString())},")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Width: ${CatrobatLanguageUtils.getEscapedString(metadata.virtualScreenWidth.toString())},")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Height: ${CatrobatLanguageUtils.getEscapedString(metadata.virtualScreenHeight.toString())},")
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Display mode: ${CatrobatLanguageUtils.getEscapedString(metadata.screenMode.toString())}")
        programString.appendLine(level1IndentionLevelEnd)
    }

    private fun serializeGlobals() {

        val globals = arrayListOf<String>()
        for (variable in project.userVariables) {
            globals.add(CatrobatLanguageUtils.formatVariable(variable.name))
        }
        for (list in project.userLists) {
            globals.add(CatrobatLanguageUtils.formatList(list.name))
        }
        if (globals.isEmpty()) {
            return
        }
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_1)}Globals {")
        for (i in globals.indices) {
            programString.append("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}${globals[i]}")
            if (i < globals.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine(level1IndentionLevelEnd)
    }

    private fun serializeScenes() {
        for (scene in project.sceneList) {
            programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_1)}Scene ${CatrobatLanguageUtils.getEscapedString(scene.name)} {")
            if (scene.backgroundSprite != null) {
                programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Background {")
                serializeSprite(scene.backgroundSprite)
                programString.appendLine(level2IndentionLevelEnd)
            }

            for (sprite in scene.spriteList) {
                if (sprite == scene.backgroundSprite) {
                    continue
                }
                programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_2)}Actor or object ${CatrobatLanguageUtils.getEscapedString(sprite.name)} {")
                serializeSprite(sprite)
                programString.appendLine(level2IndentionLevelEnd)
            }

            programString.appendLine(level1IndentionLevelEnd)
        }
    }

    private fun serializeSprite(sprite: Sprite) {
        serializeLooks(sprite)
        serializeSounds(sprite)
        serializeLocals(sprite)
        serializeScripts(sprite)
        serializeUserDefinedScripts(sprite)
    }

    private fun serializeLooks(sprite: Sprite) {
        if (sprite.lookList.isEmpty()) {
            return
        }
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}Looks {")
        for (i in sprite.lookList.indices) {
            programString.append("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_4)}${CatrobatLanguageUtils.getEscapedString(sprite.lookList[i].name)}: ${CatrobatLanguageUtils.getEscapedString(sprite.lookList[i].file.name)}")
            if (i < sprite.lookList.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine(level3IndentionLevelEnd)
    }

    private fun serializeSounds(sprite: Sprite) {
        if (sprite.soundList.isEmpty()) {
            return
        }
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}Sounds {")
        for (i in sprite.soundList.indices) {
            programString.append("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_4)}${CatrobatLanguageUtils.getEscapedString(sprite.soundList[i].name)}: ${CatrobatLanguageUtils.getEscapedString(sprite.soundList[i].file.name)}")
            if (i < sprite.soundList.size - 1) {
                programString.append(',')
            }
            programString.appendLine()
        }
        programString.appendLine(level3IndentionLevelEnd)
    }

    private fun serializeLocals(sprite: Sprite) {
        val locals = arrayListOf<String>()
        for (variable in sprite.userVariables) {
            locals.add(CatrobatLanguageUtils.formatVariable(variable.name))
        }
        for (list in sprite.userLists) {
            locals.add(CatrobatLanguageUtils.formatList(list.name))
        }

        if (locals.isEmpty()) {
            return
        }

        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}Locals {")
        for (i in locals.indices) {
            programString.append("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_4)}${locals[i]}")
            if (i < locals.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine(level3IndentionLevelEnd)
    }

    private fun serializeScripts(sprite: Sprite) {
        if (sprite.scriptList.isEmpty()) {
            return
        }
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}Scripts {")
        for (script in sprite.scriptList) {
            if (script !is UserDefinedScript) {
                programString.append(script.scriptBrick.serializeToCatrobatLanguage(IndentionLevel.Level_4.ordinal + 1))
            }
        }
        programString.appendLine(level3IndentionLevelEnd)
    }

    private fun serializeUserDefinedScripts(sprite: Sprite) {
        if (sprite.scriptList.isEmpty()) {
            return
        }
        programString.appendLine("${CatrobatLanguageUtils.getIndention(IndentionLevel.Level_3)}User Defined Bricks {")
        for (script in sprite.scriptList) {
            if (script is UserDefinedScript) {
                programString.append(script.scriptBrick.serializeToCatrobatLanguage(IndentionLevel.Level_4.ordinal + 1))
            }
        }
        programString.appendLine(level3IndentionLevelEnd)
    }
}
