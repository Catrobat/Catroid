/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickCategoryAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;

public class BrickCategoryDialog extends DialogFragment {

	private ListView listView;
	private BrickCategoryAdapter adapter;

	private OnCategorySelectedListener onCategorySelectedListener;
	private OnBrickCategoryDialogDismissCancelListener onBrickCategoryDialogDismissCancelListener;

	public BrickCategoryDialog() {
	}

	public void setOnBrickCategoryDialogDismissCancelListener(OnBrickCategoryDialogDismissCancelListener listener) {
		onBrickCategoryDialogDismissCancelListener = listener;
	}

	public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
		onCategorySelectedListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setStyle(STYLE_NORMAL, R.style.brick_dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_categories, null);

		ImageButton closeButton = (ImageButton) rootView.findViewById(R.id.btn_close_dialog);
		TextView textView = (TextView) rootView.findViewById(R.id.tv_dialog_title);
		listView = (ListView) rootView.findViewById(R.id.categoriesListView);

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				abort();
				dismiss();
			}
		});

		textView.setText(getString(R.string.categories));

		setupBrickCategories(listView, inflater);

		Window window = getDialog().getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL);
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

		getDialog().setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					abort();
					dismiss();
					return true;
				}

				return false;
			}
		});

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (onCategorySelectedListener != null) {
					onCategorySelectedListener.onCategorySelected(adapter.getItem(position));
				}
			}
		});
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (onBrickCategoryDialogDismissCancelListener != null) {
			onBrickCategoryDialogDismissCancelListener.onBrickCategoryDialogDismiss();
		}

		super.onDismiss(dialog);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (onBrickCategoryDialogDismissCancelListener != null) {
			onBrickCategoryDialogDismissCancelListener.onBrickCategoryDialogCancel();
		}

		super.onCancel(dialog);
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

	private void abort() {
		ScriptTabActivity activity = ((ScriptTabActivity) getActivity());
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		fragment.setDontCreateNewBrick(true);
	}

	private void setupBrickCategories(ListView listView, LayoutInflater inflater) {
		List<View> categories = new ArrayList<View>();
		categories.add(inflater.inflate(R.layout.brick_category_motion, null));
		categories.add(inflater.inflate(R.layout.brick_category_looks, null));
		categories.add(inflater.inflate(R.layout.brick_category_sound, null));
		categories.add(inflater.inflate(R.layout.brick_category_control, null));

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_nxt, null));
		}
		adapter = new BrickCategoryAdapter(categories);
		listView.setAdapter(adapter);
	}

	public interface OnCategorySelectedListener {

		public void onCategorySelected(String category);

	}

	public interface OnBrickCategoryDialogDismissCancelListener {

		public void onBrickCategoryDialogDismiss();

		public void onBrickCategoryDialogCancel();

	}
}
