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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.util.List;

public abstract class BrickBaseType implements Brick {
	private static final long serialVersionUID = 1L;
	private static final String TAG = BrickBaseType.class.getSimpleName();
	protected transient View view;
	protected transient CheckBox checkbox;
	protected transient boolean checked = false;
	protected transient BrickAdapter adapter;
	protected transient int alphaValue = 255;
	public transient boolean animationState = false;

	protected boolean commentedOut;
	private transient int visibility = View.VISIBLE;

	@Override
	public boolean isCommentedOut() {
		return commentedOut;
	}

	@Override
	public void commentOut() {
		this.commentedOut = true;
		setNeighboringCommentedOutBricksVisible();
		if (view == null) {
			return;
		}

		ViewGroup viewGroup = (ViewGroup) view;
		for (int index = 0; index < viewGroup.getChildCount(); index++) {
			View child = viewGroup.getChildAt(index);

			if (child instanceof BrickLayout) {
				Drawable background = child.getBackground();

				if (background == null) {
					return;
				}

				ColorMatrix matrix = new ColorMatrix();
				matrix.setSaturation(0);

				ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

				background.setColorFilter(filter);
			}
		}


	}

	@Override
	public void commentIn() {
		this.commentedOut = false;
		visibility = View.VISIBLE;
		setVisibilityForCommentedOutBricksStartingWith(adapter.getBrickList().indexOf(this) + 1, View.VISIBLE);

		if (view == null) {
			return;
		}

		ViewGroup viewGroup = (ViewGroup) view;

		for (int index = 0; index < viewGroup.getChildCount(); index++) {
			View child = viewGroup.getChildAt(index);

			if (child instanceof BrickLayout) {
				Drawable background = child.getBackground();
				if (background != null) {
					background.clearColorFilter();
				}
			}
		}
	}

	private void setNeighboringCommentedOutBricksVisible() {

		if (adapter == null || adapter.getBrickList() == null) {
			return;
		}

		int indexOfThisBrick = adapter.getBrickList().indexOf(this);

		if (!(this instanceof ScriptBrick)) {
			for (int i = indexOfThisBrick - 2; i > 0; i--) {
				BrickBaseType current = ((BrickBaseType) adapter.getBrickList().get(i));
				if (!current.isHidden()) {
					if (!current.isCommentedOut()) {
						return;
					}

					if (current.view == null) {
						setVisibilityForCommentedOutBricksStartingWith(i + 1, View.VISIBLE);
						return;
					}

					View first = ((ViewGroup) current.view).getChildAt(0);
					if (first instanceof ImageView) {
						doShow(i + 1);
					}

					return;
				}
			}
		}
		setVisibilityForCommentedOutBricksStartingWith(indexOfThisBrick + 1, View.VISIBLE);
	}

	private void setCmtOutImage() {
		if (view == null || !(view instanceof ViewGroup)) {
			return;
		}

		view.setVisibility(visibility);

		ViewGroup viewGroup = (ViewGroup) view;

		final ImageView imageView;
		if (viewGroup.getChildAt(0) instanceof ImageView) {
			imageView = (ImageView) viewGroup.getChildAt(0);
			if (!isCommentedOut()) {
				imageView.setVisibility(View.GONE);
				return;
			}
		} else if (isCommentedOut()) {
			imageView = new ImageView(view.getContext());
			viewGroup.addView(imageView, 0);
			imageView.setPadding(15, 0, 15, 0);
		} else {
			return;
		}

		imageView.setVisibility(View.VISIBLE);

		int previous = adapter.getBrickList().indexOf(this) - 1;
		final int next = adapter.getBrickList().indexOf(this) + 1;
		boolean hasPrevious = previous >= 0;
		boolean hasNext = next < adapter.getBrickList().size();


		if (this instanceof ScriptBrick || !hasPrevious || !adapter.getBrickList().get(previous).isCommentedOut()) {

			if (!hasNext || (hasNext && (!adapter.getBrickList().get(next).isCommentedOut()) ||
					(adapter.getBrickList().get(next) instanceof ScriptBrick))) {
				imageView.setVisibility(View.INVISIBLE);
			} else {

				imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

				if (((BrickBaseType) adapter.getBrickList().get(next)).isHidden()) {
					imageView.setImageResource(R.drawable.plus);
					imageView.setOnClickListener(getShowClickListener(next));
				} else {
					imageView.setImageResource(R.drawable.minus);
					imageView.setOnClickListener(getHideClickListener(next));
				}


			}


		} else {
			imageView.setImageResource(R.drawable.line);

			imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.MATCH_PARENT));

