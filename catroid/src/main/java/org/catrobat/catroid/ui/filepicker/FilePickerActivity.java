/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.filepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class FilePickerActivity extends BaseActivity implements ListProjectFilesTask.OnListProjectFilesListener {

	public static final String TAG = FilePickerActivity.class.getSimpleName();

	private static final int PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801;

	private RecyclerView recyclerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_file_picker);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.import_project);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recyclerView = findViewById(R.id.recycler_view);

		setShowProgressBar(true);
		getFiles();
	}

	private void setShowProgressBar(boolean show) {
		findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		findViewById(R.id.recycler_view).setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void getFiles() {
		new RequiresPermissionTask(PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
				Collections.singletonList(READ_EXTERNAL_STORAGE),
				R.string.runtime_permission_general) {

			@Override
			public void task() {
				new ListProjectFilesTask(FilePickerActivity.this)
						.execute(getStorageRoots().toArray(new File[0]));
			}
		}.execute(this);
	}

	private List<File> getStorageRoots() {
		List<File> rootDirs = new ArrayList<>();
		for (File externalFilesDir : getExternalFilesDirs(null)) {
			try {
				String path = externalFilesDir.getAbsolutePath();
				Log.e(TAG, externalFilesDir.canRead() + " Path: " + path);
				String packageName = getApplicationContext().getPackageName();
				path = path.replaceAll("/Android/data/" + packageName + "/files", "");
				rootDirs.add(new File(path));
			} catch (Exception e) {
				// needed for APIs 21 & 22
				Log.e(TAG, "externalFilesDir is null" + e.getMessage());
			}
		}
		return rootDirs;
	}

	@Override
	public void onListProjectFilesComplete(List<File> files) {
		setShowProgressBar(false);

		if (files.isEmpty()) {
			findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);
		} else {
			initializeAdapter(files);
		}
	}

	private void initializeAdapter(List<File> files) {
		FilePickerAdapter adapter = new FilePickerAdapter(files);
		adapter.setOnItemClickListener(new FilePickerAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(File item) {
				Intent data = new Intent();
				data.setData(Uri.fromFile(item));
				setResult(RESULT_OK, data);
				finish();
			}
		});

		recyclerView.setAdapter(adapter);
	}
}
