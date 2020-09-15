/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.ui.regexassistant;

import android.app.Activity;

import org.catrobat.catroid.utils.HtmlRegexExtractor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HtmlRegexExtractorTest {

	@Before
	public void setUp() {
		Activity context = new Activity();
		htmlExtractor = new HtmlRegexExtractor(context);
	}
	private HtmlRegexExtractor htmlExtractor;

	@Test
	public void testFindKeywordWithEmptyKeyword() {
		assertNull(htmlExtractor.findKeyword("", "abc"));
	}

	@Test
	public void testFindKeywordWithWrongKeyword() {
		assertNull(htmlExtractor.findKeyword("def", "abc"));
	}

	@Test
	public void testFindOneKeywordInLongWord() {
		assertEquals("abc", htmlExtractor.findKeyword("abc", "abcdefg"));
	}

	@Test
	public void testFindKeywordInSentence() {
		assertEquals("abc", htmlExtractor.findKeyword("abc", "Wer are looking for the abc "
				+ "statement in long text"));
	}

	@Test
	public void testFindKeywordInSentenceWithSpaceInKeywordAndTextWithNBSP() {
		assertEquals("ab&nbsp;c", htmlExtractor.findKeyword("ab c", "Wer are looking for "
				+ "the "
				+ "ab&nbsp;c statement in long text"));
	}

	@Test
	public void testKeywordWithNonBreakingSpace() {
		assertEquals("Key&nbsp;Word", htmlExtractor.findKeyword("Key Word", "Key&nbsp;Word"));
	}

	@Test
	public void testKeywordWithTag() {
		assertEquals("Key <i>Word", htmlExtractor.findKeyword("Key Word", "Key "
				+ "<i>Word</i>"));
	}

	@Test
	public void testFalseKeywordOrder() {
		assertNull(htmlExtractor.findKeyword("Key Word", "Word Key"));
	}

	@Test
	public void testRegexAsKeyword() {
		assertNull("A regular expression should not be found inside a html text that "
						+ "doesn't contain it literally",
				htmlExtractor.findKeyword("[A-Z]", "ABCDE"));
	}

	@Test
	public void testRegexAsKeywordAndInText() {
		assertEquals("[A-Z]", htmlExtractor.findKeyword("[A-Z]", "[A-Z]+.*"));
	}

	@Test
	public void testFindKeywordSmallestOccurrence() {
		assertEquals("Hello&nbsp;World", htmlExtractor.findKeyword("Hello World",
				"Hello Banana Animal Text Hello&nbsp;World Ape"));
	}

	@Test
	public void testCreateRegexWithOneCharContext() {
		assertEquals("b(.*)e", htmlExtractor.htmlToRegexConverter("cd", "abcdefg"));
	}

	@Test
	public void testCreateRegexWithKeywordAtStart() {
		assertEquals("(.*)c", htmlExtractor.htmlToRegexConverter("ab", "abcdefg"));
	}

	@Test
	public void testCreateRegexWithKeywordAtEnd() {
		assertEquals("d(.*)", htmlExtractor.htmlToRegexConverter("efg", "abcdefg"));
	}

	@Test
	public void testCreateRegexWithDuplicateKeywordFirstOccurrence1CharContext() {
		assertEquals("ab(.*)defga", htmlExtractor.htmlToRegexConverter("KEY", "abKEYdefgadKEYdefg"));
	}

	@Test
	public void testCreateRegexWith2CharContext() {
		assertEquals("yb(.*)de", htmlExtractor.htmlToRegexConverter("KEY", "abcdefg ybKEYdefg"));
	}

	@Test
	public void testCreateRegexWhereKeywordEqualsHtmlText() {
		assertEquals("(.*)", htmlExtractor.htmlToRegexConverter("abcdefg", "abcdefg"));
	}

	@Test
	public void testCreateRegexPostfixInKeyword() {
		assertEquals("(.*)b", htmlExtractor.htmlToRegexConverter("abc", "abcbc"));
	}

	@Test
	public void testCreateRegexOutOfBoundsAfter2CharContext() {
		assertEquals("b(.*)ba", htmlExtractor.htmlToRegexConverter("abc", "babcbabcb"));
	}
	@Test
	public void testFirstKeyBordersOnSecondKey() {
		assertEquals("baaaa(.*)aaaaK", htmlExtractor.htmlToRegexConverter("KEY",
				"baaaaKEYaaaaKEYaaaa"));
	}
	@Test
	public void testCreateRegexWhereTextOnlyKeywords() {
		assertEquals("(.*)aa", htmlExtractor.htmlToRegexConverter("a", "aaa"));
	}
}
