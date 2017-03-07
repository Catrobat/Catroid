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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;

import java.util.List;

public class NoNetworkDialog extends AlertDialog {

	private List<Brick> brickList;

	public NoNetworkDialog(Context context, List<Brick> brickList) {
		super(context);
		this.brickList = brickList;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		View dialogLayout = View.inflate(this.getContext(), R.layout.dialog_error_networkconnection, null);
		LinearLayout imageLayout = (LinearLayout) dialogLayout
				.findViewById(R.id.dialog_error_network_brickimages_layout);

		for (Brick networkBrick : brickList) {
			imageLayout.addView(networkBrick.getPrototypeView(getContext()));
		}

		setTitle(R.string.error_no_network_title);
		if (brickList.size() > 1) {
			TextView brickDescription = (TextView) dialogLayout.findViewById(R.id.dialog_text_headtext_bricks);
			brickDescription.setText(this.getContext().getText(R.string.error_no_network_multiple_bricks));
		}
		setCancelable(false);
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				});
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.preference_title),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(Settings.ACTION_SETTINGS);
						getContext().startActivity(i);
					}
				});
		setView(dialogLayout);
		super.onCreate(savedInstanceState);
	}
}
