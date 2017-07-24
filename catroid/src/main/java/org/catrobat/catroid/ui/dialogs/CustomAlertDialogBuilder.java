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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.TextSizeUtil;

public class CustomAlertDialogBuilder extends AlertDialog.Builder {

	private TextView textView;

	public CustomAlertDialogBuilder(Context context) {
		super(context);
		View dialogView = View.inflate(context, R.layout.dialog_custom_alert_dialog, null);
		textView = (TextView) dialogView.findViewById(R.id.dialog_text_text_view);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * TextSizeUtil.getModifier());
		setView(dialogView);
	}

	@Override
	public CustomAlertDialogBuilder setMessage(int messageId) {
		textView.setText(messageId);
		return this;
	}

	@Override
	public CustomAlertDialogBuilder setMessage(CharSequence message) {
		textView.setText(message);
		return this;
	}
}
