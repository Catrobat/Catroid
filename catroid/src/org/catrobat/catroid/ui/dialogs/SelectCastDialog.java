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
import android.support.v7.media.MediaRouter;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;

import java.util.List;

public class SelectCastDialog extends DialogFragment {

	private static final String DIALOG_TAG = "cast_device_selector";
	ArrayAdapter<MediaRouter.RouteInfo> deviceAdapter;
	Activity activity;

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
		//CastManager.getInstance().addCallback();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
/*		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.cast_device_selector_dialog_title));
		builder.setAdapter(deviceAdapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				synchronized (this) { //TODO Sync needed?
					MediaRouter.RouteInfo routeInfo = CastManager.getInstance().getRouteInfos().get(which);
					CastManager.getInstance().addCallback();
					CastManager.getInstance().startCastButtonAnimation();
					CastManager.getInstance().selectRoute(routeInfo);
				}
			}
		});
		return builder.create();*/

		LayoutInflater inflater = getActivity().getLayoutInflater();
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View view = inflater.inflate(R.layout.dialog_select_cast, null);
		ListView listView = (ListView) view.findViewById(R.id.cast_device_list_view);
		listView.setAdapter(deviceAdapter);
		listView.setDivider(null);

		builder.setView(view).setTitle(getString(R.string.cast_device_selector_dialog_title));
		final AlertDialog dialog = builder.create();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				synchronized (this) {
					MediaRouter.RouteInfo routeInfo = CastManager.getInstance().getRouteInfos().get(position);
					//CastManager.getInstance().addCallback();
					CastManager.getInstance().setCallback();
					CastManager.getInstance().startCastButtonAnimation();
					CastManager.getInstance().selectRoute(routeInfo);
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}
}
