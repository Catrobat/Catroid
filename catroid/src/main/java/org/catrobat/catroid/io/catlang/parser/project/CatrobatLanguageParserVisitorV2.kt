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

package org.catrobat.catroid.io.catlang.parser.project

import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBaseResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageListResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageProgramVisitResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageVariableResult
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import java.lang.NumberFormatException

class CatrobatLanguageParserVisitorV2 : CatrobatLanguageParserVisitor<CatrobatLanguageBaseResult> {
    private val project = Project()
    private var currentScene: Scene? = null
    private var currentSprite: Sprite? = null

    override fun visit(tree: ParseTree?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitChildren(node: RuleNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitTerminal(node: TerminalNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitErrorNode(node: ErrorNode?): CatrobatLanguageBaseResult {
        throw NotImplementedError("Not needed.")
    }

    override fun visitProgram(ctx: CatrobatLanguageParser.ProgramContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program definition found.")
        }
        visitProgramHeader(ctx.programHeader())
        visitProgramBody(ctx.programBody())

        return CatrobatLanguageProgramVisitResult(project)
    }

    override fun visitProgramHeader(ctx: CatrobatLanguageParser.ProgramHeaderContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program header found.")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitProgramBody(ctx: CatrobatLanguageParser.ProgramBodyContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid program body found.")
        }
        this.project.name = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)

        if (ctx.metadata() == null || ctx.metadata().size != 1) {
            throw CatrobatLanguageParsingException("Metadata must occur exactly once")
        }

        if (ctx.stage() == null || ctx.stage().size != 1) {
            throw CatrobatLanguageParsingException("Stage must occur exactly once")
        }

        if (ctx.globals() == null || ctx.globals().size != 1) {
            throw CatrobatLanguageParsingException("Globals must occur exactly once")
        }

