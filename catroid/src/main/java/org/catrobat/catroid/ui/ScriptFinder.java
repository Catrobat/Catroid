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

	private List<Integer[]> searchResults;

	private final EditText searchBar;
	private final TextView searchResultIndexTextView;
	private final TextView sceneAndObjectNameTextView;
	private final ProgressBar progressBar;

	private final View searchButton;
	private final View findNextButton;
	private final View findPreviousButton;

	private int searchResultIndex;
	private String searchQuery = "";

	public ScriptFinder(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_script_finder, this, true);

		searchBar = findViewById(R.id.search_bar);
		searchButton = findViewById(R.id.find);
		findNextButton = findViewById(R.id.find_next);
		findPreviousButton = findViewById(R.id.find_previous);
		View close = findViewById(R.id.close);
		progressBar = findViewById(R.id.progress_bar);
		sceneAndObjectNameTextView = findViewById(R.id.scene_and_sprite_name);
		searchResultIndexTextView = findViewById(R.id.search_position_indicator);

		searchButton.setOnClickListener(v -> find());
		findNextButton.setOnClickListener(v -> findNext());
		findPreviousButton.setOnClickListener(v -> findPrevious());
		close.setOnClickListener(v -> close());

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (searchQuery.equals(s.toString().toLowerCase(Locale.ROOT))) {
					findNextButton.setVisibility(View.VISIBLE);
					findPreviousButton.setVisibility(View.VISIBLE);
					searchResultIndexTextView.setVisibility(View.VISIBLE);
					searchButton.setVisibility(View.GONE);
				} else {
					findNextButton.setVisibility(View.GONE);
					findPreviousButton.setVisibility(View.GONE);
					searchResultIndexTextView.setVisibility(View.GONE);
					searchButton.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};

		searchBar.addTextChangedListener(textWatcher);
	}

	private void find() {
		String query = searchBar.getText().toString().toLowerCase(Locale.ROOT);
		if (!query.isEmpty()) {
			if (!searchQuery.equals(query)) {
				searchQuery = query;
				fillIndices(query);
				searchButton.setVisibility(View.GONE);
				findNextButton.setVisibility(View.VISIBLE);
				findPreviousButton.setVisibility(View.VISIBLE);
			} else if (searchResults != null) {
				findNext();
			}
		} else {
			ToastUtil.showError(getContext(),
					getContext().getString(R.string.query_field_is_empty));
		}
	}

	private void findNext() {
		if (!searchResults.isEmpty()) {
			searchResultIndex = (searchResultIndex + 1) % searchResults.size();
			updateUI();
		} else {
			searchResultIndexTextView.setText("0/0");
			ToastUtil.showError(getContext(), getContext().getString(R.string.no_results_found));
		}
	}

	private void findPrevious() {
		if (!searchResults.isEmpty()) {
			searchResultIndex = searchResultIndex == 0 ? searchResults.size() - 1 : searchResultIndex - 1;
			updateUI();
		} else {
			searchResultIndexTextView.setText("0/0");
			ToastUtil.showError(getContext(), getContext().getString(R.string.no_results_found));
		}
	}

	private void updateUI() {
		Integer[] result = searchResults.get(searchResultIndex);
		searchResultIndexTextView.setText(String.format(Locale.ROOT, "%d/%d", searchResultIndex + 1,
				searchResults.size()));
		if (onResultFoundListener != null) {
			onResultFoundListener.onResultFound(result[0], result[1], result[2], searchResults.size(),
					sceneAndObjectNameTextView);
		}
	}

	public void fillIndices(String query) {
		searchResultIndex = -1;
		Scene activeScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite activeSprite = ProjectManager.getInstance().getCurrentSprite();
		if (searchResults != null) {
			searchResults.clear();
		} else {
			searchResults = new ArrayList<>();
		}
		new Thread(() -> {
			Activity activity = (Activity) getContext();
			if (!activity.isFinishing()) {
				activity.runOnUiThread(() -> {
					searchButton.setVisibility(View.GONE);
					findNextButton.setVisibility(View.GONE);
					findPreviousButton.setVisibility(View.GONE);
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
							searchResults.add(new Integer[] {i, j, k});
						}
					}
				}
			}
			if (!activity.isFinishing()) {
				activity.runOnUiThread(() -> {
					findNextButton.setVisibility(View.VISIBLE);
					findPreviousButton.setVisibility(View.VISIBLE);
					searchResultIndexTextView.setVisibility(View.VISIBLE);
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
		inputMethodManager.toggleSoftInputFromWindow(searchBar.getApplicationWindowToken(),
				InputMethodManager.SHOW_FORCED, 0);
		onOpenListener.onOpen();
		searchBar.requestFocus();
	}

	public void close() {
		this.setVisibility(View.GONE);
		if (searchResults != null) {
			searchResults.clear();
		}
		searchBar.setText("");
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
