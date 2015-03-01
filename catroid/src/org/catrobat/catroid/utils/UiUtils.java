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

package org.catrobat.catroid.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.catrobat.catroid.R;

/**
 * Created by illya on 01/03/15.
 */
public class UiUtils {
	/**
	 * Start Blink animation.
	 *
	 * @param view view to animate
	 */
	public static void startBlinkAnimation(final Context context, final View view) {
		if (view == null || context == null) {
			return;
		}
		startAnimation(view, AnimationUtils.loadAnimation(context, R.anim.blink));
	}

	/**
	 * Start animation. Set View Transient state during animation to false.
	 *
	 * @param view      view to animate
	 * @param animation animation.
	 */
	public static void startAnimation(final View view, Animation animation) {
		if (animation == null || view == null || !view.hasWindowFocus()) {
			return;
		}
		
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ViewCompat.setHasTransientState(view, true);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ViewCompat.setHasTransientState(view, false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		view.startAnimation(animation);
	}
}
