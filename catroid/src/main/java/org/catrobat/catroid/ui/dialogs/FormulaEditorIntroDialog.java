/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.utils.SnackbarUtil;

import java.util.LinkedList;
import java.util.Queue;

public class FormulaEditorIntroDialog extends Dialog implements View.OnClickListener {

	private TextView introTitle;
	private TextView introSummary;
	private Queue<IntroSlide> introSlides;
	private FormulaEditorFragment formulaEditorFragment;

	public FormulaEditorIntroDialog(FormulaEditorFragment formulaEditorFragment, int style) {
		super(formulaEditorFragment.getActivity(), style);
		this.formulaEditorFragment = formulaEditorFragment;
		introSlides = getIntroDialogSlides();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setCanceledOnTouchOutside(false);

		setContentView(R.layout.dialog_formula_editor_intro);

		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

		Drawable background = new ColorDrawable(Color.BLACK);
		background.setAlpha(200);
		getWindow().setBackgroundDrawable(background);

		getWindow().setDimAmount(0f);

		introTitle = findViewById(R.id.intro_dialog_title);
		introSummary = findViewById(R.id.intro_dialog_summary);

		(findViewById(R.id.intro_dialog_skip_button)).setOnClickListener(this);
		(findViewById(R.id.intro_dialog_next_button)).setOnClickListener(this);

		nextSlide();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.intro_dialog_skip_button:
				onBackPressed();
				break;
			case R.id.intro_dialog_next_button:
				nextSlide();
				break;
			default:
				break;
		}
	}

	private void nextSlide() {
		if (introSlides.isEmpty()) {
			onBackPressed();
		} else {
			introSlides.remove().applySlide();
		}
	}

	@Override
	public void onBackPressed() {
		SnackbarUtil.setHintShown(formulaEditorFragment.getActivity(),
				formulaEditorFragment.getActivity().getResources().getResourceName(R.string.formula_editor_intro_title_formula_editor));
		super.onBackPressed();
	}

	private static final int NONE = -1;

	private class IntroSlide {

		int titleStringId;
		int summaryStringId;
		int targetViewId;

		IntroSlide(int titleStringId,
				int summaryStringId,
				int targetViewId) {
			this.titleStringId = titleStringId;
			this.summaryStringId = summaryStringId;
			this.targetViewId = targetViewId;
		}

		void applySlide() {
			setText();
			if (targetViewId != NONE) {
				setAnimation();
				setPosition();
			} else {
				getWindow().setGravity(Gravity.CENTER);
			}
		}

		private void setText() {
			introTitle.setText(titleStringId);
			introSummary.setText(summaryStringId);
		}

		private void setAnimation() {
			View targetView = formulaEditorFragment.getView().findViewById(targetViewId);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
			alphaAnimation.setDuration(1000);
			alphaAnimation.setRepeatCount(2);
			alphaAnimation.setFillAfter(true);
			targetView.startAnimation(alphaAnimation);
		}

		private int getViewYCoordinates(int resourceId) {
			Rect rectangle = new Rect();
			Window window = getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
			int statusBarHeight = rectangle.top;
			int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
			int titleBarHeight = contentViewTop - statusBarHeight;

			int[] location = new int[2];
			View view = formulaEditorFragment.getView().findViewById(resourceId);
			view.getLocationOnScreen(location);
			return location[1] + titleBarHeight;
		}

		private void setPosition() {
			int keyboardLocation = getViewYCoordinates(R.id.formula_editor_keyboard_compute);
			int targetViewLocation = getViewYCoordinates(targetViewId);
			int toolbarLocation = getViewYCoordinates(R.id.formula_editor_brick_space);

			getWindow().setGravity(Gravity.TOP);
			WindowManager.LayoutParams dialogLayoutParams = getWindow().getAttributes();
			if (targetViewLocation >= keyboardLocation) {
				dialogLayoutParams.y = toolbarLocation;
			} else {
				dialogLayoutParams.y = keyboardLocation;
			}
			getWindow().setAttributes(dialogLayoutParams);
		}
	}

	private Queue<IntroSlide> getIntroDialogSlides() {
		Queue<IntroSlide> introSlides = new LinkedList<>();
		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_formula_editor,
				R.string.formula_editor_intro_summary_formula_editor,
				NONE));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_input_field,
				R.string.formula_editor_intro_summary_input_field,
				R.id.formula_editor_edit_field));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_keyboard,
				R.string.formula_editor_intro_summary_keyboard,
				R.id.formula_editor_keyboard_7));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_compute,
				R.string.formula_editor_intro_summary_compute,
				R.id.formula_editor_keyboard_compute));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_object,
				R.string.formula_editor_intro_summary_object,
				R.id.formula_editor_keyboard_object));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_functions,
				R.string.formula_editor_intro_summary_functions,
				R.id.formula_editor_keyboard_function));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_logic,
				R.string.formula_editor_intro_summary_logic,
				R.id.formula_editor_keyboard_logic));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_device,
				R.string.formula_editor_intro_summary_device,
				R.id.formula_editor_keyboard_sensors));

		introSlides.add(new IntroSlide(
				R.string.formula_editor_intro_title_data,
				R.string.formula_editor_intro_summary_data,
				R.id.formula_editor_keyboard_data));
		return introSlides;
	}
}
