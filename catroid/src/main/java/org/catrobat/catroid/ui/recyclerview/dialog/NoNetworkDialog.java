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

package org.catrobat.catroid.ui.recyclerview.dialog;

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
		View root = View.inflate(getContext(), R.layout.dialog_no_network_connection, null);
		LinearLayout brickContainer = root.findViewById(R.id.brick_container);

		setTitle(R.string.error_no_network_title);
		((TextView) root.findViewById(R.id.headline_bricks))
				.setText(getContext().getResources().getQuantityString(R.plurals.bricks_using_network_headline,
				brickList.size()));

		for (Brick brick : brickList) {
			brickContainer.addView(brick.getPrototypeView(getContext()));
		}

		setCancelable(false);
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.preference_title),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Settings.ACTION_SETTINGS);
						getContext().startActivity(intent);
					}
				});
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				});

		setView(root);
		super.onCreate(savedInstanceState);
	}
}
