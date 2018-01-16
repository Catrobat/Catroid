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

import android.app.FragmentManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;

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
		NewLookDialog dialog = new NewLookDialog(this, dstScene, sprite);
		dialog.show(fragmentManager, NewLookDialog.TAG);
	}

	@Override
	public void addItem(LookData item) {
		look = item;
		sprite.getLookList().add(item);
		NewSpriteDialog dialog = new NewSpriteDialog();
		dialog.show(fragmentManager, TAG);
	}

	private class NewSpriteDialog extends TextDialog {

		NewSpriteDialog() {
			super(R.string.new_sprite_dialog_title, R.string.sprite_name_label, null, false);
		}

		@Override
		protected boolean handlePositiveButtonClick() {
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
		protected void handleNegativeButtonClick() {
			try {
				StorageHandler.deleteFile(look.getAbsolutePath());
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
