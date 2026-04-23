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

package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;

public class SingleSeekBar {

	private FormulaBrick formulaBrick;
	private Brick.BrickField brickField;
	private int seekBarTitleId;

	private TextView valueTextView;

	public SingleSeekBar(FormulaBrick formulaBrick, Brick.BrickField brickField, int seekBarTitleId) {
		this.formulaBrick = formulaBrick;
		this.brickField = brickField;
		this.seekBarTitleId = seekBarTitleId;
	}

	public View getView(final Context context) {
		View view = View.inflate(context, R.layout.single_seek_bar_view, null);
		view.setFocusableInTouchMode(true);
		view.requestFocus();

		TextView seekBarTitle = view.findViewById(R.id.single_seekbar_title);
		seekBarTitle.setText(seekBarTitleId);

		valueTextView = view.findViewById(R.id.single_seekbar_value);
		valueTextView.setOnClickListener(view1 -> FormulaEditorFragment.showFragment(context, formulaBrick, brickField));

		SeekBar seekBar = view.findViewById(R.id.single_seekbar_seekbar);
		String currentStringValue = formulaBrick.getFormulaWithBrickField(brickField).getTrimmedFormulaString(context);
		seekBar.setProgress(Double.valueOf(currentStringValue.replace(",", ".")).intValue());
		valueTextView.setText(String.valueOf(seekBar.getProgress()));
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				valueTextView.setText(String.valueOf(seekBar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				formulaBrick.setFormulaWithBrickField(brickField, new Formula(seekBar.getProgress()));
			}
		});

		return view;
	}
}
