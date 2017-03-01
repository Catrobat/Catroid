/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.media.MediaRouter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;

public class SelectCastDialog extends DialogFragment {

	private static final String DIALOG_TAG = "cast_device_selector";
	private ArrayAdapter<MediaRouter.RouteInfo> deviceAdapter;
	private Activity activity;
	private AlertDialog dialog;

	public SelectCastDialog(ArrayAdapter<MediaRouter.RouteInfo> adapter, Activity activity) {
		this.deviceAdapter = adapter;
		this.activity = activity;
		CastManager.getInstance().setCallback(MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	public void openDialog() {
		show(activity.getFragmentManager(), DIALOG_TAG);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		CastManager.getInstance().setCallback();
		super.onDismiss(dialog);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (CastManager.getInstance().currentlyConnecting() || CastManager.getInstance().isConnected()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(activity.getString(R.string.cast_ready_to_cast) + " "
					+ CastManager.getInstance().getSelectedDevice().getFriendlyName());
			builder.setPositiveButton(R.string.disconnect, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					synchronized (this) {
						CastManager.getInstance().getMediaRouter().unselect(MediaRouter.UNSELECT_REASON_STOPPED);
					}
				}
			});
			dialog = builder.create();
			return dialog;
		} else {
			final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			View view = View.inflate(getActivity(), R.layout.dialog_select_cast, null);
			ListView listView = (ListView) view.findViewById(R.id.cast_device_list_view);
			listView.setEmptyView(view.findViewById(R.id.empty_view_item));
			builder.setView(view).setTitle(getString(R.string.cast_device_selector_dialog_title));
			listView.setAdapter(deviceAdapter);
			listView.setDivider(null);

			dialog = builder.create();

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					synchronized (this) {
						MediaRouter.RouteInfo routeInfo = CastManager.getInstance().getRouteInfos().get(position);
						CastManager.getInstance().setCallback();
						CastManager.getInstance().startCastButtonAnimation();
						CastManager.getInstance().selectRoute(routeInfo);
						dialog.dismiss();
					}
				}
			});

			// Show a tip inside of the empty view after CAST_NOT_SEEING_DEVICE_TIMEOUT milliseconds.
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					synchronized (this) {
						if (dialog != null && dialog.isShowing() && deviceAdapter.isEmpty()) {
							TextView t = (TextView) dialog.findViewById(R.id.cast_searching_for_cast_text_view);
							String text = activity.getText(R.string.cast_searching_for_cast_devices).toString()
									+ activity.getText(R.string.cast_trouble_finding_devices_tip);
							t.setText(text);
						}
					}
				}
			}, Constants.CAST_NOT_SEEING_DEVICE_TIMEOUT);
			return dialog;
		}
	}
}
