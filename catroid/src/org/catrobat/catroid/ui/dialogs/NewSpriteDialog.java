/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.common.io.Files;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.SpriteHistory;
import org.catrobat.catroid.content.bricks.PointToBrick.SpinnerAdapterWrapper;
import org.catrobat.catroid.content.commands.SpriteCommands;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilCamera;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NewSpriteDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_sprite";
	public static final String TAG = NewSpriteDialog.class.getSimpleName();

	private static final int REQUEST_SELECT_IMAGE = 0;
	private static final int REQUEST_CREATE_POCKET_PAINT_IMAGE = 1;
	private static final int REQUEST_TAKE_PICTURE = 2;
	private static final int REQUEST_MEDIA_LIBRARY = 3;
	private final ActionAfterFinished requestedAction;
	private final DialogWizardStep wizardStep;
	private Uri lookUri;
	private View dialogView;
	private String newObjectName = null;
	private SpinnerAdapterWrapper spinnerAdapter;

	public NewSpriteDialog() {
		this.requestedAction = ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT;
		this.wizardStep = DialogWizardStep.STEP_1;
	}

	public NewSpriteDialog(SpinnerAdapterWrapper spinnerAdapter) {
		this.requestedAction = ActionAfterFinished.ACTION_UPDATE_SPINNER;
		this.spinnerAdapter = spinnerAdapter;
		this.wizardStep = DialogWizardStep.STEP_1;
	}

	private NewSpriteDialog(DialogWizardStep wizardStep, Uri lookUri, String newObjectName,
			ActionAfterFinished requestedAction, SpinnerAdapterWrapper spinnerAdapter) {
		this.requestedAction = requestedAction;
		this.wizardStep = wizardStep;
		this.lookUri = lookUri;
		this.newObjectName = newObjectName;
		this.spinnerAdapter = spinnerAdapter;
	}

	static NewSpriteDialog newInstance() {
		NewSpriteDialog newSpriteDialog = new NewSpriteDialog();

		Bundle arguments = new Bundle();
		arguments.putInt(ActionAfterFinished.KEY, ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT.ordinal());
		arguments.putInt(DialogWizardStep.KEY, DialogWizardStep.STEP_1.ordinal());
		newSpriteDialog.setArguments(arguments);
		return newSpriteDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_object, null);
		setupPaintroidButton(dialogView);
		setupGalleryButton(dialogView);
		setupCameraButton(dialogView);
		setupMediaLibraryButton(dialogView);

		AlertDialog dialog = null;
		AlertDialog.Builder dialogBuilder = new CustomAlertDialogBuilder(getActivity()).setView(dialogView).setTitle(
				R.string.new_sprite_dialog_title);
		if (wizardStep == DialogWizardStep.STEP_1) {
			dialog = createDialogStepOne(dialogBuilder);
		} else if (wizardStep == DialogWizardStep.STEP_2) {
			dialog = createDialogStepTwo(dialogBuilder);
		}
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(final DialogInterface dialog) {
				Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						if (handleOkButton()) {
							dialog.dismiss();
						}
					}
				});
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		return dialog;
	}

	private AlertDialog createDialogStepOne(AlertDialog.Builder dialogBuilder) {
		AlertDialog dialog = dialogBuilder.create();
		dialogView.findViewById(R.id.dialog_new_object_step_2_layout).setVisibility(View.GONE);
		return dialog;
	}

	private AlertDialog createDialogStepTwo(AlertDialog.Builder dialogBuilder) {
		AlertDialog dialog = dialogBuilder.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel_button, null).create();

		dialogView.findViewById(R.id.dialog_new_object_step_1_layout).setVisibility(View.GONE);
		dialogView.findViewById(R.id.dialog_new_object_second_row).setVisibility(View.GONE);

		ImageView imageView = (ImageView) dialogView.findViewById(R.id.dialog_new_object_look_preview);
		if (newObjectName == null) {
			newObjectName = getString(R.string.new_sprite_dialog_default_sprite_name);
		}
		newObjectName = Utils.getUniqueObjectName(newObjectName);

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int[] imageDimensions = ImageEditing.getImageDimensions(lookUri.getPath());

		while (metrics.widthPixels < imageDimensions[0] && metrics.heightPixels < imageDimensions[1]) {
			imageDimensions[0] = imageDimensions[0] / 2;
			imageDimensions[1] = imageDimensions[1] / 2;
		}

		imageView.setImageBitmap(ImageEditing.getScaledBitmapFromPath(lookUri.getPath(), imageDimensions[0], imageDimensions[1], ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true));

		EditText editTextNewObject = (EditText) dialogView.findViewById(R.id.dialog_new_object_name_edit_text);
		editTextNewObject.setHint(newObjectName);
		return dialog;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (lookUri == null) {
				lookUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));
			}

			try {
				switch (requestCode) {
					case REQUEST_CREATE_POCKET_PAINT_IMAGE:
						lookUri = Uri.parse(data.getExtras().getString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT));
						break;
					case REQUEST_SELECT_IMAGE:
						lookUri = decodeUri(data.getData());
						newObjectName = new File(lookUri.toString()).getName();
						break;
					case REQUEST_TAKE_PICTURE:
						lookUri = UtilCamera.rotatePictureIfNecessary(lookUri, getString(R.string.default_look_name));
						break;
					case REQUEST_MEDIA_LIBRARY:
						lookUri = Uri.parse(data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH));
						newObjectName = Files.getNameWithoutExtension(lookUri.toString());
						break;
					default:
						return;
				}

				NewSpriteDialog dialog = new NewSpriteDialog(DialogWizardStep.STEP_2, lookUri, newObjectName,
						requestedAction, spinnerAdapter);
				dialog.show(getActivity().getSupportFragmentManager(), NewSpriteDialog.DIALOG_FRAGMENT_TAG);
				dismiss();
			} catch (NullPointerException e) {
				Utils.showErrorDialog(getActivity(), R.string.error_load_image);
				Log.e(TAG, Log.getStackTraceString(e));
			}
		} else {
			dismiss();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (spinnerAdapter != null) {
			spinnerAdapter.updateSpinner();
		}
	}

	private Uri decodeUri(Uri uri) throws NullPointerException {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
		Uri decodedUri = Uri.parse(cursor.getString(columnIndex));
		cursor.close();

		return decodedUri;
	}

	private void setupPaintroidButton(View parentView) {
		View paintroidButton = parentView.findViewById(R.id.dialog_new_object_paintroid);

		final Intent intent = new Intent("android.intent.action.MAIN");
		intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
				Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

		paintroidButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (LookController.getInstance().checkIfPocketPaintIsInstalled(intent, getActivity())) {
					Intent intent = new Intent("android.intent.action.MAIN");
					intent.setComponent(new ComponentName(Constants.POCKET_PAINT_PACKAGE_NAME,
							Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME));

					Bundle bundleForPocketPaint = new Bundle();
					bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, "");
					bundleForPocketPaint.putString(Constants.EXTRA_PICTURE_NAME_POCKET_PAINT,
							getString(R.string.default_look_name));
					intent.putExtras(bundleForPocketPaint);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, lookUri);

					intent.addCategory("android.intent.category.LAUNCHER");
					startActivityForResult(intent, REQUEST_CREATE_POCKET_PAINT_IMAGE);
				}
			}
		});
	}

	private void setupGalleryButton(View parentView) {
		View galleryButton = parentView.findViewById(R.id.dialog_new_object_gallery);

		galleryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, lookUri);
				startActivityForResult(intent, REQUEST_SELECT_IMAGE);
			}
		});
	}

	private void setupCameraButton(View parentView) {
		View cameraButton = parentView.findViewById(R.id.dialog_new_object_camera);

		cameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				lookUri = UtilCamera.getDefaultLookFromCameraUri(getString(R.string.default_look_name));

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, lookUri);
				startActivityForResult(intent, REQUEST_TAKE_PICTURE);
			}
		});
	}

	private void setupMediaLibraryButton(View parentView) {
		View mediaButton = parentView.findViewById(R.id.dialog_new_object_library);

		mediaButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				String url = Constants.LIBRARY_LOOKS_URL;
				intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
				intent.putExtra(WebViewActivity.CALLING_ACTIVITY, SpritesListFragment.TAG);
				startActivityForResult(intent, REQUEST_MEDIA_LIBRARY);
			}
		});
	}

	private boolean handleOkButton() {
		EditText editText = (EditText) dialogView.findViewById(R.id.dialog_new_object_name_edit_text);
		String newSpriteName;
		if (editText.length() == 0) {
			newSpriteName = editText.getHint().toString();
		} else {
			newSpriteName = editText.getText().toString().trim();
		}
		if (newSpriteName.contains(".")) {
			int fileExtensionPosition = newSpriteName.indexOf('.');
			newSpriteName = newSpriteName.substring(0, fileExtensionPosition);
		}

		ProjectManager projectManager = ProjectManager.getInstance();

		if (newSpriteName.equalsIgnoreCase("")) {
			Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.no_spritename_entered);
			return false;
		}

		if (projectManager.spriteExists(newSpriteName)) {
			Utils.showErrorDialog(getActivity(), R.string.name_exists, R.string.spritename_already_exists);
			return false;
		}

		Sprite sprite = new Sprite(newSpriteName);
		ArrayList<Sprite> sprites = new ArrayList<>();
		sprites.add(sprite);
		SpriteCommands.AddSpriteCommand command = new SpriteCommands.AddSpriteCommand(sprites);
		SpriteHistory.getInstance(projectManager.getCurrentProject().getName()).add(command);
		getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_NEW_SPRITE_CREATED));
		projectManager.addSprite(sprite);

		LookData lookData;
		try {
			File newLookFile = StorageHandler.getInstance().copyImage(projectManager.getCurrentProject().getName(),
					lookUri.getPath(), null);
			if (lookUri.getPath().contains(Constants.TMP_LOOKS_PATH)) {
				File oldFile = new File(lookUri.getPath());
				oldFile.delete();
			}
			String imageFileName = newLookFile.getName();
			Utils.rewriteImageFileForStage(getActivity(), newLookFile);

			lookData = new LookData();
			lookData.setLookFilename(imageFileName);
			lookData.setLookName(newSpriteName);
		} catch (IOException ioException) {
			Utils.showErrorDialog(getActivity(), R.string.error_load_image);
			Log.e(TAG, Log.getStackTraceString(ioException));
			return false;
		} catch (NullPointerException e) {
			Utils.showErrorDialog(getActivity(), R.string.error_load_image);
			Log.e(TAG, "somebody might have selected an image and deleted it before it was added");
			Log.e(TAG, Log.getStackTraceString(e));
			return false;
		}

		sprite.getLookDataList().add(lookData);

		if (requestedAction == ActionAfterFinished.ACTION_UPDATE_SPINNER && spinnerAdapter != null) {
			Intent broadcastIntent;
			broadcastIntent = new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED);
			getActivity().sendBroadcast(broadcastIntent);
			spinnerAdapter.refreshSpinnerAfterNewSprite(getActivity(), newSpriteName);
		} else {
			Intent broadcastIntent;
			broadcastIntent = new Intent(ScriptActivity.ACTION_SPRITES_LIST_CHANGED);
			getActivity().sendBroadcast(broadcastIntent);
		}

		if (requestedAction == ActionAfterFinished.ACTION_FORWARD_TO_NEW_OBJECT) {
			projectManager.setCurrentSprite(sprite);

			Intent intent = new Intent(getActivity(), ProgramMenuActivity.class);
			intent.putExtra(ProgramMenuActivity.FORWARD_TO_SCRIPT_ACTIVITY, ScriptActivity.FRAGMENT_SCRIPTS);
			startActivity(intent);
		}
		dismiss();
		return true;
	}

	public enum ActionAfterFinished {
		ACTION_FORWARD_TO_NEW_OBJECT, ACTION_UPDATE_SPINNER;
		static final String KEY = "action";
	}

	public enum DialogWizardStep {
		STEP_1, STEP_2;
		static final String KEY = "step";
	}
}
