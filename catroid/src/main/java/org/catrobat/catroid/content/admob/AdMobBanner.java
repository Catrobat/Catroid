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
package org.catrobat.catroid.content.admob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.utils.Utils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AdMobBanner {

	private AdView adView;
	private AdRequest adRequest;
	private String deviceId;

	@SuppressLint("HardwareIds")
	public AdMobBanner(Context context) {
		adView = new AdView(context);
		deviceId = Utils.getAdMobDeviceId(context);
	}

	public AdView getAdView() {
		return adView;
	}

	public void createNewAdMobBanner(AdSize adSize, int relativeLayoutRule) {
		adView.setAdSize(adSize);
		adView.setAdUnitId(BuildConfig.ADMOB_BANNER_UNIT_ID);
		adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(relativeLayoutRule);
		adParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		adView.setLayoutParams(adParams);
		adView.loadAd(adRequest);
	}

	public void showAdMobBanner() {
		adView.setVisibility(VISIBLE);
	}

	public void pauseAdMobBanner() {
		adView.pause();
	}

	public void resumeAdMobBanner() {
		adView.resume();
	}

	public void hideAdMobBanner() {
		adView.setVisibility(GONE);
		adView.destroy();
	}
}
