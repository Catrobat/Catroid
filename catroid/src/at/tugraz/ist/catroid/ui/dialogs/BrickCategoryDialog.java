/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickCategoryAdapter;

public class BrickCategoryDialog extends Dialog {
	private ScriptActivity activity;
	private BrickCategoryAdapter adapter;

	public BrickCategoryDialog(ScriptActivity activity) {
		super(activity);
		this.activity = activity;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_categories);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void setupBrickCategories(ListView listView) {
		LayoutInflater inflater = activity.getLayoutInflater();
		List<View> categories = new ArrayList<View>();
		categories.add(inflater.inflate(R.layout.brick_category_motion, null));
		categories.add(inflater.inflate(R.layout.brick_category_looks, null));
		categories.add(inflater.inflate(R.layout.brick_category_sound, null));
		categories.add(inflater.inflate(R.layout.brick_category_control, null));

		adapter = new BrickCategoryAdapter(categories);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		ListView listView = (ListView) findViewById(R.id.categoriesListView);
		setupBrickCategories(listView);
		final Dialog brickCategoryDialog = this;

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String selectedCategory = adapter.getItem(position);
				Dialog addBrickDialog = new AddBrickDialog(brickCategoryDialog, activity, selectedCategory);
				addBrickDialog.show();
			}
		});
	}
}
