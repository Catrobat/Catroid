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

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;

public final class PlaySceneDialog extends AlertDialog {

	private PlaySceneDialog(Context context) {
		super(context);
	}

	public static class Builder extends AlertDialog.Builder {

		public Builder(@NonNull Context context) {
			super(context);

			final ProjectManager projectManager = ProjectManager.getInstance();
			final Scene currentScene = projectManager.getCurrentlyEditedScene();
			final Scene defaultScene = projectManager.getCurrentProject().getDefaultScene();

			String[] dialogOptions = new String[] {
					String.format(context.getString(R.string.play_scene_dialog_default), defaultScene.getName()),
					String.format(context.getString(R.string.play_scene_dialog_current), currentScene.getName())
			};

			setTitle(R.string.play_scene_dialog_title);

			projectManager.setCurrentlyPlayingScene(defaultScene);
			projectManager.setStartScene(defaultScene);

			setSingleChoiceItems(dialogOptions, 0, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							projectManager.setCurrentlyPlayingScene(defaultScene);
							projectManager.setStartScene(defaultScene);
							break;
						case 1:
							projectManager.setCurrentlyPlayingScene(currentScene);
							projectManager.setStartScene(currentScene);
							break;
						default:
							break;
					}
				}
			});
		}
	}
}
