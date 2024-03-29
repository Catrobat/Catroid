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

package org.catrobat.catroid.io.catlang.parser.parameter

import android.content.Context
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.FormulaLexer
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.FormulaParser
import org.catrobat.catroid.io.catlang.parser.parameter.context.FormulaVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.error.FormulaParsingException

class CatrobatFormulaParser(private val context: Context, private val project: Project, private val scene: Scene, private val sprite: Sprite, private val brick: Brick) {
    @Suppress("FunctionOverloading")
    class LexerErrorListener : BaseErrorListener() {
        val errors: ArrayList<String> = arrayListOf()
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            errors.add("Error at argument position $charPositionInLine: $msg")
        }
    }

    @Suppress("FunctionOverloading")
    class ParserErrorListener : BaseErrorListener() {
        val errors: ArrayList<String> = arrayListOf()
        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            errors.add("Error at argument position $charPositionInLine: $msg")
        }
    }

    fun parseArgument(argument: String): Formula {
        val lexerErrorListener = LexerErrorListener()
        val parserErrorListener = ParserErrorListener()

        val lexer = FormulaLexer(CharStreams.fromString(argument))
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)

        var parser = FormulaParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)

        val argumentContext = parser.formula()
        throwArgumentParsingException(argument, lexerErrorListener.errors)
        throwArgumentParsingException(argument, parserErrorListener.errors)

        val visitor = CatrobatFormulaParserVisitor(context, getVariables(), getLists(), getUserDefinedBrickParameters(), scene)
        return (visitor.visitFormula(argumentContext) as FormulaVisitResult).formula
    }

    private fun getLists(): List<String> {
        val lists = arrayListOf<String>()
        lists.addAll(project.userLists.map { it.name })
        lists.addAll(sprite.userLists.map { it.name })
        return lists
    }

    private fun getVariables(): List<String> {
        val variables = arrayListOf<String>()
        variables.addAll(project.userVariables.map { it.name })
        variables.addAll(sprite.userVariables.map { it.name })
        variables.addAll(project.multiplayerVariables.map { it.name })
        return variables
    }

    private fun getUserDefinedBrickParameters(): List<String> {
        val script = brick.script
        if (script is UserDefinedScript) {
            val scriptBrick = script.scriptBrick
            if (scriptBrick is UserDefinedReceiverBrick) {
                return scriptBrick.userDefinedBrick.userDefinedBrickInputs.map { it.name }
            }
        }
        return emptyList()
    }

    private fun throwArgumentParsingException(argument: String, errors: List<String>) {
        if (errors.isNotEmpty()) {
            throw FormulaParsingException("Error while parsing argument $argument:" + errors.joinToString("\n"))
        }
    }
}
