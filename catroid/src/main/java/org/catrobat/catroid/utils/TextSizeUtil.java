/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

public final class TextSizeUtil {

	private static boolean largeFormulaEditorTextSize = false;

	private static float modifier = 1.0f;

	private static float widthForIncreasedTextSizeInInches = 3.75f;
	private static float modifierIncreaseDelta = 0.8f;

	private static List<TextView> enlargedObjects = new ArrayList<>();

	private TextSizeUtil() {
	}

	public static void mapTextSizesToDeviceSize() {
		Context context = CatroidApplication.getAppContext();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		if ((metrics.widthPixels / metrics.xdpi) >= widthForIncreasedTextSizeInInches) {
			largeFormulaEditorTextSize = true;
		}
	}

	public static float getModifier() {
		return modifier;
	}

	public static void enlargeViewGroup(ViewGroup viewGroup) {
		if (modifier == 1.0f) {
			return;
		}

		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			if (viewGroup.getChildAt(i) instanceof TextView) {
				TextView textView = (TextView) viewGroup.getChildAt(i);
				enlargeTextView(textView);
			} else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
				enlargeViewGroup((ViewGroup) viewGroup.getChildAt(i));
			}
		}
	}

	public static void enlargeTableLayoutButtonText(TableLayout tableLayout) {
		if (modifier == 1.0f) {
			return;
		}

		if (largeFormulaEditorTextSize) {
			modifier += modifierIncreaseDelta;
		}

		for (int i = 0; i < tableLayout.getChildCount(); i++) {
			if (tableLayout.getChildAt(i) instanceof TableRow) {
				TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
				for (int j = 0; j < tableRow.getChildCount(); j++) {
					if (tableRow.getChildAt(j) instanceof Button) {
						Button button = (Button) tableRow.getChildAt(j);
						enlargeButtonText(button);
					}
				}
			}
		}

		if (largeFormulaEditorTextSize) {
			modifier -= modifierIncreaseDelta;
		}
	}

	public static void enlargeTextView(TextView textView) {
		if (modifier == 1.0f) {
			return;
		}

		if (!enlargedObjects.contains(textView)) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * getModifier());
			enlargedObjects.add(textView);
		}
	}

	public static void enlargeActionMode(ActionMode actionMode) {
		if (modifier == 1.0f) {
			return;
		}

		if (actionMode != null) {
			TextView textView = new TextView(CatroidApplication.getAppContext());
			textView.setText(actionMode.getTitle());
			TextSizeUtil.enlargeTextView(textView);
			actionMode.setCustomView(textView);
		}
	}

	public static void enlargeButtonText(Button button) {
		if (modifier == 1.0f) {
			return;
		}

		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, button.getTextSize() * getModifier());
	}

	public static void enlargeOptionsMenu(Menu menu) {
		if (modifier == 1.0f) {
			return;
		}

		for (int i = 0; i < menu.size(); i++) {
			MenuItem item = menu.getItem(i);
			SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
			int end = spanString.length();
			spanString.setSpan(new RelativeSizeSpan(getModifier()), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			item.setTitle(spanString);
		}
	}

	public static void enlargeOptionsItem(MenuItem item) {
		if (modifier == 1.0f) {
			return;
		}

		SpannableString spanString = new SpannableString(item.getTitle().toString());
		int end = spanString.length();
		spanString.setSpan(new RelativeSizeSpan(getModifier()), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		item.setTitle(spanString);
	}

	public static void enlargePreferenceGroup(PreferenceGroup preferenceGroup) {
		if (modifier == 1.0f) {
			return;
		}

		for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
			Preference preference = preferenceGroup.getPreference(i);
			if (preference instanceof PreferenceScreen) {
				enlargePreference(preference, R.layout.preference_enlarged);
				enlargePreferenceGroup((PreferenceGroup) preference);
			} else if (preference instanceof PreferenceCategory) {
				enlargePreference(preference, R.layout.preferencecategory_enlarged);
				enlargePreferenceGroup((PreferenceGroup) preference);
			} else if (preference instanceof CheckBoxPreference) {
				enlargePreference(preference, R.layout.preference_enlarged);
			} else if (preference instanceof ListPreference) {
				enlargePreference(preference, R.layout.preference_enlarged);
			} else if (preference instanceof EditTextPreference) {
				enlargePreference(preference, R.layout.preference_enlarged);
			} else if (preference instanceof Preference) {
				enlargePreference(preference, R.layout.preference_enlarged);
			}
		}
	}

	public static void enlargePreference(Preference preference, int layoutId) {
		preference.setLayoutResource(layoutId);
	}

	public static TextAppearanceSpan getTextAppearanceSpanForMainMenu(Context context) {
		ColorStateList color;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			color = context.getResources().getColorStateList(R.color
					.main_menu_button_text_continue_program_name_color, context.getTheme());
		} else {
			color = context.getResources().getColorStateList(R.color
					.main_menu_button_text_continue_program_name_color);
		}
		int textSize = (int) context.getResources().getDimension(R.dimen.text_size_main_menu_button_continue_program_name);
		int modifiedText = (int) (textSize * modifier);

		return new TextAppearanceSpan(null, 0, modifiedText, color, null);
	}

	public static void setLargeText(boolean enabled) {
		if (enabled) {
			modifier = 1.5f;
		} else {
			modifier = 1.0f;
			enlargedObjects.clear();
		}
	}
}
