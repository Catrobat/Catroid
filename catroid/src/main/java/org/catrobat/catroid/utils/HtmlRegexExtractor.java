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

package org.catrobat.catroid.utils;

import android.content.Context;

import org.catrobat.catroid.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.VisibleForTesting;

public class HtmlRegexExtractor {
	private Context context;
	public HtmlRegexExtractor(Context context) {
		this.context = context;
	}

	public String searchKeyword(String keyword, String text) {
		String keywordFound = findKeyword(keyword, text);
		String regexFound = htmlToRegexConverter(keywordFound, text);
		if (regexFound == null) {
			showError();
			return "";
		} else {
			return regexFound;
		}
	}

	private void showError() {
		ToastUtil.showError(context,
				R.string.formula_editor_function_regex_html_extractor_not_found);
	}

	@VisibleForTesting
	public String findKeyword(String keyword, String text) {
		if (keyword.equals("")) {
			return null;
		}
		if (text.indexOf(keyword) >= 0) {
			return keyword;
		} else {
			return findKeywordWithHtmlBetweenWordsInText(keyword, text);
		}
	}

	private String findKeywordWithHtmlBetweenWordsInText(String keyword, String text) {
		String[] splittedKeyword = keyword.split(" ");
		String regex = Pattern.quote(splittedKeyword[0]);

		for (int i = 1; i < splittedKeyword.length; i++) {
			regex += ".*?" + Pattern.quote(splittedKeyword[i]);
		}
		return findShortestOccurrenceInText(regex, text);
	}

	private String findShortestOccurrenceInText(String regex, String text) {
		Matcher m = Pattern.compile(regex).matcher(text);

		String shortestOccurrence = null;
		int lastIndex = -1;
		while (m.find(lastIndex + 1)) {
			String found = m.group();
			if (shortestOccurrence == null || shortestOccurrence.length() > found.length()) {
				shortestOccurrence = found;
				lastIndex = m.start();
			}
		}
		return shortestOccurrence;
	}

	public String htmlToRegexConverter(String keyword, String htmlText) {
		int keywordIndex;
		String regex;

		if (keyword != null) {
			keywordIndex = htmlText.indexOf(keyword);
			if (keyword.equals(htmlText)) {
				regex = "(.*)";
			} else {
				regex = "(.*)";
				int distance = 0;
				do {
					distance++;

					String beforeKeyword = "";
					int beforeKeywordIndex = keywordIndex - distance;
					if (beforeKeywordIndex >= 0) {
						beforeKeyword = String.valueOf(htmlText.charAt(beforeKeywordIndex));
					}

					String afterKeyword = "";
					int afterKeywordIndex = keywordIndex + keyword.length() + distance - 1;
					if (afterKeywordIndex < htmlText.length()) {
						afterKeyword =
								String.valueOf(htmlText.charAt(afterKeywordIndex));
					}

					regex = beforeKeyword + regex + afterKeyword;
				} while (!matchesUniquely(regex, htmlText, keyword));
			}
		} else {
			regex = null;
		}
		return regex;
	}
	private boolean matchesUniquely(String pattern, String text, String expectedMatch) {
		int counter = 0;
		Matcher matcher = Pattern.compile(pattern).matcher(text);

		String matched = null;
		while (matcher.find()) {
			matched = matcher.group(1);
			counter++;
		}
		return counter == 1 && expectedMatch.equals(matched);
	}
}
