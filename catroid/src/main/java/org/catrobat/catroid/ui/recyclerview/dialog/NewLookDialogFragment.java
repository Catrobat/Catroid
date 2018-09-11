/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.PocketPaintExchangeHandler;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.POCKET_PAINT_PACKAGE_NAME;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.CAMERA;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.FILE;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.LIBRARY;
import static org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment.POCKET_PAINT;

public class NewLookDialogFragment extends DialogFragment implements View.OnClickListener {

	public static final String TAG = NewLookDialogFragment.class.getSimpleName();

	private NewItemInterface<LookData> newItemInterface;
	private Scene dstScene;
	private Sprite dstSprite;

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private Uri uri;

	public NewLookDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	public NewLookDialogFragment(NewItemInterface<LookData> newItemInterface, Scene dstScene, Sprite dstSprite) {
		this.newItemInterface = newItemInterface;
		this.dstScene = dstScene;
		this.dstSprite = dstSprite;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_new_look, null);

		view.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(this);
		view.findViewById(R.id.dialog_new_look_camera).setOnClickListener(this);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.new_look_dialog_title)
				.setView(view)
				.create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_new_look_paintroid:
				Intent intent = new Intent("android.intent.action.MAIN")
						.setComponent(new ComponentName(POCKET_PAINT_PACKAGE_NAME,
								Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

				Bundle bundle = new Bundle();
				bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
				bundle.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT, getString(R.string.default_look_name));
				intent.putExtras(bundle);
				intent.addCategory("android.intent.category.LAUNCHER");

				if (PocketPaintExchangeHandler.isPocketPaintInstalled(getActivity(), intent)) {
					startActivityForResult(intent, POCKET_PAINT);
				} else {
					BroadcastReceiver receiver = createPocketPaintBroadcastReceiver(intent, POCKET_PAINT);
					PocketPaintExchangeHandler.installPocketPaintAndRegister(receiver, getActivity());
				}
				break;
			case R.id.dialog_new_look_media_library:
				if (!Utils.isNetworkAvailable(getActivity())) {
					ToastUtil.showError(getActivity(), R.string.error_internet_connection);
				} else {
					intent = new Intent(getActivity(), WebViewActivity.class)
							.putExtra(WebViewActivity.INTENT_PARAMETER_URL, getMediaLibraryUrl())
							.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
					startActivityForResult(intent, LIBRARY);
				}
				break;
			case R.id.dialog_new_look_gallery:
				intent = new Intent(Intent.ACTION_GET_CONTENT)
						.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, getString(R.string.select_look_from_gallery)), FILE);
				break;
			case R.id.dialog_new_look_camera:
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				uri = getDefaultLookFromCameraUri(getString(R.string.default_look_name));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
				startActivityForResult(chooser, CAMERA);
				break;
		}
	}

	private String getMediaLibraryUrl() {
		if (ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite() == dstSprite) {
			if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
				return FlavoredConstants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
			} else {
				return FlavoredConstants.LIBRARY_BACKGROUNDS_URL_PORTRAIT;
			}
		} else {
			return FlavoredConstants.LIBRARY_LOOKS_URL;
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
		if (resultCode == AppCompatActivity.RESULT_CANCELED) {
			return;
		}

		switch (requestCode) {
			case POCKET_PAINT:
				createItem(data.getStringExtra(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT));
				break;
			case LIBRARY:
				createItem(data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH));
				break;
			case FILE:
				createItem(data.getData());
				break;
			case CAMERA:
				createItem(uri);
				break;
			default:
				break;
		}
		dismiss();
	}

	private void createItem(String srcPath) {
		if (srcPath == null || srcPath.isEmpty()) {
			return;
		}
		try {
			File srcFile = new File(srcPath);
			String name = StorageOperations.getSanitizedFileName(srcFile);

			File file = StorageOperations.copyFileToDir(srcFile,
					new File(dstScene.getDirectory(), IMAGE_DIRECTORY_NAME));

			newItemInterface.addItem(new LookData(uniqueNameProvider.getUniqueName(name, getScope(dstSprite)), file));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void createItem(Uri uri) {
		if (uri == null) {
			return;
		}
		try {
			String name = getString(R.string.default_look_name);

			File file = StorageOperations.copyUriToDir(getActivity().getContentResolver(),
					uri, new File(dstScene.getDirectory(), IMAGE_DIRECTORY_NAME), name);

			newItemInterface.addItem(new LookData(uniqueNameProvider.getUniqueName(name, getScope(dstSprite)), file));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private List<String> getScope(Sprite sprite) {
		List<String> scope = new ArrayList<>();
		for (LookData item : sprite.getLookList()) {
			scope.add(item.getName());
		}
		return scope;
	}

	private Uri getDefaultLookFromCameraUri(String defLookName) {
		File pictureFile = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, defLookName + ".jpg");
		return Uri.fromFile(pictureFile);
	}
}
