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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;

public class DynamicListPreference extends ListPreference implements AdapterView.OnItemClickListener {

	private int clickedDialogEntryIndex;
	private CharSequence dialogTitle;

	public DynamicListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DynamicListPreference(Context context) {
		super(context);
	}

	@Override
	protected View onCreateDialogView() {
		View view = View.inflate(getContext(), R.layout.dialog_languages, null);

		TextSizeUtil.enlargeTextView((TextView) view.findViewById(R.id.language_dialog_title));
		TextSizeUtil.enlargeButtonText((Button) view.findViewById(R.id.language_dialog_cancel_button));
		Button cancelButton = (Button) view.findViewById(R.id.language_dialog_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});

		dialogTitle = getDialogTitle();
		if (dialogTitle == null) {
			dialogTitle = getTitle();
		}
		((TextView) view.findViewById(R.id.language_dialog_title)).setText(dialogTitle);

		ListView list = (ListView) view.findViewById(R.id.language_dialog_list);

		int listItemLayoutId;
		if (TextSizeUtil.isLargeText()) {
			listItemLayoutId = R.layout.list_language_item_enlarged;
		} else {
			listItemLayoutId = R.layout.list_language_item;
		}

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getContext(), listItemLayoutId,
				getEntries());

		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setItemChecked(findIndexOfValue(getValue()), true);
		list.setOnItemClickListener(this);

		return view;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		if (getEntries() == null || getEntryValues() == null) {
			super.onPrepareDialogBuilder(builder);
			return;
		}

		clickedDialogEntryIndex = findIndexOfValue(getValue());

		builder.setTitle(null);
		builder.setPositiveButton(null, null);
		builder.setNegativeButton(null, null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		clickedDialogEntryIndex = position;
		DynamicListPreference.this.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
		getDialog().dismiss();
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult && clickedDialogEntryIndex >= 0
				&& getEntryValues() != null) {
			String value = getEntryValues()[clickedDialogEntryIndex].toString();
			if (callChangeListener(value)) {
				setValue(value);
			}
		}
	}
}
