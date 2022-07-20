/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.utils.StringFinder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class StringFinderTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private String singleLine = "Mos eisley spaceport. You will never find a more wretched hive of scum and villainy.";
	private String multiLine = "I must not fear.\n"
			+ "Fear is the mind-killer.\n"
			+ "Fear is the little-death that brings total obliteration.\n"
			+ "I will face my fear.\n"
			+ "I will permit it to pass over me and through me.\n"
			+ "And when it has gone past I will turn the inner eye to see its path.\n"
			+ "Where the fear has gone there will be nothing. Only I will remain.\n";

	@Test
	public void testMatchBetween() {
		String start = "find";
		String end = "of";

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(singleLine, start, end);
		assertEquals(" a more wretched hive ", stringFinder.getResult());
	}

	@Test
	public void testMatchWithNewLineChars() {
		String start = "fear.\n";
		String end = "\nFear is the little-death that brings total obliteration.";

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(multiLine, start, end);
		assertEquals("Fear is the mind-killer.", stringFinder.getResult());
	}

	@Test
	public void testMultipleStartStringMatches() {
		String start = "Fear";
		String end = "I will face my fear.";

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(multiLine, start, end);
		assertEquals(" is the mind-killer.\n"
				+ "Fear is the little-death that brings total obliteration.\n", stringFinder.getResult());
	}

	@Test
	public void testMultipleEndStringMatches() {
		String start = "find";
		String end = StringFinder.encodeSpecialChars(".");

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(singleLine, start, end);
		assertEquals(" a more wretched hive of scum and villainy", stringFinder.getResult());
	}

	@Test
	public void testNoMatchForEnd() {
		String start = "find";
		String end = "I won't be found";

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(singleLine, start, end);
		assertNull(stringFinder.getResult());
	}

	@Test
	public void testGetResultWithoutFind() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("You must call findBetween(String string, String start, String end) first.");
		new StringFinder().getResult();
	}

	@Test
	public void testGetResultTwice() {
		String start = "find";
		String end = ".";

		StringFinder stringFinder = new StringFinder();
		stringFinder.findBetween(singleLine, start, end);
		stringFinder.getResult();

		exception.expect(IllegalStateException.class);
		exception.expectMessage("You must call findBetween(String string, String start, String end) first.");
		stringFinder.getResult();
	}
}
