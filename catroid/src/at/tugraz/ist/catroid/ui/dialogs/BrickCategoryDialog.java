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

import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickCategoryAdapter;

public class BrickCategoryDialog extends Dialog {
	private ScriptTabActivity activity;
	private BrickCategoryAdapter adapter;

	public BrickCategoryDialog(ScriptTabActivity activity) {
		super(activity, R.style.brick_dialog);
		this.activity = activity;

		Window window = getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		//window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL);

		setContentView(R.layout.dialog_categories);
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

		ImageButton closeButton = (ImageButton) findViewById(R.id.btn_close_dialog);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				abort();
				dismiss();
			}
		});

		TextView textView = (TextView) findViewById(R.id.tv_dialog_title);
		textView.setText(activity.getString(R.string.categories));
	}

	private void abort() {
		activity.setDontcreateNewBrick();
	}

	private void setupBrickCategories(ListView listView) {
		LayoutInflater inflater = activity.getLayoutInflater();
		List<View> categories = new ArrayList<View>();
		categories.add(inflater.inflate(R.layout.brick_category_motion, null));
		categories.add(inflater.inflate(R.layout.brick_category_looks, null));
		categories.add(inflater.inflate(R.layout.brick_category_sound, null));
		categories.add(inflater.inflate(R.layout.brick_category_control, null));

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_nxt, null));
		}
		adapter = new BrickCategoryAdapter(categories);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		ListView listView = (ListView) findViewById(R.id.categoriesListView);
		setupBrickCategories(listView);

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.selectedCategory = adapter.getItem(position);
				activity.removeDialog(ScriptTabActivity.DIALOG_ADD_BRICK);
				activity.showDialog(ScriptTabActivity.DIALOG_ADD_BRICK);
			}
		});
	}
}
