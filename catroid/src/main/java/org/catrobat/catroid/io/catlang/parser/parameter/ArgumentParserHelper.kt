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
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.catlang.parser.parameter.context.FormulaVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.error.ArgumentParsingException
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.CatrobatParameterLexer
import org.catrobat.catroid.io.catlang.parser.parameter.antlr.gen.CatrobatParameterParser

class ArgumentParserHelper(private val context: Context) {

    class LexerErrorListener : BaseErrorListener() {
        val errors: ArrayList<String> = arrayListOf()

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            //super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
            errors.add("Error at argument position $charPositionInLine: $msg")
        }
    }

    class ParserErrorListener : BaseErrorListener() {
        val errors: ArrayList<String> = arrayListOf()

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            errors.add("Error at argument position $charPositionInLine: $msg")
        }
    }

    fun parseArgument(parameter: String): Formula {
        val lexerErrorListener = LexerErrorListener()
        val parserErrorListener = ParserErrorListener()

        val lexer = CatrobatParameterLexer(CharStreams.fromString(parameter))
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)

        var parser = CatrobatParameterParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)

        val visitor = ArgumentVisitor(context, arrayListOf(), arrayListOf(), arrayListOf())

        val argument = parser.argument()

        throwArgumentParsingException(lexerErrorListener.errors)
        throwArgumentParsingException(parserErrorListener.errors)

        var result = visitor.visitArgument(argument)
        if (result is FormulaVisitResult) {
            val internFormula = result.formula.getTrimmedFormulaStringForCatrobatLanguage(context)

            val scope = Scope(null, Sprite(), null)
            val computed = result.formula.getUserFriendlyString(null, scope)

            return result.formula
        }
        return Formula(1)
    }

    private fun throwArgumentParsingException(errors: List<String>) {
        if (errors.isNotEmpty()) {
            throw ArgumentParsingException(errors.joinToString("\n"))
        }
    }
}