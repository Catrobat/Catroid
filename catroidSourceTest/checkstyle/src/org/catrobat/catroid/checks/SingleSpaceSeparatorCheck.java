/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class SingleSpaceSeparatorCheck extends Check {
	private static final String MESSAGE = "Only use a single space to separate tokens";

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.EOF };
	}

	@Override
	public void beginTree(DetailAST rootAST) {
		visitEachToken(rootAST);
	}

	private void visitEachToken(DetailAST ast) {
		DetailAST sibling = ast;
		while (sibling != null) {
			checkJavaToken(sibling);
			if (sibling.getChildCount() > 0) {
				visitEachToken(sibling.getFirstChild());
			}
			sibling = sibling.getNextSibling();
		}
	}

	private void checkJavaToken(DetailAST ast) {
		if (ast.getColumnNo() < 2) {
			return;
		}

		String line = getLine(ast.getLineNo() - 1);
		char precedingChar = line.charAt(ast.getColumnNo() - 1);
		char prePrecedingChar = line.charAt(ast.getColumnNo() - 2);
		if (Character.isWhitespace(precedingChar) && !isFirstTokenInLine(ast)
				&& (precedingChar != ' ' || Character.isWhitespace(prePrecedingChar))) {
			log(ast.getLineNo(), ast.getColumnNo() - 1, MESSAGE);
		}
	}

	private boolean isFirstTokenInLine(DetailAST ast) {
		String line = getLine(ast.getLineNo() - 1);
		return line.substring(0, ast.getColumnNo()).trim().length() == 0;
	}
}

