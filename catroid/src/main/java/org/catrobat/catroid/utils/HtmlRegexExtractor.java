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

	@VisibleForTesting
	public String findKeyword(String keyword, String html) {
		String regexWithHtmlBetweenWords; 

		if (!keyword.equals("") && (html.contains(keyword)) {
			return keyword;
		} else if (keyword.contains(" ") || keyword.contains("\\n")) {
			regexWithHtmlBetweenWords = "\\Q" + keyword.replaceAll("\\s+", "\\E(\\s|&nbsp;|<[^<]+>)+?\\Q") + "\\E";
			Matcher matcher = Pattern.compile(regexWithHtmlBetweenWords).matcher(html);
			if (matcher.find()) {
				return matcher.group());
			} 
		}
		return null;
	}

	public String htmlToRegexConverter(String keyword, String html) {

		if (keyword != null) {
			int keywordIndex = html.indexOf(keyword);
			String regex = "(.*?)";
			if (!keyword.equals(html)) {
				int distance = 0;
				do {
					distance++;

					String beforeKeyword = "";
					int beforeKeywordIndex = keywordIndex - distance;
					if (beforeKeywordIndex >= 0) {
						beforeKeyword = String.valueOf(html.charAt(beforeKeywordIndex));
					}

					String afterKeyword = "";
					int afterKeywordIndex = keywordIndex + keyword.length() + distance - 1;
					if (afterKeywordIndex < html.length()) {
						afterKeyword = String.valueOf(html.charAt(afterKeywordIndex));
					}

					regex = beforeKeyword + regex + afterKeyword;
				} while (!matchesFirst(regex, html, keyword) && (beforeKeyword != "" || afterKeyword != ""));
			}
			return regex;
		}	
		regex = null;
	}

	private boolean matchesFirst(String pattern, String html, String expectedMatch) {
		Matcher matcher = Pattern.compile(pattern).matcher(html);

		return matcher.find() && expectedMatch.equals(matcher.group(1));
	}
}
