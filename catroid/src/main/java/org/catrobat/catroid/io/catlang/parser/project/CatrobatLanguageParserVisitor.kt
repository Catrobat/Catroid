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
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringContent
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToBoolean
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToDouble
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParserHelper.Companion.getStringToInt
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageBaseResult
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageVisitResult
import org.catrobat.catroid.io.catlang.parser.project.error.CatribatLanguageParsingException

class CatrobatLanguageParserVisitor : CatrobatLanguageParserVisitor<CatrobatLanguageBaseResult> {
    private val currentProject: Project = Project()
    private val CatrobatLanguageParserHelper = CatrobatLanguageParserHelper()
    private val brickList: ArrayList<Brick> = arrayListOf()

    override fun visit(tree: ParseTree?): CatrobatLanguageBaseResult {
        return CatrobatLanguageBaseResult()
    }

    override fun visitChildren(node: RuleNode?): CatrobatLanguageBaseResult {
        return CatrobatLanguageBaseResult()
    }

    override fun visitTerminal(node: TerminalNode?): CatrobatLanguageBaseResult {
        return CatrobatLanguageBaseResult()
    }

    override fun visitErrorNode(node: ErrorNode?): CatrobatLanguageBaseResult {
        return CatrobatLanguageBaseResult()
    }

