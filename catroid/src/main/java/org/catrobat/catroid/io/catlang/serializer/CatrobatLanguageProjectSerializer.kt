/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

import android.content.Context
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils

class CatrobatLanguageProjectSerializer(private val project: Project, private val context: Context) {

    private val languageVersion = "0.1"

    private val programString: StringBuilder = StringBuilder()

    private fun StringBuilder.appendLineIndented(indentionLevel: IndentionLevel, vararg stringsToAppend: String): StringBuilder {
        var builder = append(CatrobatLanguageUtils.getIndention(indentionLevel))
        for (text in stringsToAppend) {
            builder = builder.append(text)
        }
        return builder.appendLine()
    }
    private fun StringBuilder.appendLineIndented(indentionLevel: IndentionLevel, text: Char): StringBuilder = appendLine("${CatrobatLanguageUtils.getIndention(indentionLevel)}$text")

    fun serialize(): String {
        programString.appendLine("#! Catrobat Language Version $languageVersion")
        programString.appendLine("Program ${CatrobatLanguageUtils.formatString(project.name)} {")

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
        programString.appendLineIndented(IndentionLevel.Level_1, "Metadata {")
        programString.appendLineIndented(IndentionLevel.Level_2, "Description: ${CatrobatLanguageUtils.formatString(project.description)},")
        programString.appendLineIndented(IndentionLevel.Level_2, "Catrobat version: ${CatrobatLanguageUtils.formatString("${Constants.CURRENT_CATROBAT_LANGUAGE_VERSION}")},")
        programString.appendLineIndented(IndentionLevel.Level_2, "Catrobat app version: ${CatrobatLanguageUtils.formatString(metadata.applicationVersion)}")
        programString.appendLineIndented(IndentionLevel.Level_1, '}')
    }

    private fun serializeStageData() {
        val metadata = project.xmlHeader
        programString.appendLineIndented(IndentionLevel.Level_1, "Stage {")
        programString.appendLineIndented(IndentionLevel.Level_2, "Landscape mode: ${CatrobatLanguageUtils.formatString(metadata.islandscapeMode().toString())},")
        programString.appendLineIndented(IndentionLevel.Level_2, "Width: ${CatrobatLanguageUtils.formatString(metadata.virtualScreenWidth.toString())},")
        programString.appendLineIndented(IndentionLevel.Level_2, "Height: ${CatrobatLanguageUtils.formatString(metadata.virtualScreenHeight.toString())},")
        programString.appendLineIndented(IndentionLevel.Level_2, "Display mode: ${CatrobatLanguageUtils.formatString(metadata.screenMode.toString())}")
        programString.appendLineIndented(IndentionLevel.Level_1, '}')
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
        programString.appendLineIndented(IndentionLevel.Level_1, "Globals {")
        for (i in globals.indices) {
            var separator = ""
            if (i < globals.size - 1) {
                separator = ","
            }
            programString.appendLineIndented(IndentionLevel.Level_2, globals[i], separator)
        }
        programString.appendLineIndented(IndentionLevel.Level_1, '}')

        if (!project.multiplayerVariables.isNullOrEmpty()) {
            programString.appendLine()
            programString.appendLineIndented(IndentionLevel.Level_1, "Multiplayer variables {")
            for (i in project.multiplayerVariables.indices) {
                var separator = ""
                if (i < project.multiplayerVariables.size - 1) {
                    separator = ","
                }
                programString.appendLineIndented(IndentionLevel.Level_2, CatrobatLanguageUtils.formatVariable(project.multiplayerVariables[i].name), separator)
            }
            programString.appendLineIndented(IndentionLevel.Level_1, '}')
        }
    }

    private fun serializeScenes() {
        for (scene in project.sceneList) {
            programString.appendLineIndented(IndentionLevel.Level_1, "Scene ${CatrobatLanguageUtils.formatString(scene.name)} {")
            if (scene.backgroundSprite != null) {
                programString.appendLineIndented(IndentionLevel.Level_2, "Background {")
                serializeSprite(scene.backgroundSprite)
                programString.appendLineIndented(IndentionLevel.Level_2, '}')
            }

            for (sprite in scene.spriteList) {
                if (sprite == scene.backgroundSprite) {
                    continue
                }
                programString.appendLineIndented(IndentionLevel.Level_2, "Actor or object ${CatrobatLanguageUtils.formatString(sprite.name)} {")
                serializeSprite(sprite)
                programString.appendLineIndented(IndentionLevel.Level_2, '}')
            }

            programString.appendLineIndented(IndentionLevel.Level_1, '}')
        }
    }

    private fun serializeSprite(sprite: Sprite) {
        ProjectManager.getInstance().currentSprite = sprite

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
        programString.appendLineIndented(IndentionLevel.Level_3, "Looks {")
        for (i in sprite.lookList.indices) {
            val lookKeyValuePair = "${CatrobatLanguageUtils.formatLook(sprite.lookList[i].name)}: ${CatrobatLanguageUtils.formatLook(sprite.lookList[i].file.name)}"
            var separator = ""
            if (i < sprite.soundList.size - 1) {
                separator = ","
            }
            programString.appendLineIndented(IndentionLevel.Level_4, lookKeyValuePair, separator)
        }
        programString.appendLineIndented(IndentionLevel.Level_3, '}')
    }

    private fun serializeSounds(sprite: Sprite) {
        if (sprite.soundList.isEmpty()) {
            return
        }
        programString.appendLineIndented(IndentionLevel.Level_3, "Sounds {")
        for (i in sprite.soundList.indices) {
            val soundKeyValuePair = "${CatrobatLanguageUtils.formatSoundName(sprite.soundList[i].name)}: ${CatrobatLanguageUtils.formatSoundName(sprite.soundList[i].file.name)}"
            var separator = ""
            if (i < sprite.soundList.size - 1) {
                separator = ","
            }
            programString.appendLineIndented(IndentionLevel.Level_4, soundKeyValuePair, separator)
        }
        programString.appendLineIndented(IndentionLevel.Level_3, '}')
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

        programString.appendLineIndented(IndentionLevel.Level_3, "Locals {")
        for (i in locals.indices) {
            var separator = ""
            if (i < locals.size - 1) {
                separator = ","
            }
            programString.appendLineIndented(IndentionLevel.Level_4, locals[i], separator)
        }
        programString.appendLineIndented(IndentionLevel.Level_3, '}')
    }

    private fun serializeScripts(sprite: Sprite) {
        if (sprite.scriptList.isEmpty()) {
            return
        }
        programString.appendLineIndented(IndentionLevel.Level_3, "Scripts {")
        for (script in sprite.scriptList) {
            if (script !is UserDefinedScript) {
                programString.append(script.scriptBrick.serializeToCatrobatLanguage(IndentionLevel.Level_4.ordinal + 1))
            }
        }
        programString.appendLineIndented(IndentionLevel.Level_3, '}')
    }

    private fun serializeUserDefinedScripts(sprite: Sprite) {
        if (sprite.userDefinedBrickList.isEmpty()) {
            return
        }
        programString.appendLineIndented(IndentionLevel.Level_3, "User Defined Bricks {")
        for (script in sprite.scriptList) {
            if (script is UserDefinedScript) {
                try {
                    script.scriptBrick.getView(context)
                } catch (_: Exception) {
                }
                programString.append(script.scriptBrick.serializeToCatrobatLanguage(IndentionLevel.Level_4.ordinal + 1))
            }
        }
        programString.appendLineIndented(IndentionLevel.Level_3, '}')
    }
}
