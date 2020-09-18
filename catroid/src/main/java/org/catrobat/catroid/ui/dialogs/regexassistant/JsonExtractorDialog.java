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

package org.catrobat.catroid.ui.dialogs.regexassistant;

import android.content.Context;
import android.content.DialogInterface;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;

import androidx.annotation.Nullable;

public class JsonExtractorDialog extends RegularExpressionFeature {
	public JsonExtractorDialog() {
		this.titleId = R.string.formula_editor_function_regex_json_extractor_title;
	}
	@Override
	public void openDialog(Context context) {
		createDialog(context);
	}
	public void createDialog(Context context) {
		TextInputDialog.Builder builder = new TextInputDialog.Builder(context);

		builder.setView(R.layout.dialog_regex_json_extractor);
		builder.setTitle(R.string.formula_editor_function_regex_json_extractor_title);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setHint(context.getString(R.string.keyword_label));
		builder.setTextWatcher(new TextInputDialog.TextWatcher() {
			@Nullable
			@Override
			public String validateInput(String input, Context context) {
				return null;
			}
		});
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.show();
	}
}