        if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size > 1) {
            throw CatrobatLanguageParsingException("Multiplayer variables must occur at most once")
        } else if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size == 1) {
            visitMultiplayerVariables(ctx.multiplayerVariables()[0])
        }

        visitMetadata(ctx.metadata()[0])
        visitStage(ctx.stage()[0])
        visitGlobals(ctx.globals()[0])

        return CatrobatLanguageBaseResult()
    }

    private val loadedMetadata = setOf<String>()
    override fun visitMetadata(ctx: CatrobatLanguageParser.MetadataContext?): CatrobatLanguageBaseResult {
        if (ctx?.metadataContent() == null) {
            throw CatrobatLanguageParsingException("No valid metadata found.")
        }
        ctx.metadataContent().forEach {
            visitMetadataContent(it)
        }
        if (loadedMetadata.size != 3) {
            throw CatrobatLanguageParsingException("The following 3 metadata must occur exactly once each: Description, Catrobat version, Catrobat app version")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitMetadataContent(ctx: CatrobatLanguageParser.MetadataContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid metadata content found.")
        }
        if (ctx.description() != null) {
            visitDescription(ctx.description())
        } else if (ctx.catrobatVersion() != null) {
            visitCatrobatVersion(ctx.catrobatVersion())
        } else if (ctx.catrobatAppVersion() != null) {
            visitCatrobatAppVersion(ctx.catrobatAppVersion())
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDescription(ctx: CatrobatLanguageParser.DescriptionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid description found.")
        }
        if (loadedMetadata.contains(ctx.DESCRIPTION().text)) {
            throw CatrobatLanguageParsingException("Description must occur exactly once")
        }
        loadedMetadata.plus(ctx.DESCRIPTION().text)
        project.description = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatVersion(ctx: CatrobatLanguageParser.CatrobatVersionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid Catrobat version found.")
        }
        if (loadedMetadata.contains(ctx.CATROBAT_VERSION().text)) {
            throw CatrobatLanguageParsingException("Catrobat version must occur exactly once")
        }
        loadedMetadata.plus(ctx.CATROBAT_VERSION().text)

        try {
            project.catrobatLanguageVersion = CatrobatLanguageParserHelper.getStringToDouble(ctx.STRING().text)
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Catrobat version must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatAppVersion(ctx: CatrobatLanguageParser.CatrobatAppVersionContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid Catrobat app version found.")
        }
        if (loadedMetadata.contains(ctx.CATRPBAT_APP_VERSION().text)) {
            throw CatrobatLanguageParsingException("Catrobat app version must occur exactly once")
        }
        loadedMetadata.plus(ctx.CATRPBAT_APP_VERSION().text)
        project.applicationVersion = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        return CatrobatLanguageBaseResult()
    }

    private val loadedStage = setOf<String>()
    override fun visitStage(ctx: CatrobatLanguageParser.StageContext?): CatrobatLanguageBaseResult {
        if (ctx?.stageContent() == null) {
            throw CatrobatLanguageParsingException("No valid stage found.")
        }

        ctx.stageContent().forEach {
            visitStageContent(it)
        }

        if (loadedStage.size != 4) {
            throw CatrobatLanguageParsingException("The following 4 stage content must occur exactly once each: Landscape mode, Display mode, Height, Width")
        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitStageContent(ctx: CatrobatLanguageParser.StageContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid stage content found.")
        }
        if (ctx.landscapeMode() != null) {
            visitLandscapeMode(ctx.landscapeMode())
        } else if (ctx.displayMode() != null) {
            visitDisplayMode(ctx.displayMode())
        } else if (ctx.height() != null) {
            visitHeight(ctx.height())
        } else if (ctx.width() != null) {
            visitWidth(ctx.width())
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLandscapeMode(ctx: CatrobatLanguageParser.LandscapeModeContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid landscape mode found.")
        }
        if (loadedStage.contains(ctx.LANDSCAPE_MODE().text)) {
            throw CatrobatLanguageParsingException("Landscape mode must occur exactly once")
        }
        loadedStage.plus(ctx.LANDSCAPE_MODE().text)
        project.setlandscapeMode(CatrobatLanguageParserHelper.getStringToBoolean(ctx.STRING().text))
        return CatrobatLanguageBaseResult()
    }

    override fun visitHeight(ctx: CatrobatLanguageParser.HeightContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid height found.")
        }
        if (loadedStage.contains(ctx.HEIGHT().text)) {
            throw CatrobatLanguageParsingException("Height must occur exactly once")
        }
        loadedStage.plus(ctx.HEIGHT().text)
        try {
            project.setScreenHeight(CatrobatLanguageParserHelper.getStringToInt(ctx.STRING().text))
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Height must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitWidth(ctx: CatrobatLanguageParser.WidthContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid width found.")
        }
        if (loadedStage.contains(ctx.WIDTH().text)) {
            throw CatrobatLanguageParsingException("Width must occur exactly once")
        }
        loadedStage.plus(ctx.WIDTH().text)
        try {
            project.setScreenWidth(CatrobatLanguageParserHelper.getStringToInt(ctx.STRING().text))
        } catch (exception: NumberFormatException) {
            throw CatrobatLanguageParsingException("Width must be a valid numerical value")
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDisplayMode(ctx: CatrobatLanguageParser.DisplayModeContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid display mode found.")
        }
        if (loadedStage.contains(ctx.DISPLAY_MODE().text)) {
            throw CatrobatLanguageParsingException("Display mode must occur exactly once")
        }
        loadedStage.plus(ctx.DISPLAY_MODE().text)

        ScreenModes.values().forEach {
            if (it.name.equals(CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text), ignoreCase = true)) {
                project.screenMode = it
                return CatrobatLanguageBaseResult()
            }
        }

        throw CatrobatLanguageParsingException("Display mode must be one of the following: ${ScreenModes.values().joinToString(", ")}")
    }

    override fun visitGlobals(ctx: CatrobatLanguageParser.GlobalsContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid globals found.")
        }
        if (ctx.variableOrListDeclaration() == null || ctx.variableOrListDeclaration().size == 0) {
            throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
        }
        ctx.variableOrListDeclaration().forEach {
            visitVariableOrListDeclaration(it)
        }

        ctx.variableOrListDeclaration().forEach {
            val result = visitVariableOrListDeclaration(it)
            if (result is CatrobatLanguageListResult) {
                project.userLists.add(UserList(result.listName))
            } else if (result is CatrobatLanguageVariableResult) {
                project.userVariables.add(UserVariable(result.variableName))
            } else {
                throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitMultiplayerVariables(ctx: CatrobatLanguageParser.MultiplayerVariablesContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid multiplayer variables found.")
        }
        ctx.variableDeclaration().forEach {
            val variableName = (visitVariableDeclaration(it) as CatrobatLanguageVariableResult).variableName
            project.multiplayerVariables.add(UserVariable(variableName))
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitVariableOrListDeclaration(ctx: CatrobatLanguageParser.VariableOrListDeclarationContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
        }
        if (ctx.variableDeclaration() != null) {
            return visitVariableDeclaration(ctx.variableDeclaration())
        }
        return CatrobatLanguageListResult(CatrobatLanguageParserHelper.getListName(ctx.LIST_REF().text))
    }

    override fun visitVariableDeclaration(ctx: CatrobatLanguageParser.VariableDeclarationContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid variable declaration found.")
        }
        return CatrobatLanguageVariableResult(CatrobatLanguageParserHelper.getVariableName(ctx.VARIABLE_REF().text));
    }

    override fun visitScene(ctx: CatrobatLanguageParser.SceneContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid scene found.")
        }
        val sceneName = CatrobatLanguageParserHelper.getStringContent(ctx.STRING().text)
        val scene = Scene(sceneName, project)
        currentScene = scene
        project.sceneList.add(scene)

        visitBackground(ctx.background())

        ctx.actor().forEach {
            visitActor(it)
        }

        currentScene = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitBackground(ctx: CatrobatLanguageParser.BackgroundContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid background found.")
        }
        val backgroundSprite = Sprite("Background")
        currentScene!!.spriteList.add(0, backgroundSprite)
        currentSprite = backgroundSprite

        visitActorContent(ctx.actorContent())

        currentSprite = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitActor(ctx: CatrobatLanguageParser.ActorContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid actor found.")
        }
        val sprite = Sprite(CatrobatLanguageParserHelper.getStringContent(ctx.STRING(0).text))
        currentSprite = sprite
        currentScene!!.spriteList.add(sprite)

        visitActorContent(ctx.actorContent())

        currentSprite = null
        return CatrobatLanguageBaseResult()
    }

    override fun visitActorContent(ctx: CatrobatLanguageParser.ActorContentContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid actor content found.")
        }

        if (ctx.localVariables() != null && ctx.localVariables().count() > 1) {
            throw CatrobatLanguageParsingException("Local variables must occur at most once in each actor/object.")
        }
        if (ctx.looks() != null && ctx.looks().count() > 1) {
            throw CatrobatLanguageParsingException("Looks must occur at most once in each actor/object.")
        }
        if (ctx.sounds() != null && ctx.sounds().count() > 1) {
            throw CatrobatLanguageParsingException("Sounds must occur at most once in each actor/object.")
        }
        if (ctx.userDefinedScripts() != null && ctx.userDefinedScripts().count() > 1) {
            throw CatrobatLanguageParsingException("User defined scripts must occur at most once in each actor/object.")
        }

        if (ctx.userDefinedScripts() != null) {
            visitUserDefinedScripts(ctx.userDefinedScripts()[0]);
        }

        if (ctx.localVariables() != null) {
            visitLocalVariables(ctx.localVariables()[0])
        }
        if (ctx.looks() != null) {
            visitLooks(ctx.looks()[0])
        }
        if (ctx.sounds() != null) {
            visitSounds(ctx.sounds()[0])
        }

        if (ctx.scripts() != null) {
            ctx.scripts().forEach {
                visitScripts(it)
            }
        }

        return CatrobatLanguageBaseResult()
    }

    override fun visitLocalVariables(ctx: CatrobatLanguageParser.LocalVariablesContext?): CatrobatLanguageBaseResult {
        if (ctx == null) {
            throw CatrobatLanguageParsingException("No valid local variables found.")
        }
        ctx.variableOrListDeclaration().forEach {
            val result = visitVariableOrListDeclaration(it)
            if (result is CatrobatLanguageListResult) {
                currentSprite!!.userLists.add(UserList(result.listName))
            } else if (result is CatrobatLanguageVariableResult) {
                currentSprite!!.userVariables.add(UserVariable(result.variableName))
            } else {
                throw CatrobatLanguageParsingException("No valid variable or list declaration found.")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLooks(ctx: CatrobatLanguageParser.LooksContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitSounds(ctx: CatrobatLanguageParser.SoundsContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitLooksAndSoundsContent(ctx: CatrobatLanguageParser.LooksAndSoundsContentContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitScripts(ctx: CatrobatLanguageParser.ScriptsContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitUserDefinedScripts(ctx: CatrobatLanguageParser.UserDefinedScriptsContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitUserDefinedScript(ctx: CatrobatLanguageParser.UserDefinedScriptContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_defintion(ctx: CatrobatLanguageParser.Brick_defintionContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitUserDefinedBrick(ctx: CatrobatLanguageParser.UserDefinedBrickContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitUserDefinedBrickPart(ctx: CatrobatLanguageParser.UserDefinedBrickPartContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_with_body(ctx: CatrobatLanguageParser.Brick_with_bodyContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_invocation(ctx: CatrobatLanguageParser.Brick_invocationContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_condition(ctx: CatrobatLanguageParser.Brick_conditionContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitArg_list(ctx: CatrobatLanguageParser.Arg_listContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitArgument(ctx: CatrobatLanguageParser.ArgumentContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }

    override fun visitFormula(ctx: CatrobatLanguageParser.FormulaContext?): CatrobatLanguageBaseResult {
        // TODO: Implement
        return CatrobatLanguageBaseResult()
    }
}