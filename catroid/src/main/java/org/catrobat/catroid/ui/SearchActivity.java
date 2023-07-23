/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ImageButton backBtn = findViewById(R.id.backBtn);
		ImageButton clearBtn = findViewById(R.id.clearBtn);
		TextView noResTxt = findViewById(R.id.emptyTxt);
		TextView localTxt = findViewById(R.id.localHeaderTxt);
		TextView communityTxt = findViewById(R.id.communityHeaderTxt);
		RecyclerView localListView = findViewById(R.id.localProjectsList);
		RecyclerView communityListView = findViewById(R.id.communityProjectsList);
		Chip deviceChip = findViewById(R.id.deviceChip);
		Chip communityChip = findViewById(R.id.communityChip);
		communityTxt.setVisibility(View.GONE);
		localTxt.setVisibility(View.GONE);
		localListView.setVisibility(View.GONE);
		communityListView.setVisibility(View.GONE);
		clearBtn.setVisibility(View.GONE);
		EditText searchText = findViewById(R.id.searchText);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		deviceChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked && localListView.getVisibility() == View.VISIBLE) {
					localTxt.setVisibility(View.GONE);
					localListView.setVisibility(View.GONE);
				}
			}
		});
		communityChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked && communityListView.getVisibility() == View.VISIBLE) {
					communityTxt.setVisibility(View.GONE);
					communityListView.setVisibility(View.GONE);
				}
			}
		});
		clearBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchText.setText("");
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View viev) {
				finish();
			}
		});
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count > 0) {
					Log.d("count", "onTextChanged: count >0");
					clearBtn.setVisibility(View.VISIBLE);
					getLocalProjectsForSearchTerm(s);
					Log.d("devicechip", "" + deviceChip.isChecked());
					if (deviceChip.isChecked()) {
						Log.d("", "onTextChanged: in here");
						noResTxt.setVisibility(View.GONE);
						localTxt.setVisibility(View.VISIBLE);
						localListView.setAdapter(new ProjectAdapter(getLocalProjectsForSearchTerm(s)));
						localListView.setLayoutManager(llm);
						localListView.setVisibility(View.VISIBLE);
					}
				} else {
					clearBtn.setVisibility(View.GONE);
				}
				Log.d("searchText", "onTextChanged: " + s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private List<ProjectData> getLocalProjectsForSearchTerm(CharSequence searchTerm) {
		List<ProjectData> data = new ArrayList<>();
		File[] files = FlavoredConstants.DEFAULT_ROOT_DIRECTORY.listFiles();
		for (File file : files) {
			File xmlFile = new File(file, Constants.CODE_XML_FILE_NAME);
			if (!xmlFile.exists()) {
				continue;
			}
			ProjectMetaDataParser parser = new ProjectMetaDataParser(xmlFile);
			try {
				Log.d("Project found", parser.getProjectMetaData().getName());
				if (parser.getProjectMetaData().getName().contains(searchTerm)) {
					Log.d("Project added", parser.getProjectMetaData().getName());
					data.add(parser.getProjectMetaData());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return data;
	}

	private List<ProjectData> getCommunityProjectsForSearchTerm(String searchTerm) {
		List<ProjectData> data = new ArrayList<>();

		return data;
	}
}