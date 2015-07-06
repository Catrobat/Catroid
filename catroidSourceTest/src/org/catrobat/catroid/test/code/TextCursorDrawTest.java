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
package org.catrobat.catroid.test.code;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextCursorDrawTest extends TestCase {

	private static final String FORMULA_EDITOR_EDIT_TEXT_PATH = "../catroid/src/org/catrobat/catroid/formulaeditor/FormulaEditorEditText.java";
	private static final String[] EXPECTED_DRAW_CODE = { "int line = layout.getLineForOffset(absoluteCursorPosition);",
			"int baseline = layout.getLineBaseline(line);", "int ascent = layout.getLineAscent(line)",
			"float xCoordinate = layout.getPrimaryHorizontal(absoluteCursorPosition)",
			"float startYCoordinate = baseline + ascent;", "float endYCoordinate = baseline + ascent + lineHeight",
			"canvas.drawLine(xCoordinate, startYCoordinate, xCoordinate, endYCoordinate, getPaint());" };

	public void testTextCursorCode() throws IOException {
		BufferedReader reader;
		int bracketCount = 0;
		boolean methodFound = false;
		List<String> cursorCode = new ArrayList<String>();
		reader = new BufferedReader(new FileReader(new File(FORMULA_EDITOR_EDIT_TEXT_PATH)));

		String line;

		while ((line = reader.readLine()) != null) {
			if (line.contains("protected void onDraw(Canvas canvas)")) {
				bracketCount++;
				methodFound = true;
				break;
			}
		}
		while (bracketCount > 0 && (line = reader.readLine()) != null) {
			cursorCode.add(line);
			if (line.contains("{")) {
				bracketCount++;
			}
			if (line.contains("}")) {
				bracketCount--;
			}
		}

		reader.close();
		int correctLines = 0;
		assertTrue("onDraw() not found", methodFound);
		for (String readItem : cursorCode) {
			for (String expectedItem : EXPECTED_DRAW_CODE) {
				if (readItem.contains(expectedItem)) {
					correctLines++;
					break;
				}
			}
		}
		assertEquals("A number of lines seem to be modified in the source!", EXPECTED_DRAW_CODE.length, correctLines);
	}

}
