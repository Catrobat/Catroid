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
		assertEquals("abc", htmlExtractor.findKeyword("abc", "We are looking for the abc "
				+ "statement in long text"));
	}

	@Test
	public void testFindKeywordInSentenceWithSpaceInKeywordAndTextWithNBSP() {
		assertEquals("ab&nbsp;c", htmlExtractor.findKeyword("ab c", "We are looking for "
				+ "the ab&nbsp;c statement in long text"));
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
		assertEquals("\\Qb\\E(.+?)\\Qe\\E", htmlExtractor.htmlToRegexConverter("cd", "abcdefg"));
	}

	@Test
	public void testCreateRegexWithKeywordAtStart() {
		assertEquals("\\Q\\E(.+?)\\Qc\\E", htmlExtractor.htmlToRegexConverter("ab", "abcdefg"));
	}

	@Test
	public void testCreateRegexWithDuplicateKeywordFirstOccurrence1CharContext() {
		assertEquals("\\Qb\\E(.+?)\\Qd\\E", htmlExtractor.htmlToRegexConverter("KEY", "abKEYdefgadKEYdefg"));
	}

	@Test
	public void testCreateRegexWith2CharContext() {
		assertEquals("\\Qyb\\E(.+?)\\Qde\\E", htmlExtractor.htmlToRegexConverter("KEY", "abcdefg ybKEYdefg"));
	}

	@Test
	public void testCreateRegexWhereKeywordEqualsHtmlText() {
		assertEquals("(.+)", htmlExtractor.htmlToRegexConverter("abcdefg", "abcdefg"));
	}

	@Test
	public void testCreateRegexPostfixInKeyword() {
		assertEquals("\\Q\\E(.+?)\\Qbd\\E", htmlExtractor.htmlToRegexConverter("abc", "abcbd"));
	}

	@Test
	public void testCreateRegexOutOfBoundsAfter2CharContext() {
		assertEquals("\\Qb\\E(.+?)\\Qba\\E", htmlExtractor.htmlToRegexConverter("abc", "babcbabcb"));
	}
	@Test
	public void testFirstKeyBordersOnSecondKey() {
		assertEquals("\\Qa\\E(.+?)\\Qa\\E", htmlExtractor.htmlToRegexConverter("KEY",
				"baKEYaKEYaa"));
	}
	@Test
	public void testTemperatureTimeAndDateDotCom() {
		String htmlTimeAndDateDotCom = 
				"</script><nav class=nav-3><div class=fixed><a href='/weather/germany/hildesheim'"
				+ "title='Shows a weather overview'>Weather</a><a href='/weather/germany/hildesheim/hourly' "
				+ "title='Hour-by-hour weather'>Weather Hourly</a><a href='/weather/germany/hildesheim/ext' "
				+ "class=active title='Extended forecast for the next two weeks'>14 Day Forecast</a><a "
				+ "href='/weather/germany/hildesheim/historic' title='Past weather for yesterday'>"
				+ "Yesterday Weather</a><a href='/weather/germany/hildesheim/climate' title='Weatherinformation'>"
				+ "Climate</a></div></nav></section></header><main class='tpl-banner layout-grid layout-grid--sky'>"
				+ "<article class='layout-grid__main'><section class=bk-focus><div id=qlook class=bk-focus>"
				+ "<div class=h1>Now</div><img id=cur-weather class=mtt title='Partly cloudy.' "
				+ "src='//c.tadst.com/gfx/w/svg/wt-4.svg' width=80 height=80><div class=h2>8&nbsp;°C</div>"
				+ "<p>Partly cloudy.</p><br class=clear><p>Feels Like: 6&nbsp;°C<br><span "
				+ "title='High and low forecasted temperature today'>Forecast: 12 / 4&nbsp;°C</span>"
				+ "<br>Wind: 11 km/h <span class='comp sa8' title='Wind blowing from 280° West to East'>?</span>"
				+ "from West</p></div><div class=bk-focus__info><table class='table'>"
				+ "<tbody><tr><th>Location: </th><td>Braunschweig Wolfsburg</td></tr><tr><th>Current Time: </th>"
				+ "<td id=wtct>12 Oct 2020, 07:52:24</td></tr><tr><th>Latest Report: </th><td>12 Oct 2020, 07:20"
				+ "</td></tr><tr><th>Visibility: </th><td>N/A</td></tr><tr><th>Pressure</th><td>1018 mbar"
				+ "</tr><tr><th>Humidity: </th><td>87%</td></tr><tr><th>Dew Point: </th><td>6&nbsp;°C</td>"
				+ "</tr></tbody></table></div><div id=bk-map class=bk-focus__map><a href='/time/map/#!cities=1018'>"
				+ "<img title='Map showing the location of Hildesheim. Click map to see the location.' "
				+ "src='//c.tadst.com/gfx/citymap/de-10.png?9' alt='Location of Hildesheim' width=320 height=160>"
				+ "<img id=map-mrk src='//c.tadst.com/gfx/n/icon/icon-map-pin.png' class=fadeInDown "
				+ "style='left:112px;top:11px' alt=Location title='Location of Hildesheim' width=18 height=34>"
				+ "</a></div></section><section class=fixed></section><h2 class=mgt0>Hildesheim Extended Forecast "
				+ "with high and low temperatures</h2><div class=weather-graph><a href='/custom/site.html' id=set-f "
				+ "title='Change Units' onclick='return modpop(&#39;type=weather&#39&#39;Change Units for Weather&#39;);'>"
				+ "<i class='i-font i-settings'></i>°C</a><div id=weatherNav><div id=navLeft class=navleft></div>"
				+ "<div class=navLeftUnder><a href='/weather/germany/hildesheim/historic'>Last 2 weeks of weather"
				+ "</a></div><div id=navRight class=navright></div></div><div id=weatherContainer><div id=weather>"
				+ "</div></div></div><div class='row'><p class=lk-block><a href='/weather/germany/hildesheim' "
				+ "class=read-more>See weather overview</a></p></div>";
		String feelsLike = "6 °C";
		String foundHtmlFormattedKeyword = htmlExtractor.findKeyword(feelsLike, htmlTimeAndDateDotCom);
		assertEquals("\\Q: \\E(.+?)\\Q<b\\E", htmlExtractor.htmlToRegexConverter(foundHtmlFormattedKeyword, htmlTimeAndDateDotCom));
	}
}
