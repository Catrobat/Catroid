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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.achievements.Achievement;
import org.catrobat.catroid.content.achievements.AchievementListAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

public class AchievementsListActivity extends BaseActivity {


	ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.achievements_list);

		listView = (ListView) findViewById(R.id.achievementsListView);
		Achievement Start = new Achievement("Start" , R.drawable.ic_main_menu_achievements_button);
		Achievement End = new Achievement("End",  R.drawable.ic_main_menu_achievements_button);

		ArrayList<Achievement> AchievementList = new ArrayList<>();
		AchievementList.add(Start);
		for (int i = 0; i < 40; i++) {
			AchievementList.add(new Achievement("Progress "+ Integer.toString(i), R.drawable.ic_main_menu_achievements_button));
		}

		AchievementList.add(End);

		AchievementListAdapter adapter = new AchievementListAdapter(this,
				R.layout.achievement_single_item, AchievementList);
		listView.setAdapter(adapter);
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(AchievementsListActivity.this,AchievementActivity.class);


				intent.putExtra("Image", AchievementList.get(i).getDrawable());
				intent.putExtra("Title", AchievementList.get(i).getTitle());
				intent.putExtra("Description",AchievementList.get(i).getDescription());
				startActivity(intent);
			}
		});



		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.achievements_list_title);
	}

	@Override
	protected void onResume() {
		super.onResume();
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}
}

