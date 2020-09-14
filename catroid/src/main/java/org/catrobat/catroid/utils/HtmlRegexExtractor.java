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

	public void searchKeyword(String keyword, String text) {
		if (findKeyword(keyword, text) == null) {
			showError();
		} else {
			showSuccess();
		}
	}

	private void showSuccess() {
		ToastUtil.showSuccess(context,
				R.string.formula_editor_function_regex_html_extractor_found);
	}
	private void showError() {
		ToastUtil.showError(context,
				R.string.formula_editor_function_regex_html_extractor_not_found);
	}

	@VisibleForTesting
	public String findKeyword(String keyword, String text) {
		if (keyword.equals("")) {
			throw new IllegalArgumentException("No empty keywords allowed");
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
}
