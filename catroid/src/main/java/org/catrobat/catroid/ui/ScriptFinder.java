/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptFinder extends LinearLayout {

	public static final String TAG = ScriptFinder.class.getSimpleName();

	private OnResultFoundListener onResultFoundListener;
	private OnCloseListener onCloseListener;
	private OnOpenListener onOpenListener;

	private List<Integer[]> results;

	private final EditText searchEt;
	private final TextView searchPositionIndicator;
	private final TextView sceneAndObjectName;
	private final ProgressBar progressBar;

	private final View findBtn;
	private final View findNextBtn;
	private final View findPreviousBtn;
	private final View closeBtn;

	private int indexPos;
	private String searchQuery = "";

	public ScriptFinder(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_script_finder, this, true);

		searchEt = findViewById(R.id.search_bar);
		findBtn = findViewById(R.id.find);
		findNextBtn = findViewById(R.id.find_next);
		findPreviousBtn = findViewById(R.id.find_previous);
		closeBtn = findViewById(R.id.close);
		progressBar = findViewById(R.id.progress_bar);
		sceneAndObjectName = findViewById(R.id.scene_and_sprite_name);
		searchPositionIndicator = findViewById(R.id.search_position_indicator);

		findBtn.setOnClickListener(v -> find());
		findNextBtn.setOnClickListener(v -> findNext());
		findPreviousBtn.setOnClickListener(v -> findPrevious());
		closeBtn.setOnClickListener(v -> close());

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (searchQuery.equals(s.toString().toLowerCase(Locale.ROOT))) {
					findNextBtn.setVisibility(View.VISIBLE);
					findPreviousBtn.setVisibility(View.VISIBLE);
					searchPositionIndicator.setVisibility(View.VISIBLE);
					findBtn.setVisibility(View.GONE);
				} else {
					findNextBtn.setVisibility(View.GONE);
					findPreviousBtn.setVisibility(View.GONE);
					searchPositionIndicator.setVisibility(View.GONE);
					findBtn.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};

		searchEt.addTextChangedListener(textWatcher);
	}

	private void find() {
		String query = searchEt.getText().toString().toLowerCase(Locale.ROOT);
		if (!searchQuery.equals(query)) {
			searchQuery = query;
			fillIndices(query);
			findBtn.setVisibility(View.GONE);
			findNextBtn.setVisibility(View.VISIBLE);
			findPreviousBtn.setVisibility(View.VISIBLE);
		} else if (results != null) {
			findNext();
		}
	}

	private void findNext() {
		if (!results.isEmpty()) {
			indexPos = (indexPos + 1) % results.size();
			updateUI();
		} else {
			searchPositionIndicator.setText("0/0");
			ToastUtil.showError(getContext(), getContext().getString(R.string.no_results_found));
		}
	}

	private void findPrevious() {
		if (!results.isEmpty()) {
			indexPos = indexPos == 0 ? results.size() - 1 : indexPos - 1;
			updateUI();
		} else {
			searchPositionIndicator.setText("0/0");
			ToastUtil.showError(getContext(), getContext().getString(R.string.no_results_found));
		}
	}

	private void updateUI() {
		Integer[] result = results.get(indexPos);
		searchPositionIndicator.setText(String.format(Locale.ROOT, "%d/%d", indexPos + 1,
				results.size()));
		if (onResultFoundListener != null) {
			onResultFoundListener.onResultFound(result[0], result[1], result[2], results.size(),
					sceneAndObjectName);
		}
	}

	public void fillIndices(String query) {
		indexPos = -1;
		Scene activeScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite activeSprite = ProjectManager.getInstance().getCurrentSprite();
		if (results != null) {
			results.clear();
		} else {
			results = new ArrayList<>();
		}
		new Thread(() -> {
			Activity activity = (Activity) getContext();
			if (!activity.isFinishing()) {
				activity.runOnUiThread(() -> {
					findBtn.setVisibility(View.GONE);
					findNextBtn.setVisibility(View.GONE);
					findPreviousBtn.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
				});
			}
			List<Scene> scenes = ProjectManager.getInstance().getCurrentProject().getSceneList();

			for (int i = 0; i < scenes.size(); i++) {
				Scene scene = scenes.get(i);
				List<Sprite> spriteList = scene.getSpriteList();

				for (int j = 0; j < spriteList.size(); j++) {
					Sprite sprite = spriteList.get(j);
					List<Script> scriptList = sprite.getScriptList();
					List<Brick> bricks = new ArrayList<>();
					ProjectManager.getInstance().setCurrentSceneAndSprite(scene.getName(),
							sprite.getName());
					for (Script script : scriptList) {
						script.setParents();
						script.addToFlatList(bricks);
					}

					for (int k = 0; k < bricks.size(); k++) {
						Brick brick = bricks.get(k);
						if (searchBrickViews(brick.getView(getContext()), query)) {
							results.add(new Integer[] {i, j, k});
						}
					}
				}
			}
			if (!activity.isFinishing()) {
				activity.runOnUiThread(() -> {
					findNextBtn.setVisibility(View.VISIBLE);
					findPreviousBtn.setVisibility(View.VISIBLE);
					searchPositionIndicator.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					ProjectManager.getInstance().setCurrentSceneAndSprite(activeScene.getName(),
							activeSprite.getName());
					findNext();
				});
			}
		}).start();
	}

	public static boolean searchBrickViews(View v, String searchQuery) {
		try {
			if (v instanceof Spinner) {
				Object selectedItem = ((Spinner) v).getSelectedItem();
				if (selectedItem instanceof Nameable && ((Nameable) selectedItem).getName().toLowerCase(Locale.ROOT).contains(searchQuery)) {
					return true;
				}
			} else if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					boolean queryFoundInBrick = searchBrickViews(child, searchQuery);
					if (queryFoundInBrick) {
						return true;
					}
				}
			} else if (v instanceof TextView && ((TextView) v).getText().toString().toLowerCase(Locale.ROOT).contains(searchQuery)) {
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return false;
	}

	public boolean isOpen() {
		return getVisibility() == VISIBLE;
	}

	public void open() {
		this.setVisibility(View.VISIBLE);
		InputMethodManager inputMethodManager =
				(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(searchEt.getApplicationWindowToken(),
				InputMethodManager.SHOW_FORCED, 0);
		onOpenListener.onOpen();
		searchEt.requestFocus();
	}

	public void close() {
		this.setVisibility(View.GONE);
		if (results != null) {
			results.clear();
		}
		searchEt.setText("");
		searchQuery = "";
		onCloseListener.onClose();
		ViewUtils.hideKeyboard(this);
	}

	public boolean isClosed() {
		return getVisibility() == GONE;
	}

	public void setOnResultFoundListener(OnResultFoundListener onResultFoundListener) {
		this.onResultFoundListener = onResultFoundListener;
	}

	public void setOnCloseListener(OnCloseListener onCloseListener) {
		this.onCloseListener = onCloseListener;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		this.onOpenListener = onOpenListener;
	}

	public interface OnResultFoundListener {
		void onResultFound(int sceneIndex, int spriteIndex, int brickIndex, int totalResults,
				TextView textView);
	}

	public interface OnCloseListener {
		void onClose();
	}

	public interface OnOpenListener {
		void onOpen();
	}
}
