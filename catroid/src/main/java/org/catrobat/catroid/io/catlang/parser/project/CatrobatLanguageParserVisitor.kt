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
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParserVisitor
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser

class CatrobatLanguageParserVisitor : CatrobatLanguageParserVisitor<Unit> {
    override fun visit(tree: ParseTree?) {
        this.visitProgram(tree as CatrobatLanguageParser.ProgramContext)
    }

    override fun visitChildren(node: RuleNode?) {
        TODO("Not yet implemented")
    }

    override fun visitTerminal(node: TerminalNode?) {
        TODO("Not yet implemented")
    }

    override fun visitErrorNode(node: ErrorNode?) {
        TODO("Not yet implemented")
    }

    override fun visitProgram(ctx: CatrobatLanguageParser.ProgramContext?) {
        if (ctx != null) {
            ctx.programHeader()?.let { visitProgramHeader(it) }
            ctx.programBody()?.let { visitProgramBody(it) }
        }
    }

    override fun visitProgramHeader(ctx: CatrobatLanguageParser.ProgramHeaderContext?) {
        if (ctx != null) {
            ctx.PROGRAM_START()?.let { println(it.text) }
        }
    }

    override fun visitProgramBody(ctx: CatrobatLanguageParser.ProgramBodyContext?) {
        if (ctx != null) {
//            ctx.metadata()?.let { visitMetadata(it) }
//            ctx.stage()?.let { visitStage(it) }
//            ctx.globals()?.let { visitGlobals(it) }
            ctx.scene()?.let {
                for (scene in it) {
                    visitScene(scene)
                }
            }
        }
    }

    override fun visitMetadata(ctx: CatrobatLanguageParser.MetadataContext?) {
        TODO("Not yet implemented")
    }

    override fun visitMetadataContent(ctx: CatrobatLanguageParser.MetadataContentContext?) {
        TODO("Not yet implemented")
    }

    override fun visitDescription(ctx: CatrobatLanguageParser.DescriptionContext?) {
        TODO("Not yet implemented")
    }

    override fun visitCatrobatVersion(ctx: CatrobatLanguageParser.CatrobatVersionContext?) {
        TODO("Not yet implemented")
    }

    override fun visitCatrobatAppVersion(ctx: CatrobatLanguageParser.CatrobatAppVersionContext?) {
        TODO("Not yet implemented")
    }

    override fun visitStage(ctx: CatrobatLanguageParser.StageContext?) {
        TODO("Not yet implemented")
    }

    override fun visitStageContent(ctx: CatrobatLanguageParser.StageContentContext?) {
        TODO("Not yet implemented")
    }

    override fun visitLandscapeMode(ctx: CatrobatLanguageParser.LandscapeModeContext?) {
        TODO("Not yet implemented")
    }

    override fun visitHeight(ctx: CatrobatLanguageParser.HeightContext?) {
        TODO("Not yet implemented")
    }

    override fun visitWidth(ctx: CatrobatLanguageParser.WidthContext?) {
        TODO("Not yet implemented")
    }

    override fun visitDisplayMode(ctx: CatrobatLanguageParser.DisplayModeContext?) {
        TODO("Not yet implemented")
    }

    override fun visitGlobals(ctx: CatrobatLanguageParser.GlobalsContext?) {
        TODO("Not yet implemented")
    }

    override fun visitMultiplayerVariables(ctx: CatrobatLanguageParser.MultiplayerVariablesContext?) {
        TODO("Not yet implemented")
    }

    override fun visitVariableDeclaration(ctx: CatrobatLanguageParser.VariableDeclarationContext?) {
        TODO("Not yet implemented")
    }

    override fun visitScene(ctx: CatrobatLanguageParser.SceneContext?) {
        if (ctx != null) {
//            ctx.background()?.let { visitBackground(it) }
            ctx.actor()?.let {
                for (actor in it) {
                    visitActor(actor)
                }
            }
        }
    }

