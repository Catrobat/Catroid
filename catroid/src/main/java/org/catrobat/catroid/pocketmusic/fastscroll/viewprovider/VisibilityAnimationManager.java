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

package org.catrobat.catroid.pocketmusic.fastscroll.viewprovider;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.support.annotation.AnimatorRes;
import android.view.View;

import org.catrobat.catroid.R;

public class VisibilityAnimationManager {

	protected final View view;

	protected AnimatorSet hideAnimator;
	protected AnimatorSet showAnimator;

	private float pivotXRelative;
	private float pivotYRelative;

	protected VisibilityAnimationManager(final View view, @AnimatorRes int showAnimator, @AnimatorRes int
			hideAnimator, float pivotXRelative, float pivotYRelative, int hideDelay) {
		this.view = view;
		this.pivotXRelative = pivotXRelative;
		this.pivotYRelative = pivotYRelative;
		this.hideAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), hideAnimator);
		this.hideAnimator.setStartDelay(hideDelay);
		this.hideAnimator.setTarget(view);
		this.showAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), showAnimator);
		this.showAnimator.setTarget(view);
		this.hideAnimator.addListener(new AnimatorListenerAdapter() {

			boolean wasCanceled;

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (!wasCanceled) {
					view.setVisibility(View.INVISIBLE);
				}
				wasCanceled = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				super.onAnimationCancel(animation);
				wasCanceled = true;
			}
		});

		updatePivot();
	}

	public void show() {
		hideAnimator.cancel();
		if (view.getVisibility() == View.INVISIBLE) {
			view.setVisibility(View.VISIBLE);
			updatePivot();
			showAnimator.start();
		}
	}

	public void hide() {
		updatePivot();
		hideAnimator.start();
	}

	protected void updatePivot() {
		view.setPivotX(pivotXRelative * view.getMeasuredWidth());
		view.setPivotY(pivotYRelative * view.getMeasuredHeight());
	}

	public abstract static class AbsBuilder<T extends VisibilityAnimationManager> {
		protected final View view;
		protected int showAnimatorResource = R.animator.fastscroll__default_show;
		protected int hideAnimatorResource = R.animator.fastscroll__default_hide;
		protected int hideDelay = 1000;
		protected float pivotX = 0.5f;
		protected float pivotY = 0.5f;

		public AbsBuilder(View view) {
			this.view = view;
		}

		public AbsBuilder<T> withShowAnimator(@AnimatorRes int showAnimatorResource) {
			this.showAnimatorResource = showAnimatorResource;
			return this;
		}

		public AbsBuilder<T> withHideAnimator(@AnimatorRes int hideAnimatorResource) {
			this.hideAnimatorResource = hideAnimatorResource;
			return this;
		}

		public AbsBuilder<T> withHideDelay(int hideDelay) {
			this.hideDelay = hideDelay;
			return this;
		}

		public AbsBuilder<T> withPivotX(float pivotX) {
			this.pivotX = pivotX;
			return this;
		}

		public AbsBuilder<T> withPivotY(float pivotY) {
			this.pivotY = pivotY;
			return this;
		}

		public abstract T build();
	}

	public static class Builder extends AbsBuilder<VisibilityAnimationManager> {

		public Builder(View view) {
			super(view);
		}

		public VisibilityAnimationManager build() {
			return new VisibilityAnimationManager(view, showAnimatorResource, hideAnimatorResource, pivotX, pivotY, hideDelay);
		}
	}
}
