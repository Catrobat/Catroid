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
package org.catrobat.catroid.content.actions;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.content.bricks.JumpingSumoNoSoundBrick;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDeviceController;
import org.catrobat.catroid.formulaeditor.Formula;

public class JumpingSumoNoSoundAction extends TemporalAction {

	private ARDeviceController deviceController;
	private JumpingSumoDeviceController controller;
	private byte normalizedVolume;
	protected Context context;

	private Formula volumeInPercent;

	private static final int NO_VOLUME = 0;

	private static final String TAG = JumpingSumoNoSoundBrick.class.getSimpleName();

	@Override
	protected void begin() {
		super.begin();
		controller = JumpingSumoDeviceController.getInstance();
		deviceController = controller.getDeviceController();

		normalizedVolume = (byte) +NO_VOLUME;

		if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().sendAudioSettingsMasterVolume(normalizedVolume);
			Log.d(TAG, "No_Sound");

			//ToastUtil.showSuccess(context, "hello");

			/*
			private void showToast(int numberOfBackPackedItems, int groupsPlural) {
				String textForBackpacking = context.getResources().getQuantityString(
						R.plurals.packing_items_plural, numberOfBackPackedItems);
				String textForScripts = context.getResources().getQuantityString(groupsPlural, numberOfBackPackedItems);
				ToastUtil.showSuccess(context, numberOfBackPackedItems + " " + textForScripts + " "
						+ textForBackpacking);
			}*/

			// JumpingSumo (not evo) are still using old deprecated command
			/*if (ARDISCOVERY_PRODUCT_ENUM.ARDISCOVERY_PRODUCT_JS.equals(mProductType)) {
				deviceController.getFeatureJumpingSumo().sendMediaRecordPicture((byte) 0);
			} else {
				deviceController.getFeatureJumpingSumo().sendMediaRecordPictureV2();
			}*/
		}
		//ToastUtil.showSuccess(context, "hello");

		/*if (deviceController != null) {
			deviceController.getFeatureJumpingSumo().sendMediaRecordPictureV2();
			Log.d(TAG, "send jump high command JS down");
		} else {
			Log.d(TAG, "error: send jump high command JS");
		}*/
	}

	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
