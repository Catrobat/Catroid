/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
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
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;

public class BrickListAnimation {

	private DragAndDropListView listView;
	//private BrickAdapter adapter;
	private static final int ANIMATION_DURATION_BRICK_SWITCHING = 500;
	private static final int ANIMATION_DURATION_EXPAND = 500;
	private static final int ANIMATION_EXPAND_DELAY = 50;

	public BrickListAnimation(BrickAdapter adapter, DragAndDropListView listView) {
		//this.adapter = adapter;
		this.listView = listView;
	}

	public void doClickOnGroupAnimate(int groupCount, final int groupPosition) {
		doUpAnimation(groupCount, groupPosition);
		doCollapseAnimation(groupCount);
		doDownAnimation(groupCount, groupPosition);
	}

	public void doExpandAnimation(View currentListView, int childPosition) {
		AnimationSet animationSet = new AnimationSet(true);
		Animation currentAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.ABSOLUTE, -80 * (childPosition + 1),
				Animation.RELATIVE_TO_SELF, 0.0f);

		currentAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// set this, otherwise the animation starts always if the view is redrawn ie. on scrolling
				//				adapter.setAnimateChildren(false);
			}
		});

		animationSet.addAnimation(currentAnimation);
		Animation alpha = new AlphaAnimation(0.0f, 1.0f);
		animationSet.addAnimation(alpha);
		animationSet.setDuration(ANIMATION_DURATION_EXPAND);
		animationSet.setFillBefore(true);
		animationSet.setFillAfter(true);
		animationSet.setStartTime(AnimationUtils.currentAnimationTimeMillis() + childPosition * ANIMATION_EXPAND_DELAY);
		currentListView.setAnimation(animationSet);
	}

	private void doUpAnimation(int groupCount, int groupPosition) {
		Animation upAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -(float) (groupCount
						- groupPosition - 1));
		upAnimation.setDuration(ANIMATION_DURATION_BRICK_SWITCHING);
		upAnimation.setFillAfter(true);
		View groupToCollapse = getChildFromAbsolutePosition(groupCount - 1);
		if (groupToCollapse != null) {
			groupToCollapse.startAnimation(upAnimation);
		}
	}

	private void doDownAnimation(int groupCount, final int groupPosition) {
		Animation downAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, (groupCount - groupPosition - 1));
		downAnimation.setDuration(ANIMATION_DURATION_BRICK_SWITCHING);
		downAnimation.setFillAfter(true);

		downAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				// the expand animation starts if the new child Views are rendered the first time
				//				adapter.doReordering(listView, groupPosition);
			}
		});
		getChildFromAbsolutePosition(groupPosition).startAnimation(downAnimation);
	}

	private void doCollapseAnimation(int groupCount) {
		int visibleGroups = groupCount - listView.getFirstVisiblePosition();
		int visibleChilds = listView.getChildCount() - visibleGroups;

		Animation currentAnimation = new AlphaAnimation(1.0f, 0.0f);
		currentAnimation.setDuration(ANIMATION_DURATION_BRICK_SWITCHING);
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