			imageView.setScaleType(ImageView.ScaleType.FIT_XY);

		}

	}

	private boolean isHidden() {
		return visibility == View.GONE;
	}

	private void setVisibilityForCommentedOutBricksStartingWith(int start, int visibility) {
		for (int i = start; i < adapter.getBrickList().size(); i++) {
			BrickBaseType currentBrick = (BrickBaseType) adapter.getBrickList().get(i);

			if (!currentBrick.isCommentedOut() || currentBrick instanceof ScriptBrick) {
				break;
			}

			currentBrick.setVisibility(visibility);
		}
	}

	private void setVisibility(int visibility) {
		this.visibility = visibility;
		if (view == null) {
			return;
		}
		view.setVisibility(visibility);
	}

	private View.OnClickListener getHideClickListener(final int next) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doHide(next);
			}
		};
	}

	private void doHide(int next) {
		setVisibilityForCommentedOutBricksStartingWith(next, View.GONE);
		((BrickBaseType) adapter.getBrickList().get(next - 1)).doPaddingAndImgForCmtOut();
	}

	private View.OnClickListener getShowClickListener(final int next) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doShow(next);
			}
		};
	}

	private void doShow(int next) {
		setVisibilityForCommentedOutBricksStartingWith(next, View.VISIBLE);
		((BrickBaseType) adapter.getBrickList().get(next - 1)).doPaddingAndImgForCmtOut();
	}

	public void doPaddingAndImgForCmtOut() {
		if (adapter == null || view == null) {
			return;
		}
		setCmtOutImage();

		int next = adapter.getBrickList().indexOf(this) + 1;
		boolean hasNext = next < adapter.getBrickList().size();
		boolean nextCommentStateDifferent = hasNext && (this.isCommentedOut() != adapter.getBrickList().get(next).isCommentedOut());
		boolean nextIsHidden = hasNext && !this.isHidden() && ((BrickBaseType) adapter.getBrickList().get(next)).isHidden();


		if (nextCommentStateDifferent || nextIsHidden) {
			view.setPadding(0, view.getPaddingTop(), 0, 50);
		} else {
			view.setPadding(0, view.getPaddingTop(), 0, 0);
		}
	}

	@Override
	public boolean isEqualBrick(Brick brick, Project mergeResult, Project current) {
		if (this.getClass().equals(brick.getClass())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void setAnimationState(boolean animationState) {
		this.animationState = animationState;
	}

	@Override
	public int getAlphaValue() {
		return alphaValue;
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
			if (visibility == View.GONE) {
				checked = false;
			}
		}
	}

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}

	@Override
	public void setCheckboxView(int id) {
		setCheckboxView(id, view);
	}

	@Override
	public void setCheckboxView(int id, View view) {
		int checkboxVisibility = View.GONE;
		boolean enabled = true;
		boolean isChecked = false;
		if (checkbox != null) {
			checkboxVisibility = checkbox.getVisibility();
			enabled = checkbox.isEnabled();
			isChecked = checkbox.isChecked();
		}
		checkbox = (CheckBox) view.findViewById(id);
		checkbox.setChecked(isChecked);
		checkbox.setVisibility(checkboxVisibility);
		checkbox.setEnabled(enabled);
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		return (Brick) super.clone();
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BrickBaseType copyBrick = null;
		try {
			copyBrick = (BrickBaseType) clone();
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
		}
		return copyBrick;
	}

	@Override
	public void setAlpha(int newAlpha) {
		alphaValue = newAlpha;
	}

	@Override
	public void enableAllViews(View recursiveView, boolean enable) {
		View viewToDisable = recursiveView == null ? view : recursiveView;

		if (viewToDisable != null) {
			if (!(viewToDisable instanceof CheckBox)) {
				viewToDisable.setEnabled(enable);
			}
			if (viewToDisable instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) viewToDisable;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					View child = viewGroup.getChildAt(i);
					enableAllViews(child, enable);
				}
			}
		}
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public abstract View getViewWithAlpha(int alphaValue);

	@Override
	public abstract View getView(Context context, int brickId, BaseAdapter adapter);

	@Override
	public abstract View getPrototypeView(Context context);

	@Override
	public abstract List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	@Override
	public void storeDataForBackPack(Sprite sprite) { }
}
