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
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.catlang.parser.parameter.context.FormulaVisitResult
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterLexer
import org.catrobat.catroid.io.catlang.parser.parameter.gen.CatrobatParameterParser

class ArgumentParserHelper(private val context: Context) {

    class LexerErrorListener : BaseErrorListener() {
        public val errors: ArrayList<String> = arrayListOf()

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            //super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
            errors.add("line $line:$charPositionInLine $msg")
        }
    }

    class ParserErrorListener : BaseErrorListener() {
        public val errors: ArrayList<String> = arrayListOf()

        override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
            //super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
            errors.add("line $line:$charPositionInLine $msg")
        }
    }

    fun parseArgument(parameter: String): Formula {
        try {
            val lexer = CatrobatParameterLexer(CharStreams.fromString(parameter))
            var parser = CatrobatParameterParser(CommonTokenStream(lexer))

            val visitor = ArgumentVisitor(context)
            var result = visitor.visitArgument(parser.argument())
            if (result is FormulaVisitResult) {
                val internFormula = result.formula.getTrimmedFormulaStringForCatrobatLanguage(context)
                println(internFormula)
            }
        } catch (ex: Throwable) {
            println(ex.message)
        }

        return Formula(1)
    }

//    fun lexer(parameter: String) {
//        try {
//            val lexer = CatrobatParameterLexer(CharStreams.fromString(parameter))
//            val lexerErrorListener = LexerErrorListener()
//            lexer.removeErrorListeners()
//            lexer.addErrorListener(lexerErrorListener)
//
//            val cts = CommonTokenStream(lexer)
//
//            val parser = CatrobatParameterParser(cts)
//            parser.removeErrorListeners()
//            val parserErrorListener = ParserErrorListener()
//            parser.addErrorListener(parserErrorListener)
//            val argCtx = parser.argument()
//
//            val visitor = TestVisitor()
//            visitor.visitArgument(argCtx)
//
//            println(parserErrorListener.errors.count())
//        } catch (ex: Throwable) {
//            println(ex.message)
//        }
//    }
}