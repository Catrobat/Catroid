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
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NewSpriteDialogWrapper implements NewItemInterface<LookData> {

	public static final String TAG = NewSpriteDialogWrapper.class.getSimpleName();

	private NewItemInterface<Sprite> newItemInterface;
	private FragmentManager fragmentManager;
	private Scene dstScene;
	private Sprite sprite;
	private LookData look;

	public NewSpriteDialogWrapper(NewItemInterface<Sprite> newItemInterface, Scene dstScene) {
		this.newItemInterface = newItemInterface;
		this.dstScene = dstScene;
	}

	public void showDialog(FragmentManager manager) {
		fragmentManager = manager;
		sprite = new Sprite();
		NewLookDialogFragment dialog = new NewLookDialogFragment(this, dstScene, sprite) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				onWorkflowCanceled();
			}
		};
		dialog.show(fragmentManager, NewLookDialogFragment.TAG);
	}

	public void onWorkflowCanceled() {
	}

	@Override
	public void addItem(LookData item) {
		look = item;
		sprite.getLookList().add(item);
		NewSpriteDialogFragment dialog = new NewSpriteDialogFragment();
		dialog.show(fragmentManager, TAG);
	}

	private class NewSpriteDialogFragment extends TextInputDialogFragment {

		ImageView spritePreview;

		NewSpriteDialogFragment() {
			super(R.string.new_sprite_dialog_title, R.string.sprite_name_label, null, false);
		}

		@Override
		protected View inflateView() {
			View view = View.inflate(getActivity(), R.layout.dialog_new_sprite, null);
			spritePreview = view.findViewById(R.id.image_view);
			spritePreview.setImageBitmap(look.getThumbnailBitmap());
			return view;
		}

		@Override
		public Dialog onCreateDialog(Bundle bundle) {
			super.text = new UniqueNameProvider().getUniqueName(look.getName(), getScope(dstScene));
			return super.onCreateDialog(bundle);
		}

		@Override
		protected boolean onPositiveButtonClick() {
			String name = inputLayout.getEditText().getText().toString().trim();

			if (name.isEmpty()) {
				inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
				return false;
			}

			if (getScope(dstScene).contains(name)) {
				inputLayout.setError(getString(R.string.name_already_exists));
				return false;
			} else {
				sprite.setName(name);
				newItemInterface.addItem(sprite);
				return true;
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			onWorkflowCanceled();
		}

		@Override
		protected void onNegativeButtonClick() {
			try {
				StorageOperations.deleteFile(look.getFile());
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		private Set<String> getScope(Scene scene) {
			Set<String> scope = new HashSet<>();
			for (Sprite item : scene.getSpriteList()) {
				scope.add(item.getName());
			}
			return scope;
		}
	}
}
