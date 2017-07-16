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

package org.catrobat.catroid.gui.fragment;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.data.LookInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;
import org.catrobat.catroid.storage.FilePathInfo;
import org.catrobat.catroid.storage.StorageManager;

import java.io.IOException;
import java.util.List;

import static org.catrobat.catroid.common.Constants.POCKET_PAINT_PACKAGE_NAME;

public class LookListFragment extends RecyclerViewListFragment<LookInfo> {

	public static final String TAG = LookListFragment.class.getSimpleName();

	private SpriteInfo sprite;

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		String sceneName = getActivity().getIntent().getStringExtra(SceneListFragment.SELECTED_SCENE);
		String spriteName = getActivity().getIntent().getStringExtra(SpriteListFragment.SELECTED_SPRITE);
		sprite = ProjectHolder.getInstance().getCurrentProject().getSceneByName(sceneName).getSpriteByName(spriteName);
		super.onActivityCreated(savedInstance);
	}

	@Override
	protected RecyclerViewAdapter<LookInfo> createAdapter() {
		return new RecyclerViewAdapter<>(sprite.getLooks());
	}

	@Override
	protected Class getItemType() {
		return LookInfo.class;
	}

	@Override
	protected DirectoryPathInfo getCurrentDirectory() {
		return sprite.getDirectoryInfo();
	}

	@Override
	public void addItem(String name) {
		try {
			FilePathInfo lookFile = StorageManager.createEmptyPngOnSDCard(400, 400, getCurrentDirectory());
			editWithPocketPaint(lookFile);
			adapter.addItem(new LookInfo(name, lookFile));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public void onItemClick(LookInfo item) {
		editWithPocketPaint(item.getFilePathInfo());
	}

	@Override
	protected void showRenameDialog(String name) {
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_look_dialog, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}

	private void editWithPocketPaint(FilePathInfo pathInfo) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(POCKET_PAINT_PACKAGE_NAME, Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		Bundle bundle = new Bundle();
		bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, pathInfo.getAbsolutePath());
		intent.putExtras(bundle);
		intent.addCategory("android.intent.category.LAUNCHER");

		if (isPocketPaintInstalled(intent)) {
			startActivity(intent);
		} else {
			installAndOpenPocketPaint(intent);
		}
	}

	private boolean isPocketPaintInstalled(Intent intent) {
		List<ResolveInfo> packages = getActivity().getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		return !packages.isEmpty();
	}

	private void installAndOpenPocketPaint(final Intent pocketPaintIntent) {

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if (!packageName.equals(POCKET_PAINT_PACKAGE_NAME)) {
					return;
				}

				getActivity().unregisterReceiver(this);

				if (isPocketPaintInstalled(pocketPaintIntent)) {
					ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context
							.ACTIVITY_SERVICE);
					activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
					startActivity(pocketPaintIntent);
				}
			}
		};

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		getActivity().registerReceiver(receiver, intentFilter);

		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POCKET_PAINT_DOWNLOAD_LINK));
		downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(downloadIntent);
	}
}
