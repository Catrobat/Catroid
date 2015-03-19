/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

package org.catrobat.catroid.ui.bricks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.ui.BrickView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.UserBrickDataEditorFragment;
import org.catrobat.catroid.utils.Utils;

/**
 * UserScriptDefinitionBrick View Factory.
 * Created by Illya Boyko on 16/03/15.
 */
public class UserScriptDefinitionBrickViewProvider extends BrickViewProvider {
	public UserScriptDefinitionBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createUserScriptDefinitionBrickView(final UserScriptDefinitionBrick brick, ViewGroup parent) {
		final View view = inflateBrickView(parent, R.layout.brick_user_definition);

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_user_definition_layout);
		layout.setFocusable(false);
		layout.setFocusableInTouchMode(false);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		TextView define = new TextView(context);
		define.setFocusable(false);
		define.setFocusableInTouchMode(false);
		define.setTextAppearance(context, R.style.BrickText);
		define.setText(context.getString(R.string.define));
		define.setText(define.getText() + "  ");

		layout.addView(define);
		((LinearLayout.LayoutParams) define.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;

		BrickView prototype = createView(brick.getBrick(), layout, true);
		Bitmap brickImage = getBrickImage(prototype);

		ImageView preview = getBorderedPreview(brickImage);
		preview.setFocusable(false);
		preview.setFocusableInTouchMode(false);

		// This stuff isn't being included by the style when I use setTextAppearance.
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View eventOrigin) {
				if (clickAllowed(view)) {
					UserBrickDataEditorFragment.showFragment(view, brick);
				}
			}
		};
		preview.setOnClickListener(clickListener);
		layout.setOnClickListener(clickListener);
		define.setOnClickListener(clickListener);

		layout.addView(preview);

		return view;
	}


	private Bitmap getBrickImage(View view) {

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();

		view.setDrawingCacheEnabled(true);

		view.measure(View.MeasureSpec.makeMeasureSpec(ScreenValues.SCREEN_WIDTH, View.MeasureSpec.EXACTLY), View.MeasureSpec
				.makeMeasureSpec(
						Utils.getPhysicalPixels(DragAndDropListView.WIDTH_OF_BRICK_PREVIEW_IMAGE, view.getContext()),
						View.MeasureSpec.AT_MOST));
		view.layout(0, 0, ScreenValues.SCREEN_WIDTH, view.getMeasuredHeight());

		view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		view.buildDrawingCache(true);

		if (view.getDrawingCache() == null) {
			view.setDrawingCacheEnabled(drawingCacheEnabled);
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(drawingCacheEnabled);

		return bitmap;
	}

	public ImageView getBorderedPreview(final Bitmap bitmap) {
		ImageView imageView = new ImageView(context);
		imageView.setBackgroundColor(Color.TRANSPARENT);

		int radius = 7;

		Bitmap result = getWithBorder(radius, bitmap, Color.argb(Math.round(0.25f * 255), 0, 0, Math.round(0.1f * 255)));

		imageView.setImageBitmap(result);

		return imageView;
	}

	public Bitmap getWithBorder(int radius, Bitmap bitmap, int color) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int borderedWidth = width + radius * 2;
		int borderedHeight = height + radius * 2;

		Bitmap toReturn = Bitmap.createBitmap(borderedWidth, borderedHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(toReturn);

		Bitmap border = Bitmap.createBitmap(borderedWidth, borderedHeight, Bitmap.Config.ARGB_8888);
		Canvas borderCanvas = new Canvas(border);

		Bitmap alpha = bitmap.extractAlpha();

		Paint paintBorder = new Paint();
		paintBorder.setColor(Color.WHITE);
		Paint paintBorder2 = new Paint();
		paintBorder2.setColor(color);
		Paint paint = new Paint();

		borderCanvas.drawBitmap(alpha, 0, 0, paintBorder);
		borderCanvas.drawBitmap(alpha, radius * 2, 0, paintBorder);
		borderCanvas.drawBitmap(alpha, 0, radius * 2, paintBorder);
		borderCanvas.drawBitmap(alpha, radius * 2, radius * 2, paintBorder);

		alpha = border.extractAlpha();

		canvas.drawBitmap(alpha, 0, 0, paintBorder2);
		canvas.drawBitmap(bitmap, radius, radius, paint);

		return toReturn;
	}


}
