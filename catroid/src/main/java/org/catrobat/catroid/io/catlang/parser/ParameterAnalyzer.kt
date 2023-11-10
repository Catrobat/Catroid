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

package org.catrobat.catroid.io.catlang.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.catrobat.catroid.io.catlang.parser.gen.CatrobatParameterLexer
import org.catrobat.catroid.io.catlang.parser.gen.CatrobatParameterParser
import org.catrobat.catroid.io.catlang.parser.gen.CatrobatParameterParserBaseVisitor
import org.catrobat.catroid.io.catlang.parser.gen.CatrobatParameterParserVisitor

class ParameterAnalyzer {

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

    fun lexer(parameter: String) {
        try {
            val lexer = CatrobatParameterLexer(CharStreams.fromString(parameter))
            val lexerErrorListener = LexerErrorListener()
            lexer.removeErrorListeners()
            lexer.addErrorListener(lexerErrorListener)

            lexer.allTokens.forEach { println(it) }
            println(lexerErrorListener.errors.count())

            val cts = CommonTokenStream(lexer)
            cts.fill()

            val parser = CatrobatParameterParser(cts)
            parser.removeErrorListeners()
            val parserErrorListener = ParserErrorListener()
            parser.addErrorListener(parserErrorListener)
            val argCtx = parser.argument()

            val visitor = TestVisitor()
            visitor.visitArgument(argCtx)

            println(parserErrorListener.errors.count())
        } catch (ex: Throwable) {
            println(ex.message)
        }
    }

    class TestVisitor : CatrobatParameterParserVisitor<Int> {
        override fun visit(tree: ParseTree?): Int {
            TODO("Not yet implemented")
        }

        override fun visitChildren(node: RuleNode?): Int {
            TODO("Not yet implemented")
        }

        override fun visitTerminal(node: TerminalNode?): Int {
            TODO("Not yet implemented")
        }

        override fun visitErrorNode(node: ErrorNode?): Int {
            TODO("Not yet implemented")
        }

        override fun visitArgument(ctx: CatrobatParameterParser.ArgumentContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitExpression(ctx: CatrobatParameterParser.ExpressionContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitSimple_expression(ctx: CatrobatParameterParser.Simple_expressionContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitSensor_reference(ctx: CatrobatParameterParser.Sensor_referenceContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitMethod_invoaction(ctx: CatrobatParameterParser.Method_invoactionContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitParameters(ctx: CatrobatParameterParser.ParametersContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitParam_list(ctx: CatrobatParameterParser.Param_listContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitUnary_expression(ctx: CatrobatParameterParser.Unary_expressionContext?): Int {
            TODO("Not yet implemented")
        }

        override fun visitLiteral(ctx: CatrobatParameterParser.LiteralContext?): Int {
            TODO("Not yet implemented")
        }
    }
}