/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;

public class BrickListAnimation {

	private DragNDropListView listView;
	private BrickAdapter adapter;

	public BrickListAnimation(BrickAdapter adapter, DragNDropListView listView) {
		this.adapter = adapter;
		this.listView = listView;
	}

	public void doClickOnGroupAnimate(int groupCount, final int groupPosition) {
		doUpAnimation(groupCount, groupPosition);
		doCollapseAnimation(groupCount);
		doDownAnimation(groupCount, groupPosition);
	}

	public void doExpandAnimation(View currentListView, int childPosition) {
		AnimationSet set = new AnimationSet(true);
		Animation currentAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.ABSOLUTE, -80 * (childPosition + 1), Animation.RELATIVE_TO_SELF, 0.0f
				);
		currentAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// set this, otherwise the animation starts always if the view is redrawn ie. on scrolling
				adapter.setAnimateChildren(false);
			}
		});
		set.addAnimation(currentAnimation);
		Animation alpha = new AlphaAnimation(0.0f, 1.0f);
		set.addAnimation(alpha);
		set.setDuration(Consts.ANIMATION_DURATION_EXPAND);
		set.setFillBefore(true);
		set.setFillAfter(true);
		set.setStartTime(AnimationUtils.currentAnimationTimeMillis() + childPosition * Consts.ANIMATION_EXPAND_DELAY);
		currentListView.setAnimation(set);
	}

	private void doUpAnimation(int groupCount, int groupPosition) {
		Animation up_animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -(float) (groupCount - groupPosition - 1)
				);
		up_animation.setDuration(Consts.ANIMATION_DURATION_BRICK_SWITCHING);
		up_animation.setFillAfter(true);
		getChildFromAbsolutePosition(groupCount - 1).startAnimation(up_animation);
	}

	private void doDownAnimation(int groupCount, final int groupPosition) {
		Animation down_animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, (groupCount - groupPosition - 1)
				);
		down_animation.setDuration(Consts.ANIMATION_DURATION_BRICK_SWITCHING);
		down_animation.setFillAfter(true);

		down_animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// the expand animation starts if the new child Views are rendered the first time
				adapter.doReordering(listView, groupPosition);
			}
		});
		getChildFromAbsolutePosition(groupPosition).startAnimation(down_animation);
	}

	private void doCollapseAnimation(int groupCount) {
		int visibleGroups = groupCount - listView.getFirstVisiblePosition();
		int visibleChilds = listView.getChildCount() - visibleGroups;

		Animation currentAnimation = new AlphaAnimation(1.0f, 0.0f);
		currentAnimation.setDuration(Consts.ANIMATION_DURATION_BRICK_SWITCHING);
		currentAnimation.setFillAfter(true);
		for (int i = 0; i < visibleChilds; ++i) {
			getChildFromAbsolutePosition(groupCount + i).startAnimation(currentAnimation);
		}
	}

	private View getChildFromAbsolutePosition(int absolutePosition) {
		int displayedPosition = absolutePosition - listView.getFirstVisiblePosition();
		return listView.getChildAt(displayedPosition);
	}
}
