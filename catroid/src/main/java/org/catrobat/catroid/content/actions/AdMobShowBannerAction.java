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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.bricks.AdMobShowBannerBrick;
import org.catrobat.catroid.stage.StageActivity;

public class AdMobShowBannerAction extends TemporalAction {

	public static final String TAG = AdMobShowBannerAction.class.getSimpleName();

	private AdMobShowBannerBrick.AdMobBannerPositionEnum mobPositionEnum;
	private AdMobShowBannerBrick.AdMobBannerSizeEnum mobSizeEnum;

	public void setMobSizeEnum(AdMobShowBannerBrick.AdMobBannerSizeEnum adMobSizeEnum) {
		this.mobSizeEnum = adMobSizeEnum;
	}

	public void setAdMobPositionEnum(AdMobShowBannerBrick.AdMobBannerPositionEnum mobPosition) {
		this.mobPositionEnum = mobPosition;
	}

	@Override
	protected void update(float percent) {
		showAdMobBanner();
	}

	private void showAdMobBanner() {
		if (StageActivity.adMobHandler == null) {
			return;
		}

		switch (mobPositionEnum) {
			case BOTTOM:
				switch (mobSizeEnum) {
					case SMART_BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_SMART_BANNER_BOTTOM).sendToTarget();
						Log.e(TAG, "ADMOB_SMART_BANNER_BOTTOM");
						break;
					case LARGE_BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_LARGE_BANNER_BOTTOM).sendToTarget();
						Log.e(TAG, "ADMOB_LARGE_BANNER_BOTTOM");
						break;
					case BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_BANNER_BOTTOM).sendToTarget();
						Log.e(TAG, "ADMOB_BANNER_BOTTOM");
						break;
					default:
						Log.e(TAG, "invalid case" + mobSizeEnum.name());
				}
				break;
			case TOP:
				switch (mobSizeEnum) {
					case SMART_BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_SMART_BANNER_TOP).sendToTarget();
						Log.e(TAG, "ADMOB_SMART_BANNER_TOP");
						break;
					case LARGE_BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_LARGE_BANNER_TOP).sendToTarget();
						Log.e(TAG, "ADMOB_LARGE_BANNER_TOP");
						break;
					case BANNER:
						StageActivity.adMobHandler.obtainMessage(StageActivity.ADMOB_BANNER_TOP).sendToTarget();
						Log.e(TAG, "ADMOB_BANNER_TOP");
						break;
					default:
						Log.e(TAG, "invalid case" + mobSizeEnum.name());
				}
				break;
		}
	}
}
