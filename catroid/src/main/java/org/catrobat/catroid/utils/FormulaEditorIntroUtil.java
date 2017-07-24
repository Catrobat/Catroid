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

package org.catrobat.catroid.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.math.Rectangle;

import org.catrobat.catroid.R;

public final class FormulaEditorIntroUtil {

	private static Activity activity;
	private static String introId;
	private static boolean prepared = false;

	private static PopupWindow introWindow;
	private static Bitmap screenshot;
	private static Bitmap screenshotDark;
	private static Rectangle[] highlightAreas;
	private static int[] titleStringIds;
	private static int[] summaryStringIds;
	private static int currentPage;

	private static final int INTRO_PAGES = 9;
	private static final int TEXT_SPACING = 50;
	private static final int MODIFIER_ADDITION = 2;
	private static final int COLOR_MULTIPLY = 0xFF5F5F5F;
	private static final int COLOR_ADD = 0x00000000;

	private FormulaEditorIntroUtil() {
	}

	public static void initializeIntro(Activity currentActivity, ViewGroup parentView, LayoutInflater inflater) {
		activity = currentActivity;

		if (!SnackBarUtil.areHintsEnabled(activity) || isIntroVisible()) {
			return;
		}

		currentPage = 0;
		prepared = false;
		introId = activity.getResources().getResourceName(R.string.formula_editor_intro_title_formula_editor);

		View introView = inflater.inflate(R.layout.formula_editor_intro_dialog, parentView);
		TextSizeUtil.enlargeViewGroup((ViewGroup) introView);

		introWindow = new PopupWindow(
				introView,
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT);
		introWindow.setClippingEnabled(false);

		highlightAreas = new Rectangle[INTRO_PAGES];
		titleStringIds = new int[INTRO_PAGES];
		summaryStringIds = new int[INTRO_PAGES];

		Button nextButton = (Button) introView.findViewById(R.id.intro_next_button);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextPage();
			}
		});

		Button skipButton = (Button) introView.findViewById(R.id.intro_skip_button);
		skipButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finishIntro();
			}
		});
	}

	public static void prepareIntro(View fragmentView) {
		if (activity == null || !SnackBarUtil.areHintsEnabled(activity)) {
			return;
		}

		if (!prepared) {
			setHighlightParams(fragmentView);
			setIntroStrings();
			setScreenshotBitmaps(fragmentView);
			setIntroTextPosition(fragmentView);
			nextPage();
			prepared = true;
			showIntro(fragmentView);
		}
	}

	public static void showIntro(View fragmentView) {
		if (activity == null || !SnackBarUtil.areHintsEnabled(activity)) {
			return;
		}

		if (!SnackBarUtil.wasHintAlreadyShown(activity, introId) && prepared && !isIntroVisible()) {
			introWindow.showAtLocation(fragmentView, Gravity.CENTER, 0, 0);
		}
	}

	public static void dismissIntro() {
		if (isIntroVisible()) {
			introWindow.dismiss();
		}
	}

	public static void finishIntro() {
		dismissIntro();
		currentPage = 0;
		prepared = false;
		SnackBarUtil.setHintShown(activity, introId);
	}

	public static boolean isIntroVisible() {
		return (introWindow != null && introWindow.isShowing());
	}

	private static void setHighlightParams(View fragmentView) {
		int idx = 0;
		setParamsOfView(null, idx++, 0, 0);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_edit_field), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_7), idx++, 4, 4);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_compute), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_object), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_function), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_logic), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_sensors), idx++, 1, 1);
		setParamsOfView((View) fragmentView.findViewById(R.id.formula_editor_keyboard_data), idx++, 1, 1);
	}

	private static void setIntroStrings() {
		int idx = 0;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_formula_editor;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_input_field;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_keyboard;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_compute;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_object;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_functions;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_logic;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_device;
		titleStringIds[idx++] = R.string.formula_editor_intro_title_data;
		idx = 0;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_formula_editor;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_input_field;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_keyboard;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_compute;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_object;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_functions;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_logic;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_device;
		summaryStringIds[idx++] = R.string.formula_editor_intro_summary_data;
	}

	private static void setParamsOfView(View view, int highlightRectangleIndex, int widthModifier, int heightModifier) {
		int width = 1;
		int height = 1;
		int[] coords = new int[2];

		if (view != null) {
			width = view.getWidth() * widthModifier + (widthModifier - 1) * MODIFIER_ADDITION;
			height = view.getHeight() * heightModifier + (widthModifier - 1) * MODIFIER_ADDITION;
			view.getLocationOnScreen(coords);
		}
		highlightAreas[highlightRectangleIndex] = new Rectangle(coords[0], coords[1], width, height);
	}

	private static void setIntroTextPosition(View fragmentView) {
		int[] coords = new int[2];
		View view = (View) fragmentView.findViewById(R.id.formula_editor_edit_field);
		view.getLocationOnScreen(coords);

		TextView titleTextView = (TextView) introWindow.getContentView().findViewById(R.id.intro_title);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleTextView.getLayoutParams();
		params.topMargin = coords[1] + view.getHeight() + TEXT_SPACING;
	}

	private static void setScreenshotBitmaps(View fragmentView) {
		SnackBarUtil.hideActiveSnack();

		View screenShotView = fragmentView.getRootView();
		screenShotView.setDrawingCacheEnabled(true);
		screenShotView.layout(0, 0, screenShotView.getMeasuredWidth(), screenShotView.getMeasuredHeight());
		screenshot = Bitmap.createBitmap(screenShotView.getMeasuredWidth(), screenShotView
				.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(screenshot);
		screenShotView.draw(canvas);
		screenShotView.setDrawingCacheEnabled(false);

		screenshotDark = screenshot.copy(Bitmap.Config.ARGB_8888, true);
		canvas = new Canvas(screenshotDark);
		Paint paint = new Paint(Color.WHITE);
		ColorFilter filter = new LightingColorFilter(COLOR_MULTIPLY, COLOR_ADD);
		paint.setColorFilter(filter);
		canvas.drawBitmap(screenshotDark, new Matrix(), paint);
		ImageView introImageView = (ImageView) introWindow.getContentView().findViewById(R.id.intro_background_image);
		introImageView.setImageBitmap(screenshotDark);

		SnackBarUtil.showActiveSnack();
	}

	private static void nextPage() {
		if (currentPage >= INTRO_PAGES) {
			finishIntro();
			return;
		}

		ImageView highlightImageView = (ImageView) introWindow.getContentView().findViewById(R.id.intro_highlight_image);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				(int) highlightAreas[currentPage].width,
				(int) highlightAreas[currentPage].height);
		params.leftMargin = (int) highlightAreas[currentPage].x;
		params.topMargin = (int) highlightAreas[currentPage].y;
		highlightImageView.setLayoutParams(params);

		Bitmap highlight = Bitmap.createBitmap(screenshot, params.leftMargin, params.topMargin, params.width, params.height);
		highlightImageView.setImageBitmap(highlight);

		TextView titleTextView = (TextView) introWindow.getContentView().findViewById(R.id.intro_title);
		titleTextView.setText(titleStringIds[currentPage]);
		TextView summaryTextView = (TextView) introWindow.getContentView().findViewById(R.id.intro_summary);
		summaryTextView.setText(summaryStringIds[currentPage]);
		currentPage++;
	}
}
