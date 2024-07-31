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

import android.content.Context
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageLexer
import org.catrobat.catroid.io.catlang.parser.project.antlr.gen.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.context.CatrobatLanguageProgramVisitResult
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

class CatrobatLanguageParser {
    private class ErrorListener : BaseErrorListener() {
        val errors: ArrayList<String> = arrayListOf()

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            errors.add("Error parsing catrobat program in line $line at position $charPositionInLine: $msg")
        }
    }

    companion object {
        fun parseProgramFromString(program: String, context: Context): Project? {
            try {
                val lexer = CatrobatLanguageLexer(CharStreams.fromString(program))
                val lexerErrorListener = ErrorListener()
                lexer.removeErrorListeners()
                lexer.addErrorListener(lexerErrorListener)

                val parser = CatrobatLanguageParser(CommonTokenStream(lexer))
                val parserErrorListener = ErrorListener()
                parser.removeErrorListeners()
                parser.addErrorListener(parserErrorListener)

                val programContext = parser.program()

                throwErrorIfErrorsPresent(lexerErrorListener.errors)
                throwErrorIfErrorsPresent(parserErrorListener.errors)

                val visitor = CatrobatLanguageParserVisitor(context)
                val result = visitor.visitProgram(programContext)

                return if (result is CatrobatLanguageProgramVisitResult) {
                    result.project
                } else {
                    null
                }
            } catch (t: CatrobatLanguageParsingException) {
                println(t.message)
                throw t
            }
        }

        private fun throwErrorIfErrorsPresent(errors: ArrayList<String>) {
            if (errors.isNotEmpty()) {
                throw CatrobatLanguageParsingException(errors.joinToString("\n") { it })
            }
        }
    }
}
