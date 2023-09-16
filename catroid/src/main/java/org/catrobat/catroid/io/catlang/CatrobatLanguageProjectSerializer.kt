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

package org.catrobat.catroid.io.catlang

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite

class CatrobatLanguageProjectSerializer(private val project: Project) {

    private val languageVersion = "0.1"
    private val level1Indention = CatrobatLanguageUtils.getIndention(1)
    private val level2Indention = CatrobatLanguageUtils.getIndention(2)
    private val level3Indention = CatrobatLanguageUtils.getIndention(3)
    private val level4Indention = CatrobatLanguageUtils.getIndention(4)

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
        programString.appendLine("${level1Indention}Metadata {")
        programString.appendLine("${level2Indention}Description: ${CatrobatLanguageUtils.getEscapedString(project.description)}")
        programString.appendLine("${level2Indention}Catrobat version: ${Constants.CURRENT_CATROBAT_LANGUAGE_VERSION}")
        programString.appendLine("${level2Indention}Catrobat app version: ???")
        programString.appendLine("${level1Indention}}")
    }

    private fun serializeStageData() {
        val metadata = project.xmlHeaderMetadata
        programString.appendLine("${level1Indention}Stage {")
        programString.appendLine("${level2Indention}Landscape mode: ${CatrobatLanguageUtils.getEscapedString(metadata.islandscapeMode().toString())}")
        programString.appendLine("${level2Indention}Width: ${CatrobatLanguageUtils.getEscapedString(metadata.virtualScreenWidth.toString())}")
        programString.appendLine("${level2Indention}Height: ${CatrobatLanguageUtils.getEscapedString(metadata.virtualScreenHeight.toString())}")
        programString.appendLine("${level2Indention}Display mode: ${CatrobatLanguageUtils.getEscapedString(metadata.screenMode.toString())}")
        programString.appendLine("${level1Indention}}")
    }

    private fun serializeGlobals() {
        programString.appendLine("${level1Indention}Globals {")

        val globals = arrayListOf<String>()
        for (variable in project.userVariables) {
            globals.add(CatrobatLanguageUtils.formatVariable(variable.name))
        }
        for (list in project.userLists) {
            globals.add(CatrobatLanguageUtils.formatList(list.name))
        }

        for (i in globals.indices) {
            programString.append("${level2Indention}${globals[i]}")
            if (i < globals.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine("${level1Indention}}")
    }

    private fun serializeScenes() {
        for (scene in project.sceneList) {
            programString.appendLine("${level1Indention}Scene ${CatrobatLanguageUtils.getEscapedString(scene.name)} {")
            programString.appendLine("${level2Indention}Background {")
            serializeSprite(scene.backgroundSprite)
            programString.appendLine("${level2Indention}}")

            for (sprite in scene.spriteList) {
                programString.appendLine("${level2Indention}Actor ${CatrobatLanguageUtils.getEscapedString(sprite.name)} {")
                serializeSprite(sprite)
                programString.appendLine("${level2Indention}}")
            }

            programString.appendLine("${level1Indention}}")
        }
    }

    private fun serializeSprite(sprite: Sprite) {
        serializeLooks(sprite)
        serializeSounds(sprite)
        serializeScripts(sprite)
    }

    private fun serializeLooks(sprite: Sprite) {
        if (sprite.lookList.isEmpty()) {
            return
        }
        programString.appendLine("${level3Indention}Looks {")
        for (i in sprite.lookList.indices) {
            programString.append("${level4Indention}${CatrobatLanguageUtils.getEscapedString(sprite.lookList[i].name)}: ${CatrobatLanguageUtils.getEscapedString(sprite.lookList[i].file.name)}")
            if (i < sprite.lookList.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine("${level3Indention}}")
    }

    private fun serializeSounds(sprite: Sprite) {
        if (sprite.soundList.isEmpty()) {
            return
        }
        programString.appendLine("${level3Indention}Sounds {")
        for (i in sprite.soundList.indices) {
            programString.append("${level4Indention}${CatrobatLanguageUtils.getEscapedString(sprite.soundList[i].name)}: ${CatrobatLanguageUtils.getEscapedString(sprite.soundList[i].file.name)}")
            if (i < sprite.soundList.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine("${level3Indention}}")
    }

    private fun serializeLocals(sprite: Sprite) {
        programString.appendLine("${level3Indention}Locals {")

        val locals = arrayListOf<String>()
        for (variable in sprite.userVariables) {
            locals.add(CatrobatLanguageUtils.formatVariable(variable.name))
        }
        for (list in sprite.userLists) {
            locals.add(CatrobatLanguageUtils.formatList(list.name))
        }

        for (i in locals.indices) {
            programString.append("${level4Indention}${locals[i]}")
            if (i < locals.size - 1) {
                programString.append(",")
            }
            programString.appendLine()
        }
        programString.appendLine("${level3Indention}}")
    }

    private fun serializeScripts(sprite: Sprite) {
        if (sprite.scriptList.isEmpty()) {
            return
        }
        programString.appendLine("${level3Indention}Scripts {")
        for (script in sprite.scriptList) {
            programString.appendLine(script.scriptBrick.serializeToCatrobatLanguage(4))
        }
        programString.appendLine("${level3Indention}}")
    }
}