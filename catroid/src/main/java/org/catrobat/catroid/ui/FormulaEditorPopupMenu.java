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

package org.catrobat.catroid.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.catrobat.catroid.formulaeditor.InternToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FormulaEditorPopupMenu {
	private final PopupWindow popupWindow;

	private final View formulaEditorEditText;
	private OnUpdateListener onUpdateListener;

	private final View cut;
	private final View copy;
	private final View paste;

	private List<InternToken> clipboard;

	@SuppressLint("InflateParams")
	public FormulaEditorPopupMenu(Context context, FormulaEditorEditText formulaEditorEditText) {
		this.formulaEditorEditText = formulaEditorEditText;

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.formula_editor_pop_up_menu, null);

		popupWindow = new PopupWindow(popupView, WRAP_CONTENT, WRAP_CONTENT, false);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setElevation(10);

		cut = popupView.findViewById(R.id.cut);
		copy = popupView.findViewById(R.id.copy);
		paste = popupView.findViewById(R.id.paste);

		cut.setOnClickListener(v -> {
			copyTokens(formulaEditorEditText.getSelectedTokens());
			formulaEditorEditText.deleteSelection();
			if (onUpdateListener != null) {
				onUpdateListener.onUpdate();
			}
			popupWindow.dismiss();
		});

		copy.setOnClickListener(v -> {
			copyTokens(formulaEditorEditText.getSelectedTokens());
			popupWindow.dismiss();
		});

		paste.setOnClickListener(v -> {
			if (clipboard != null && clipboard.size() > 0) {
				formulaEditorEditText.addTokens(cloneTokens(clipboard));
				if (onUpdateListener != null) {
					onUpdateListener.onUpdate();
				}
			}
			popupWindow.dismiss();
		});

		View.OnTouchListener onTouchListener = getOnTouchListener();
		cut.setOnTouchListener(onTouchListener);
		copy.setOnTouchListener(onTouchListener);
		paste.setOnTouchListener(onTouchListener);
		popupWindow.getContentView().setOnTouchListener(onTouchListener);
	}

	private View.OnTouchListener getOnTouchListener() {
		return new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						int[] location = new int[2];
						popupWindow.getContentView().getLocationOnScreen(location);
						initialX = location[0];
						initialY = location[1];
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						return true;
					case MotionEvent.ACTION_UP:
						if (Math.abs(event.getRawX() - initialTouchX) <= 5 && Math.abs(event.getRawY() - initialTouchY) <= 5) {
							v.performClick();
						}
						return true;
					case MotionEvent.ACTION_MOVE:
						int x = initialX + (int) (event.getRawX() - initialTouchX);
						int y = initialY + (int) (event.getRawY() - initialTouchY);
						popupWindow.update(x, y, -1, -1, true);
						return true;
				}
				return false;
			}
		};
	}

	private List<InternToken> cloneTokens(List<InternToken> tokens) {
		List<InternToken> t = new ArrayList<>();
		for (InternToken token : tokens) {
			t.add(token.deepCopy());
		}
		return t;
	}

	private void copyTokens(List<InternToken> tokens) {
		clipboard = cloneTokens(tokens);
	}

	public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
		this.onUpdateListener = onUpdateListener;
	}

	public void show(int x, int y, boolean isHighlighted) {
		boolean isClipboardEmpty = (clipboard == null || clipboard.isEmpty());

		if (isClipboardEmpty && !isHighlighted) {
			return;
		}

		if (isClipboardEmpty) {
			paste.setVisibility(GONE);
		} else {
			paste.setVisibility(VISIBLE);
		}

		if (isHighlighted) {
			copy.setVisibility(VISIBLE);
			cut.setVisibility(VISIBLE);
		} else {
			copy.setVisibility(GONE);
			cut.setVisibility(GONE);
		}

		popupWindow.showAtLocation(formulaEditorEditText, Gravity.NO_GRAVITY, x, y);
	}

	public interface OnUpdateListener {
		void onUpdate();
	}

	public boolean isVisible() {
		return popupWindow.isShowing();
	}

	public void dismiss() {
		popupWindow.dismiss();
	}
}
