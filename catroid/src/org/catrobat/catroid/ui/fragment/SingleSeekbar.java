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

package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;

public class SingleSeekbar {

	private final FormulaBrick formulaBrick;
	private final Brick.BrickField seekbarField;
	private int seekbarTitleId;

	private TextView formulaEditorEditTextSeekbarValue;
	private View seekbarView;

	private SeekBar speedSeekBar;

	public SingleSeekbar(FormulaBrick formulaBrick,
			Brick.BrickField seekbarField, int seekbarTitleId) {
		this.formulaBrick = formulaBrick;
		this.seekbarField = seekbarField;
		this.seekbarTitleId = seekbarTitleId;
	}

	public View getView(Context context) {
		seekbarView = View.inflate(context, R.layout.single_seekbar_value_chooser, null);
		seekbarView.setFocusableInTouchMode(true);
		seekbarView.requestFocus();

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				FormulaEditorFragment.showFragment(view, formulaBrick, seekbarField);
			}
		};

		formulaEditorEditTextSeekbarValue = (TextView) seekbarView.findViewById(R.id.single_seekbar_value);
		formulaEditorEditTextSeekbarValue.setOnClickListener(onClickListener);
		formulaBrick.getFormulaWithBrickField(seekbarField).setTextFieldId(R.id.single_seekbar_value);
		formulaBrick.getFormulaWithBrickField(seekbarField).refreshTextField(seekbarView);

		speedSeekBar = (SeekBar) seekbarView.findViewById(R.id.single_seekbar_seekbar);

		TextView seekbarTitle = (TextView) seekbarView.findViewById(R.id.single_seekbar_title);
		seekbarTitle.setText(seekbarTitleId);

		speedSeekBar.setProgress(getCurrentBrickFieldValue());

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				formulaEditorEditTextSeekbarValue.setText(String.valueOf(seekBar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress()));
			}
		};

		speedSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		return seekbarView;
	}

	private int getCurrentBrickFieldValue() {
		String stringValue = formulaBrick.getFormulaWithBrickField(seekbarField).getDisplayString(seekbarView.getContext());
		return Double.valueOf(stringValue.replace(",", ".")).intValue();
	}
}
