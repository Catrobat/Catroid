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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.recyclerview.controller.LookController;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;
import org.catrobat.catroid.utils.DownloadUtil;

import java.io.IOException;
import java.util.List;

public class ReplaceExistingMediaDialogFragment extends DialogFragment {

	public static final String TAG = ReplaceExistingMediaDialogFragment.class.getSimpleName();

	private TextInputLayout inputLayout;
	private RadioGroup radioGroup;

	protected String mediaName;
	protected String url;
	protected String mediaType;
	protected String filePath;

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setMediaType(String type) {
		this.mediaType = type;
	}

	public void setFilePath(String path) {
		this.filePath = path;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_overwrite_media, null);

		inputLayout = view.findViewById(R.id.input);
		inputLayout.getEditText().setText(mediaName);

		radioGroup = view.findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.replace:
						inputLayout.setVisibility(TextView.GONE);
						break;
					case R.id.rename:
						inputLayout.setVisibility(TextView.VISIBLE);
						break;
				}
			}
		});

		int header;
		int replaceText;
		int renameText;
		int renameHeaderText;

		switch (mediaType) {
			case Constants.MEDIA_TYPE_LOOK:
				header = R.string.name_already_exists;
				replaceText = R.string.overwrite_replace_look;
				renameText = R.string.overwrite_rename_look;
				renameHeaderText = R.string.look_name_label;
				break;
			case Constants.MEDIA_TYPE_SOUND:
				header = R.string.name_already_exists;
				replaceText = R.string.overwrite_replace_sound;
				renameText = R.string.overwrite_rename_sound;
				renameHeaderText = R.string.sound_name_label;
				break;
			default:
				header = R.string.rename_sprite_dialog;
				replaceText = R.string.overwrite_replace_default;
				renameText = R.string.overwrite_rename_default;
				renameHeaderText = R.string.sprite_name_label;
		}

		((RadioButton) view.findViewById(R.id.replace)).setText(replaceText);
		((RadioButton) view.findViewById(R.id.rename)).setText(renameText);
		inputLayout.setHint(getString(renameHeaderText));

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(header)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, null)
				.create();

		alertDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(final DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (onPositiveButtonClick()) {
							dismiss();
						}
					}
				});
				buttonPositive.setEnabled(!inputLayout.getEditText().getText().toString().isEmpty());
				DialogInputWatcher inputWatcher = new DialogInputWatcher(inputLayout, buttonPositive, false);
				inputLayout.getEditText().addTextChangedListener(inputWatcher);
			}
		});
		inputLayout.getEditText().setOnFocusChangeListener(new OpenSoftkeyboardRightAway(alertDialog));
		return alertDialog;
	}

	private boolean onPositiveButtonClick() {
		switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.replace:
				replaceMediaItem();
				return true;
			case R.id.rename:
				return renameMediaItem();
			default:
				throw new IllegalStateException(TAG + ": Cannot find RadioButton.");
		}
	}

	private void replaceMediaItem() {
		switch (mediaType) {
			case Constants.MEDIA_TYPE_LOOK:
				List<LookData> looks = ProjectManager.getInstance().getCurrentSprite().getLookList();
				LookController lookController = new LookController();

				for (LookData look : looks) {
					if (mediaName.equals(look.getName())) {
						try {
							lookController.delete(look);
						} catch (IOException e) {
							Log.e(TAG, Log.getStackTraceString(e));
						}
						looks.remove(look);
					}
				}
				break;
			case Constants.MEDIA_TYPE_SOUND:
				List<SoundInfo> sounds = ProjectManager.getInstance().getCurrentSprite().getSoundList();
				SoundController soundController = new SoundController();

				for (SoundInfo sound : sounds) {
					if (mediaName.equals(sound.getName())) {
						try {
							soundController.delete(sound);
						} catch (IOException e) {
							Log.e(TAG, Log.getStackTraceString(e));
						}
						sounds.remove(sound);
					}
				}
				break;
		}
		DownloadUtil.getInstance().startMediaDownload(getActivity(), url, mediaName, filePath);
	}

	private boolean renameMediaItem() {
		String name = inputLayout.getEditText().getText().toString();
		switch (mediaType) {
			case Constants.MEDIA_TYPE_LOOK:
				if (!isLookNameUnique(name)) {
					inputLayout.setError(getString(R.string.name_already_exists));
					return false;
				}
				break;
			case Constants.MEDIA_TYPE_SOUND:
				if (isSoundNameUnique(name)) {
					inputLayout.setError(getString(R.string.name_already_exists));
					return false;
				}
				break;
		}

		filePath = filePath.replace(mediaName, name);
		DownloadUtil.getInstance().startMediaDownload(getActivity(), url, name, filePath);
		return true;
	}

	private boolean isLookNameUnique(String name) {
		List<LookData> looks = ProjectManager.getInstance().getCurrentSprite().getLookList();
		for (LookData look : looks) {
			if (name.equals(look.getName())) {
				return false;
			}
		}
		return true;
	}

	private boolean isSoundNameUnique(String name) {
		List<SoundInfo> sounds = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		for (SoundInfo sound : sounds) {
			if (name.equals(sound.getName())) {
				return false;
			}
		}
		return true;
	}
}
