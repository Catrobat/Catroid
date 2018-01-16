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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.PocketPaintExchangeHandler;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.POCKET_PAINT_PACKAGE_NAME;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.CAMERA;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.FILE;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.LIBRARY;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.POCKET_PAINT;
import static org.catrobat.catroid.utils.Utils.buildBackpackScenePath;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildScenePath;

public class NewLookDialog extends DialogFragment implements View.OnClickListener {

	public static final String TAG = NewLookDialog.class.getSimpleName();

	private NewItemInterface<LookData> newItemInterface;
	private Scene dstScene;
	private Sprite dstSprite;

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private Uri uri;

	public NewLookDialog(NewItemInterface<LookData> newItemInterface, Scene dstScene, Sprite dstSprite) {
		this.newItemInterface = newItemInterface;
		this.dstScene = dstScene;
		this.dstSprite = dstSprite;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_look, (ViewGroup) getView(), false);

		view.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_camera).setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view).setTitle(R.string.new_look_dialog_title);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_new_look_paintroid:
				Intent paintroidIntent = new Intent("android.intent.action.MAIN");
				paintroidIntent.setComponent(new ComponentName(POCKET_PAINT_PACKAGE_NAME, Constants
						.POCKET_PAINT_INTENT_ACTIVITY_NAME));

				Bundle bundle = new Bundle();
				bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
				bundle.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));
				paintroidIntent.putExtras(bundle);
				paintroidIntent.addCategory("android.intent.category.LAUNCHER");

				if (PocketPaintExchangeHandler.isPocketPaintInstalled(getActivity(), paintroidIntent)) {
					startActivityForResult(paintroidIntent, POCKET_PAINT);
				} else {
					BroadcastReceiver receiver = createPocketPaintBroadcastReceiver(paintroidIntent, POCKET_PAINT);
					PocketPaintExchangeHandler.installPocketPaintAndRegister(receiver, getActivity());
				}
				break;
			case R.id.dialog_new_look_media_library:
				if (Utils.isNetworkAvailable(getActivity(), true)) {
					Intent libraryIntent = new Intent(getActivity(), WebViewActivity.class);
					String url = Constants.LIBRARY_LOOKS_URL;

					libraryIntent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
					libraryIntent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
					startActivityForResult(libraryIntent, LIBRARY);
				}
				break;
			case R.id.dialog_new_look_gallery:
				Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
				fileChooserIntent.setType("image/*");

				startActivityForResult(Intent.createChooser(
						fileChooserIntent, getString(R.string.select_look_from_gallery)), FILE);
				break;
			case R.id.dialog_new_look_camera:
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				uri = getDefaultLookFromCameraUri(getString(R.string.default_look_name));

				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Intent chooserIntent = Intent.createChooser(cameraIntent, getString(R.string.select_look_from_camera));
				startActivityForResult(chooserIntent, CAMERA);
				break;
		}
	}

	private BroadcastReceiver createPocketPaintBroadcastReceiver(final Intent paintroidIntent, final int
			requestCode) {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if (!packageName.equals(POCKET_PAINT_PACKAGE_NAME)) {
					return;
				}

				getActivity().unregisterReceiver(this);

				if (PocketPaintExchangeHandler.isPocketPaintInstalled(getActivity(), paintroidIntent)) {
					ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context
							.ACTIVITY_SERVICE);
					activityManager.moveTaskToFront(getActivity().getTaskId(), 0);
					startActivityForResult(paintroidIntent, requestCode);
				}
			}
		};
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}

		String srcPath;

		switch (requestCode) {
			case POCKET_PAINT:
				srcPath = data.getStringExtra(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT);
				createItem(srcPath);
				break;
			case LIBRARY:
				srcPath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
				createItem(srcPath);
				break;
			case FILE:
				srcPath = StorageHandler.getPathFromUri(getActivity().getContentResolver(), data.getData());
				createItem(srcPath);
				break;
			case CAMERA:
				srcPath = StorageHandler.getPathFromUri(getActivity().getContentResolver(), uri);
				createItem(srcPath);
				break;
			default:
				break;
		}

		dismiss();
	}

	private void createItem(String srcPath) {
		if (srcPath.isEmpty()) {
			return;
		}
		try {
			String name = StorageHandler.getSanitizedFileName(new File(srcPath));
			String fileName = StorageHandler.copyFile(srcPath, getPath(dstScene)).getName();
			newItemInterface.addItem(
					new LookData(uniqueNameProvider.getUniqueName(name, getScope(dstSprite)), fileName));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private Set<String> getScope(Sprite sprite) {
		Set<String> scope = new HashSet<>();
		for (LookData item : sprite.getLookList()) {
			scope.add(item.getName());
		}
		return scope;
	}

	private String getPath(Scene scene) {
		String path;
		if (scene.isBackPackScene) {
			path = buildBackpackScenePath(scene.getName());
		} else {
			path = buildScenePath(scene.getProject().getName(), scene.getName());
		}

		return buildPath(path, IMAGE_DIRECTORY);
	}

	private Uri getDefaultLookFromCameraUri(String defLookName) {
		File pictureFile = new File(Constants.DEFAULT_ROOT, defLookName + ".jpg");
		return Uri.fromFile(pictureFile);
	}
}