    override fun visitBackground(ctx: CatrobatLanguageParser.BackgroundContext?) {
        TODO("Not yet implemented")
    }

    override fun visitActor(ctx: CatrobatLanguageParser.ActorContext?) {
        if (ctx != null) {
            ctx.STRING(0).let { println("Actor name: " + it.text) }
            ctx.actorContent()?.let { visitActorContent(it) }
        }
    }

    override fun visitActorContent(ctx: CatrobatLanguageParser.ActorContentContext?) {
        if (ctx != null) {
//            ctx.looks()?.let {
//                for (looks in it) {
//                    visitLooks(looks)
//                }
//            }
//            ctx.sounds()?.let {
//                for (sounds in it) {
//                    visitSounds(sounds)
//                }
//            }
            ctx.scripts()?.let {
                for (scripts in it) {
                    visitScripts(scripts)
                }
            }
        }
    }

    override fun visitLocalVariables(ctx: CatrobatLanguageParser.LocalVariablesContext?) {
        TODO("Not yet implemented")
    }

    override fun visitLooks(ctx: CatrobatLanguageParser.LooksContext?) {
        TODO("Not yet implemented")
    }

    override fun visitSounds(ctx: CatrobatLanguageParser.SoundsContext?) {
        TODO("Not yet implemented")
    }

    override fun visitLooksAndSoundsContent(ctx: CatrobatLanguageParser.LooksAndSoundsContentContext?) {
        TODO("Not yet implemented")
    }

    override fun visitScripts(ctx: CatrobatLanguageParser.ScriptsContext?) {
        if (ctx != null) {
            ctx.brick_with_body()?.let {
                for (brick in it) {
                    visitBrick_with_body(brick)
                }
            }
        }
    }

    override fun visitBrick_with_body(ctx: CatrobatLanguageParser.Brick_with_bodyContext?) {
        if (ctx != null) {
            ctx.BRICK_NAME().let { println("Brick name: " + it.text) }
            ctx.brick_condition()?.let { visitBrick_condition(it) }
            ctx.brick_invocation()?.let {
                for (invocation in it) {
                    visitBrick_invocation(invocation)
                }
            }
            ctx.brick_with_body()?.let {
                for (brick in it) {
                    visitBrick_with_body(brick)
                }
            }
        }
    }

    override fun visitBrick_invocation(ctx: CatrobatLanguageParser.Brick_invocationContext?) {
        if (ctx != null) {
            ctx.BRICK_NAME().let { println("Brick name: " + it.text) }
            ctx.brick_condition()?.let { visitBrick_condition(it) }
        }
    }

    override fun visitBrick_condition(ctx: CatrobatLanguageParser.Brick_conditionContext?) {
        if (ctx != null) {
            ctx.arg_list()?.let { visitArg_list(it) }
        }
    }

    override fun visitArg_list(ctx: CatrobatLanguageParser.Arg_listContext?) {
        if (ctx != null) {
            ctx.argument()?.let {
                for (argument in it) {
                    visitArgument(argument)
                }
            }
        }
    }

    override fun visitArgument(ctx: CatrobatLanguageParser.ArgumentContext?) {
        if (ctx != null) {
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
            // skip visitFormula
//            ctx.formula()?.let { visitFormula(it) }
        }
    }

    override fun visitFormula(ctx: CatrobatLanguageParser.FormulaContext?) {
        if (ctx != null) {
            val start = ctx.start
            val end = ctx.stop

            if (start.startIndex > end.stopIndex) {
                // TODO: Error handling
                return
            }

            val tokenStream = ctx.start.tokenSource.inputStream
            val textInterval = Interval(start.startIndex, end.stopIndex)
            try {
                val text = tokenStream.getText(textInterval)
                println(text)
            } catch (e: Exception) {
                println("Error with Interval $textInterval")
            }
        }
    }
}