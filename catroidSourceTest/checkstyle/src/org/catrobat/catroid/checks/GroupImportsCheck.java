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

import com.puppycrawl.tools.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class GroupImportsCheck extends Check {
	public static final String MESSAGE = "Group imports by the first package identifier.";
	public static final String NO_COMMENTS_ALLOWED_MESSAGE = "Comments in the import section will be removed or rearranged by the Android Studio formatter. Remove or move them.";

	private static final int[] IMPORT_TYPES = new int[] { TokenTypes.IMPORT, TokenTypes.STATIC_IMPORT };

	private String[] lines;

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.IMPORT };
	}

	@Override
	public boolean isCommentNodesRequired() {
		return true;
	}

	@Override
	public void beginTree(DetailAST rootAST) {
		super.beginTree(rootAST);
		this.lines = getLines();
		logCommentsInImportSection(rootAST);
	}

	private void logCommentsInImportSection(DetailAST rootAST) {
		DetailAST packageDefintion = null;
		DetailAST lastImport = null;

		DetailAST currentAst = rootAST;
		while (currentAst != null) {
			if (currentAst.getType() == TokenTypes.PACKAGE_DEF) {
				packageDefintion = currentAst;
			} else if (isImport(currentAst)) {
				lastImport = currentAst;
			}
			currentAst = currentAst.getNextSibling();
		}

		if (lastImport == null) {
			return;
		}

		currentAst = packageDefintion;
		while (currentAst != null && currentAst != lastImport) {
			if (Utils.isCommentType(currentAst.getType())) {
				log(currentAst.getLineNo(), NO_COMMENTS_ALLOWED_MESSAGE);
			}
			currentAst = currentAst.getNextSibling();
		}
	}

	@Override
	public void visitToken(DetailAST ast) {
		DetailAST nextSibling = ast.getNextSibling();
		while (nextSibling != null && ast.getLineNo() == nextSibling.getLineNo()) {
			nextSibling = nextSibling.getNextSibling();
		}

		if (nextSibling != null
				&& isImport(nextSibling)
				&& nextSibling.getLineNo() - ast.getLineNo() > 1
				&& haveSameRootPackageIdentifier(ast, nextSibling)) {
			log(ast.getLineNo() + 1, MESSAGE);
		}
	}

	private boolean isImport(DetailAST ast) {
		for (int importType : IMPORT_TYPES) {
			if (ast.getType() == importType) {
				return true;
			}
		}
		return false;
	}

	private boolean haveSameRootPackageIdentifier(DetailAST firstImportAst, DetailAST secondImportAst) {
		String firstRootPackageIdentifier = getRootPackageIdentifier(getLine(firstImportAst));
		String secondRootPackageIdentifier = getRootPackageIdentifier(getLine(secondImportAst));
		return firstRootPackageIdentifier.equals(secondRootPackageIdentifier);
	}

	private String getLine(DetailAST ast) {
		return lines[ast.getLineNo() - 1];
	}

	private String getRootPackageIdentifier(String line) {
		return line.substring(line.indexOf(' ') + 1, line.indexOf('.'));
	}
}
