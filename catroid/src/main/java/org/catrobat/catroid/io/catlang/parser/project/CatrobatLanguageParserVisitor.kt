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

import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringContent
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToBoolean
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToDouble
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToInt
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageArgumentResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBaseResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageKeyValueResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageListResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageProgramVisitResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageStringResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageVariableResult
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageDoubleDefinitionException
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

class CatrobatLanguageParserVisitor /* : CatrobatLanguageParserVisitor<CatrobatLanguageBaseResult> */{
//    private val currentProject: Project = Project()
//    private var currentScene: Scene? = null
//    private var currentSprite: Sprite? = null
//    private val CatrobatLanguageParserHelper = CatrobatLanguageParserHelper()
//    private val brickList: ArrayList<Brick> = arrayListOf()
//
//    override fun visit(tree: ParseTree?): CatrobatLanguageBaseResult {
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitChildren(node: RuleNode?): CatrobatLanguageBaseResult {
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitTerminal(node: TerminalNode?): CatrobatLanguageBaseResult {
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitErrorNode(node: ErrorNode?): CatrobatLanguageBaseResult {
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitProgram(ctx: CatrobatLanguageParser.ProgramContext?): CatrobatLanguageBaseResult {
//        ctx?.programHeader()?.let { visitProgramHeader(it) }
//        val programBody = ctx?.programBody()
//        if (programBody != null) {
//            return visitProgramBody(programBody)
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitProgramHeader(ctx: CatrobatLanguageParser.ProgramHeaderContext?): CatrobatLanguageBaseResult {
//        if (ctx?.PROGRAM_START() != null) {
//            println("Program start: " + ctx.PROGRAM_START().text)
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitProgramBody(ctx: CatrobatLanguageParser.ProgramBodyContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Program body is null")
//        }
//
//        if (ctx.metadata() == null || ctx.metadata().size != 1) {
//            throw CatrobatLanguageParsingException("Metadata must occur exactly once")
//        } else {
//            visitMetadata(ctx.metadata()[0])
//        }
//
//        if (ctx.stage() == null || ctx.stage().size != 1) {
//            throw CatrobatLanguageParsingException("Stage must occur exactly once")
//        } else {
//            visitStage(ctx.stage()[0])
//        }
//
//        if (ctx.globals() == null || ctx.globals().size != 1) {
//            throw CatrobatLanguageParsingException("Globals must occur exactly once")
//        } else {
//            visitGlobals(ctx.globals()[0])
//        }
//
//        if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size > 1) {
//            throw CatrobatLanguageParsingException("Multiplayer variables must occur at most once")
//        } else if (ctx.multiplayerVariables() != null && ctx.multiplayerVariables().size == 1) {
//            visitMultiplayerVariables(ctx.multiplayerVariables()[0])
//        }
//
//        if (ctx.scene() == null || ctx.scene().size == 0) {
//            throw CatrobatLanguageParsingException("Scene must occur at least once")
//        } else {
//            ctx.scene().forEach {
//                visitScene(it)
//            }
//        }
//        return CatrobatLanguageProgramVisitResult(currentProject)
//    }
//
//    override fun visitMetadata(ctx: CatrobatLanguageParser.MetadataContext?): CatrobatLanguageBaseResult {
//        if (ctx?.metadataContent() != null) {
//            ctx.metadataContent().forEach {
//                visitMetadataContent(it)
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitMetadataContent(ctx: CatrobatLanguageParser.MetadataContentContext?): CatrobatLanguageBaseResult {
//        ctx?.description()?.let { visitDescription(it) }
//        ctx?.catrobatVersion()?.let { visitCatrobatVersion(it) }
//        ctx?.catrobatAppVersion()?.let { visitCatrobatAppVersion(it) }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitDescription(ctx: CatrobatLanguageParser.DescriptionContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.description = it.text
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitCatrobatVersion(ctx: CatrobatLanguageParser.CatrobatVersionContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.catrobatLanguageVersion = getStringToDouble(it.text)
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitCatrobatAppVersion(ctx: CatrobatLanguageParser.CatrobatAppVersionContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.applicationVersion = it.text
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitStage(ctx: CatrobatLanguageParser.StageContext?): CatrobatLanguageBaseResult {
//        if (ctx?.stageContent() != null) {
//            ctx.stageContent().forEach {
//                visitStageContent(it)
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitStageContent(ctx: CatrobatLanguageParser.StageContentContext?): CatrobatLanguageBaseResult {
//        ctx?.landscapeMode()?.let { visitLandscapeMode(it) }
//        ctx?.height()?.let { visitHeight(it) }
//        ctx?.width()?.let { visitWidth(it) }
//        ctx?.displayMode()?.let { visitDisplayMode(it) }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitLandscapeMode(ctx: CatrobatLanguageParser.LandscapeModeContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.setlandscapeMode(getStringToBoolean(it.text))
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitHeight(ctx: CatrobatLanguageParser.HeightContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.setScreenHeight(getStringToInt(it.text))
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitWidth(ctx: CatrobatLanguageParser.WidthContext?): CatrobatLanguageBaseResult {
//        ctx?.STRING()?.let {
//            currentProject.setScreenWidth(getStringToInt(it.text))
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitDisplayMode(ctx: CatrobatLanguageParser.DisplayModeContext?): CatrobatLanguageBaseResult {
//        if (ctx?.STRING() == null) {
//            throw CatrobatLanguageParsingException("Display mode is null")
//        }
//        try {
//            currentProject.screenMode = ScreenModes.valueOf(getStringContent(ctx.STRING().text).toUpperCase())
//        } catch (e: IllegalArgumentException) {
//            throw CatrobatLanguageParsingException("Unknown Display mode: ${ctx.STRING().text}")
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitGlobals(ctx: CatrobatLanguageParser.GlobalsContext?): CatrobatLanguageBaseResult {
//        if (ctx?.variableOrListDeclaration() == null) {
//            throw CatrobatLanguageParsingException("Globals is null")
//        }
//
//        ctx.variableOrListDeclaration().forEach {
//            val result = visitVariableOrListDeclaration(it)
//            if (result is CatrobatLanguageVariableResult) {
//                if (currentProject.getMultiplayerVariable(result.variableName) != null ||
//                    currentProject.getUserVariable(result.variableName) != null
//                ) {
//                    throw CatrobatLanguageDoubleDefinitionException(result.variableName)
//                }
//                currentProject.addUserVariable(UserVariable(result.variableName))
//            } else if (result is CatrobatLanguageListResult) {
//                if (currentProject.getUserList(result.listName) != null) {
//                    throw CatrobatLanguageParsingException("User list already exists: ${result.listName}")
//                }
//                currentProject.addUserList(UserList(result.listName))
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitMultiplayerVariables(ctx: CatrobatLanguageParser.MultiplayerVariablesContext?): CatrobatLanguageBaseResult {
//        if (ctx?.variableDeclaration() == null) {
//            throw CatrobatLanguageParsingException("Multiplayer variables is null")
//        }
//
//        ctx.variableDeclaration().forEach {
//            val result = visitVariableDeclaration(it)
//            if (result is CatrobatLanguageVariableResult) {
//                if (currentProject.getMultiplayerVariable(result.variableName) != null ||
//                    currentProject.getUserVariable(result.variableName) != null
//                ) {
//                    throw CatrobatLanguageDoubleDefinitionException(result.variableName)
//                }
//                currentProject.addMultiplayerVariable(UserVariable(result.variableName))
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitVariableOrListDeclaration(ctx: CatrobatLanguageParser.VariableOrListDeclarationContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Variable or list declaration is null")
//        }
//
//        if (ctx.LIST_REF() != null) {
//            val listName = ctx.LIST_REF().text
//            return CatrobatLanguageListResult(getStringContent(listName))
//        } else if (ctx.variableDeclaration() != null) {
//            return visitVariableDeclaration(ctx.variableDeclaration())
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitVariableDeclaration(ctx: CatrobatLanguageParser.VariableDeclarationContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Variable declaration is null")
//        }
//
//        if (ctx.VARIABLE_REF() != null) {
//            val variableName = ctx.VARIABLE_REF().text
//            return CatrobatLanguageVariableResult(getStringContent(variableName))
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitScene(ctx: CatrobatLanguageParser.SceneContext?): CatrobatLanguageBaseResult {
//        if (ctx?.SCENE() == null) {
//            throw CatrobatLanguageParsingException("Scene is null")
//        }
//        currentScene = Scene(getStringContent(ctx.SCENE().text), currentProject)
//        currentProject.addScene(currentScene)
//
//        if (ctx?.background() == null) {
//            throw CatrobatLanguageParsingException("Background Scene is mandatory")
//        }
//        visitBackground(ctx.background())
//
//        ctx?.actor()?.let {
//            it.forEach() {
//                visitActor(it)
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitBackground(ctx: CatrobatLanguageParser.BackgroundContext?): CatrobatLanguageBaseResult {
//        if (ctx?.actorContent() == null) {
//            throw CatrobatLanguageParsingException("Actor content is null")
//        }
//        currentSprite = Sprite(ctx.BACKGROUND().text)
//        currentSprite!!.look.setZIndex(Constants.Z_INDEX_BACKGROUND)
//        currentScene!!.addSprite(currentSprite!!)
//        // TODO: is not background sprite
//        val backgroundSprite = currentSprite!!.isBackgroundSprite()
//        return visitActorContent(ctx.actorContent())
//    }
//
//    override fun visitActor(ctx: CatrobatLanguageParser.ActorContext?): CatrobatLanguageBaseResult {
//        if (ctx?.actorContent() == null) {
//            throw CatrobatLanguageParsingException("Actor content is null")
//        }
//        currentSprite = Sprite(ctx.STRING()[0].text)
//        currentScene!!.addSprite(currentSprite!!)
//        return visitActorContent(ctx.actorContent())
//    }
//
//    override fun visitActorContent(ctx: CatrobatLanguageParser.ActorContentContext?): CatrobatLanguageBaseResult {
//        if (ctx?.localVariables() != null && ctx.localVariables().size > 1) {
//            throw CatrobatLanguageParsingException("Local variables must occur at most once")
//        } else if (ctx?.localVariables()?.size == 1) {
//            visitLocalVariables(ctx.localVariables()[0])
//        }
//
//        if (ctx?.looks() != null && ctx.looks().size > 1) {
//            throw CatrobatLanguageParsingException("Looks must occur at most once")
//        } else if (ctx?.looks()?.size == 1) {
//            visitLooks(ctx.looks()[0])
//        }
//
//        if (ctx?.sounds() != null && ctx.sounds().size > 1) {
//            throw CatrobatLanguageParsingException("Sounds must occur at most once")
//        } else if (ctx?.sounds()?.size == 1) {
//            visitSounds(ctx.sounds()[0])
//        }
//
//        ctx?.scripts()?.forEach() {
//            visitScripts(it)
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitLocalVariables(ctx: CatrobatLanguageParser.LocalVariablesContext?): CatrobatLanguageBaseResult {
//        if (ctx?.variableOrListDeclaration() == null) {
//            throw CatrobatLanguageParsingException("Local variables is null")
//        }
//        if (currentSprite == null) {
//            throw CatrobatLanguageParsingException("Local variables must be in a sprite")
//        }
//
//        ctx.variableOrListDeclaration().forEach {
//            val result = visitVariableOrListDeclaration(it)
//            if (result is CatrobatLanguageVariableResult) {
//                if (currentSprite!!.getUserVariable(result.variableName) != null) {
//                    throw CatrobatLanguageDoubleDefinitionException(result.variableName)
//                }
//                currentSprite!!.addUserVariable(UserVariable(result.variableName))
//            } else if (result is CatrobatLanguageListResult) {
//                if (currentSprite!!.getUserList(result.listName) != null) {
//                    throw CatrobatLanguageDoubleDefinitionException(result.listName)
//                }
//                currentSprite!!.addUserList(UserList(result.listName))
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitLooks(ctx: CatrobatLanguageParser.LooksContext?): CatrobatLanguageBaseResult {
//        if (ctx?.looksAndSoundsContent() == null) {
//            throw CatrobatLanguageParsingException("Looks content is null")
//        }
//        if (currentSprite == null) {
//            throw CatrobatLanguageParsingException("Looks must be in a sprite")
//        }
//        ctx.looksAndSoundsContent().forEach() {
//            val result = visitLooksAndSoundsContent(it)
//            if (result is CatrobatLanguageKeyValueResult) {
//                val lookName = result.key
//                val fileName = result.value
//                // TODO: Where to get Files from?
////                currentSprite.lookList.add(LookData(lookName, null))
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitSounds(ctx: CatrobatLanguageParser.SoundsContext?): CatrobatLanguageBaseResult {
//        if (ctx?.looksAndSoundsContent() == null) {
//            throw CatrobatLanguageParsingException("Sounds content is null")
//        }
//        if (currentSprite == null) {
//            throw CatrobatLanguageParsingException("Sounds must be in a sprite")
//        }
//        ctx.looksAndSoundsContent().forEach() {
//            val result = visitLooksAndSoundsContent(it)
//            if (result is CatrobatLanguageKeyValueResult) {
//                val soundName = result.key
//                val fileName = result.value
//                // TODO: Where to get Files from?
////                currentSprite.soundList.add(SoundInfo(soundName, File(???)))
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitLooksAndSoundsContent(ctx: CatrobatLanguageParser.LooksAndSoundsContentContext?): CatrobatLanguageBaseResult {
//        if (ctx?.STRING() == null || ctx?.STRING().size != 2) {
//            throw CatrobatLanguageParsingException("Looks or sounds content is null")
//        }
//        return CatrobatLanguageKeyValueResult(getStringContent(ctx.STRING()[0].text), getStringContent(ctx.STRING()[1].text))
//    }
//
//    override fun visitScripts(ctx: CatrobatLanguageParser.ScriptsContext?): CatrobatLanguageBaseResult {
//        ctx?.brick_with_body()?.let {
//            it.forEach() {
//                visitBrick_with_body(it)
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitUserDefinedScripts(ctx: CatrobatLanguageParser.UserDefinedScriptsContext?): CatrobatLanguageBaseResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun visitUserDefinedScript(ctx: CatrobatLanguageParser.UserDefinedScriptContext?): CatrobatLanguageBaseResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun visitBrick_defintion(ctx: CatrobatLanguageParser.Brick_defintionContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Brick definition is null")
//        }
//
//        // TODO: return results
//        if (ctx.NOTE_BRICK() != null) {
//            val noteBrick = NoteBrick(ctx.NOTE_BRICK().text)
//            if (ctx.DISABLED_BRICK_INDICATION() != null) {
//                noteBrick.setCommentedOut(true)
//            }
//        } else if (ctx.brick_invocation() != null) {
//            visitBrick_invocation(ctx.brick_invocation())
//        } else if (ctx.brick_with_body() != null) {
//            visitBrick_with_body(ctx.brick_with_body())
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitUserDefinedBrick(ctx: CatrobatLanguageParser.UserDefinedBrickContext?): CatrobatLanguageBaseResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun visitUserDefinedBrickPart(ctx: CatrobatLanguageParser.UserDefinedBrickPartContext?): CatrobatLanguageBaseResult {
//        TODO("Not yet implemented")
//    }
//
//    override fun visitBrick_with_body(ctx: CatrobatLanguageParser.Brick_with_bodyContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Brick with body is null")
//        }
//
//        val brickName = ctx.BRICK_NAME().text
//        val brickDisabled = ctx.DISABLED_BRICK_INDICATION().size == 2
//        val brickArguments = if (ctx.brick_condition() != null) {
//            visitBrick_condition(ctx.brick_condition()) as CatrobatLanguageArgumentResult
//        } else {
//            null
//        }
//
//        val hasChildren = ctx.brick_defintion()?.let {
//            it.size > 0
//        } ?: false
//        if (hasChildren) {
//            ctx.brick_defintion().forEach {
//                // TODO: store child bricks in current brick script
//                visitBrick_defintion(it)
//            }
//        }
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitBrick_invocation(ctx: CatrobatLanguageParser.Brick_invocationContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Brick invocation is null")
//        }
//
//        val brickName = ctx.BRICK_NAME().text
//        val brickDisabled = ctx.DISABLED_BRICK_INDICATION() != null
//        val brickArguments = if (ctx.brick_condition() != null) {
//            visitBrick_condition(ctx.brick_condition()) as CatrobatLanguageArgumentResult
//        } else {
//            null
//        }
//
//        return CatrobatLanguageBaseResult()
//    }
//
//    override fun visitBrick_condition(ctx: CatrobatLanguageParser.Brick_conditionContext?): CatrobatLanguageBaseResult {
//        if (ctx?.arg_list() == null) {
//            throw CatrobatLanguageParsingException("Argument list is null")
//        }
//        return visitArg_list(ctx.arg_list())
//    }
//
//    override fun visitArg_list(ctx: CatrobatLanguageParser.Arg_listContext?): CatrobatLanguageBaseResult {
//        if (ctx?.argument() == null) {
//            throw CatrobatLanguageParsingException("Argument is null")
//        }
//
//        val arguments = arrayListOf<CatrobatLanguageKeyValueResult>()
//        ctx.argument().forEach {
//            arguments.add(visitArgument(it) as CatrobatLanguageKeyValueResult)
//        }
//
//        return CatrobatLanguageArgumentResult(arguments)
//    }
//
//    override fun visitArgument(ctx: CatrobatLanguageParser.ArgumentContext?): CatrobatLanguageBaseResult {
//        if (ctx?.formula() == null || ctx?.PARAM_MODE_NAME() == null) {
//            throw CatrobatLanguageParsingException("Argument is null")
//        }
//
//        val formulaResult = visitFormula(ctx.formula())
//        val formula = (formulaResult as CatrobatLanguageStringResult).string
//
//        return CatrobatLanguageKeyValueResult(ctx.PARAM_MODE_NAME().text, formula)
//    }
//
//    override fun visitFormula(ctx: CatrobatLanguageParser.FormulaContext?): CatrobatLanguageBaseResult {
//        if (ctx == null) {
//            throw CatrobatLanguageParsingException("Formula is null")
//        }
//
//        var start = ctx.start.startIndex
//        var stop = ctx.stop.stopIndex
//
//        val textInterval = if (stop < start) {
//            Interval(stop, start)
//        } else {
//            Interval(start, stop)
//        }
//
//        val tokenStream = ctx.start.tokenSource.inputStream
//        val formulaWithBracket = tokenStream.getText(textInterval)
//        val formula = getStringContent(formulaWithBracket)
//        return CatrobatLanguageStringResult(formula)
//    }
}