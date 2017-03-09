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
package org.catrobat.catroid.ui.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.utils.UtilUi;

public abstract class CheckBoxListFragment extends ListFragment implements CheckBoxListAdapter.ListItemCheckHandler {

	public static final String TAG = CheckBoxListFragment.class.getSimpleName();

	protected CheckBoxListAdapter adapter;

	protected ActionMode actionMode;

	protected String actionModeTitle;
	protected String singleItemTitle;
	protected String multipleItemsTitle;

	protected boolean isRenameActionMode = false;

	protected CapitalizedTextView selectAllView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isActionModeActive()) {
			actionMode.finish();
			actionMode = null;
		}
	}

	@Override
	public void setListAdapter(ListAdapter adapter) {
		super.setListAdapter(adapter);
		this.adapter = (CheckBoxListAdapter) adapter;
	}

	protected boolean isActionModeActive() {
		return actionMode != null;
	}

	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	public void setShowDetails(boolean showDetails) {
		adapter.setShowDetails(showDetails);
	}

	public boolean getShowDetails() {
		return adapter.getShowDetails();
	}

	public void clearCheckedItems() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.setAllItemsCheckedTo(false);

		actionMode = null;
		BottomBar.showBottomBar(getActivity());
	}

	@Override
	public void onItemChecked() {
		if (isRenameActionMode || actionMode == null) {
			return;
		}
		updateActionModeTitle();
		updateSelectAllView();
	}

	protected void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getCheckedItems().size();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
			return;
		}

		String itemCount = Integer.toString(numberOfSelectedItems);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color));

		String completeTitle = actionModeTitle + " " + itemCount + " ";

		if (numberOfSelectedItems == 1) {
			completeTitle += singleItemTitle;
		} else {
			completeTitle += multipleItemsTitle;
		}

		Spannable completeSpannedTitle = new SpannableString(completeTitle);
		completeSpannedTitle.setSpan(colorSpan, actionModeTitle.length() + 1,
				actionModeTitle.length() + (1 + itemCount.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		actionMode.setTitle(completeSpannedTitle);
	}

	protected void addSelectAllActionModeButton(final ActionMode mode, Menu menu) {
		final View selectAllActionModeButton = UtilUi.addSelectAllActionModeButton(getActivity().getLayoutInflater(),
				mode, menu);
		selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

		selectAllActionModeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (areAllItemsChecked()) {
					adapter.setAllItemsCheckedTo(false);
				} else {
					adapter.setAllItemsCheckedTo(true);
				}
				updateSelectAllView();
			}
		});
	}

	protected void updateSelectAllView() {
		if (areAllItemsChecked()) {
			selectAllView.setText(R.string.deselect_all);
		} else {
			selectAllView.setText(R.string.select_all);
		}
	}

	private boolean areAllItemsChecked() {
		return adapter.getCheckedItems().size() == adapter.getCount();
	}

	protected void showError(int messageId) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.error)
				.setMessage(messageId)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int id) {
					}
				})
				.setCancelable(false)
				.show();
	}
}