    override fun visitProgram(ctx: CatrobatLanguageParser.ProgramContext?): CatrobatLanguageBaseResult {
        ctx?.programHeader()?.let { visitProgramHeader(it) }
        val programBody = ctx?.programBody()
        if (programBody != null) {
            return visitProgramBody(programBody)
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitProgramHeader(ctx: CatrobatLanguageParser.ProgramHeaderContext?): CatrobatLanguageBaseResult {
        if (ctx?.PROGRAM_START() != null) {
            println("Program start: " + ctx.PROGRAM_START().text)
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitProgramBody(ctx: CatrobatLanguageParser.ProgramBodyContext?): CatrobatLanguageBaseResult {
        ctx?.metadata()?.let { visitMetadata(it) }
        ctx?.stage()?.let { visitStage(it) }

        ctx?.scene()?.let {
            it.forEach {
                visitScene(it)
            }
        }
        return CatrobatLanguageVisitResult(currentProject)
    }

    override fun visitMetadata(ctx: CatrobatLanguageParser.MetadataContext?): CatrobatLanguageBaseResult {
        if (ctx?.metadataContent() != null) {
            ctx.metadataContent().forEach {
                visitMetadataContent(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitMetadataContent(ctx: CatrobatLanguageParser.MetadataContentContext?): CatrobatLanguageBaseResult {
        ctx?.description()?.let { visitDescription(it) }
        ctx?.catrobatVersion()?.let { visitCatrobatVersion(it) }
        ctx?.catrobatAppVersion()?.let { visitCatrobatAppVersion(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDescription(ctx: CatrobatLanguageParser.DescriptionContext?): CatrobatLanguageBaseResult {
        currentProject.description = ctx.toString()
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatVersion(ctx: CatrobatLanguageParser.CatrobatVersionContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            currentProject.catrobatLanguageVersion = getStringToDouble(it.text)
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitCatrobatAppVersion(ctx: CatrobatLanguageParser.CatrobatAppVersionContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            currentProject.applicationVersion = it.text
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitStage(ctx: CatrobatLanguageParser.StageContext?): CatrobatLanguageBaseResult {
        if (ctx?.stageContent() != null) {
            ctx.stageContent().forEach {
                visitStageContent(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitStageContent(ctx: CatrobatLanguageParser.StageContentContext?): CatrobatLanguageBaseResult {
        ctx?.landscapeMode()?.let { visitLandscapeMode(it) }
        ctx?.height()?.let { visitHeight(it) }
        ctx?.width()?.let { visitWidth(it) }
        ctx?.displayMode()?.let { visitDisplayMode(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLandscapeMode(ctx: CatrobatLanguageParser.LandscapeModeContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            currentProject.setlandscapeMode(getStringToBoolean(it.text))
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitHeight(ctx: CatrobatLanguageParser.HeightContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            currentProject.setScreenHeight(getStringToInt(it.text))
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitWidth(ctx: CatrobatLanguageParser.WidthContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            currentProject.setScreenWidth(getStringToInt(it.text))
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitDisplayMode(ctx: CatrobatLanguageParser.DisplayModeContext?): CatrobatLanguageBaseResult {
        ctx?.STRING()?.let {
            try {
                currentProject.screenMode = ScreenModes.valueOf(getStringContent(it.text).toUpperCase())
            } catch (e: IllegalArgumentException) {
                throw CatribatLanguageParsingException("Unknown screen mode: ${it.text}")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitGlobals(ctx: CatrobatLanguageParser.GlobalsContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitMultiplayerVariables(ctx: CatrobatLanguageParser.MultiplayerVariablesContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitVariableDeclaration(ctx: CatrobatLanguageParser.VariableDeclarationContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitScene(ctx: CatrobatLanguageParser.SceneContext?): CatrobatLanguageBaseResult {
        ctx?.background()?.let { visitBackground(it) }
        ctx?.actor()?.let {
            it.forEach() {
                visitActor(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBackground(ctx: CatrobatLanguageParser.BackgroundContext?): CatrobatLanguageBaseResult {
        ctx?.actorContent()?.let { visitActorContent(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitActor(ctx: CatrobatLanguageParser.ActorContext?): CatrobatLanguageBaseResult {
        ctx?.actorContent()?.let { visitActorContent(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitActorContent(ctx: CatrobatLanguageParser.ActorContentContext?): CatrobatLanguageBaseResult {

        ctx?.scripts()?.let {
            it.forEach() {
                visitScripts(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitLocalVariables(ctx: CatrobatLanguageParser.LocalVariablesContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitLooks(ctx: CatrobatLanguageParser.LooksContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitSounds(ctx: CatrobatLanguageParser.SoundsContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitLooksAndSoundsContent(ctx: CatrobatLanguageParser.LooksAndSoundsContentContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }

    override fun visitScripts(ctx: CatrobatLanguageParser.ScriptsContext?): CatrobatLanguageBaseResult {
        ctx?.brick_with_body()?.let {
            it.forEach() {
                visitBrick_with_body(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_defintion(ctx: CatrobatLanguageParser.Brick_defintionContext?): CatrobatLanguageBaseResult {
        ctx?.brick_with_body()?.let { visitBrick_with_body(it) }
        ctx?.brick_invocation()?.let { visitBrick_invocation(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_with_body(ctx: CatrobatLanguageParser.Brick_with_bodyContext?): CatrobatLanguageBaseResult {
        ctx?.brick_condition()?.let { visitBrick_condition(it) }
        ctx?.brick_defintion()?.let {
            it.forEach() {
                visitBrick_defintion(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_invocation(ctx: CatrobatLanguageParser.Brick_invocationContext?): CatrobatLanguageBaseResult {
        ctx?.brick_condition()?.let { visitBrick_condition(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitBrick_condition(ctx: CatrobatLanguageParser.Brick_conditionContext?): CatrobatLanguageBaseResult {
        ctx?.arg_list()?.let { visitArg_list(it) }
        return CatrobatLanguageBaseResult()
    }

    override fun visitArg_list(ctx: CatrobatLanguageParser.Arg_listContext?): CatrobatLanguageBaseResult {
        ctx?.argument()?.let {
            it.forEach() {
                visitArgument(it)
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitArgument(ctx: CatrobatLanguageParser.ArgumentContext?): CatrobatLanguageBaseResult {
        if (ctx?.formula() != null) {
            ctx.PARAM_MODE_NAME().let { println("Param name: " + it.text) }
            val start = ctx.start.startIndex
            val stop = ctx.stop.stopIndex
            val tokenStream = ctx.start.tokenSource.inputStream
            val textInterval = Interval(start, stop)
            try {
                val parameterPlusValue = tokenStream.getText(textInterval)
                val startIndexOfParameterValue = parameterPlusValue.indexOf('(') + 1
                val valueWithoutParentheses = parameterPlusValue.substring(startIndexOfParameterValue, parameterPlusValue.length - 1)
                println("Param value: " + valueWithoutParentheses)
            } catch (e: Exception) {
                println("Error with Interval $textInterval")
            }
        }
        return CatrobatLanguageBaseResult()
    }

    override fun visitFormula(ctx: CatrobatLanguageParser.FormulaContext?): CatrobatLanguageBaseResult {
        TODO("Not yet implemented")
    }
}