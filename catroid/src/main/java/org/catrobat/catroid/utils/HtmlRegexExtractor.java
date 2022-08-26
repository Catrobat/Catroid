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

	public String searchKeyword(String keyword, String html) {
		String foundHtmlFormattedKeyword = findKeyword(keyword, html);
		String regex = htmlToRegexConverter(foundHtmlFormattedKeyword, html);

		if (regex == null) {
			showError();
			return "";
		} else {
			return regex;
		}
	}

	private void showError() {
		ToastUtil.showError(context,
				R.string.formula_editor_function_regex_html_extractor_not_found);
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public String findKeyword(String keyword, String html) {
		if (keyword.equals("")) {
			return null;
		}
		if (html.contains(keyword)) {
			return keyword;
		} else {
			return findKeywordWithHtmlBetweenWordsInText(keyword, html);
		}
	}

	private String findKeywordWithHtmlBetweenWordsInText(String keywords, String html) {

		String[] splittedKeywords = keywords.split("\\s+");
		StringBuilder keywordsWithHtmlBetweenWords = new StringBuilder(keywords.length())
				.append(Pattern.quote(splittedKeywords[0]));

		for (int i = 1; i < splittedKeywords.length; i++) {
			keywordsWithHtmlBetweenWords.append("(\\s|&nbsp;|<[^>]+>)+?");
			keywordsWithHtmlBetweenWords.append(Pattern.quote(splittedKeywords[i]));
		}
		Matcher matcher = Pattern.compile(keywordsWithHtmlBetweenWords.toString()).matcher(html);
		if (matcher.find()) {
			return matcher.group();
		} 
		return null;
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public String htmlToRegexConverter(String keyword, String html) {

		if (keyword == null) {
			return null;
		}
		int keywordIndex = html.indexOf(keyword);
		if (keyword.equals(html) || keywordIndex < 0) {
			return "(.+)";
		}
		String regex;
		int distance = 0;
		int beforeKeywordIndex;
		int afterKeywordIndex;

		do {
			distance++;

			beforeKeywordIndex = Math.max(0, keywordIndex - distance);
			String beforeKeyword = html.substring(beforeKeywordIndex, keywordIndex);

			afterKeywordIndex = Math.min(keywordIndex + keyword.length() + distance, html.length());
			String afterKeyword = html.substring(keywordIndex + keyword.length(), afterKeywordIndex);

			regex = Pattern.quote(beforeKeyword) + "(.+?)" + Pattern.quote(afterKeyword);
		} while (!matchesFirst(regex, html, keyword) && (beforeKeywordIndex > 0 || afterKeywordIndex < html.length()));
		return regex;
	}

	private boolean matchesFirst(String pattern, String html, String expectedMatch) {
		Matcher matcher = Pattern.compile(pattern).matcher(html);

		return matcher.find() && expectedMatch.equals(matcher.group(1));
	}
}
