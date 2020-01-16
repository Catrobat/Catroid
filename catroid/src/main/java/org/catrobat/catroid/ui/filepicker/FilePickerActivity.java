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

import android.os.Bundle;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

public class FilePickerActivity extends BaseActivity  {

	public static final String TAG = FilePickerActivity.class.getSimpleName();

	private static final int PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801;

	private RecyclerView recyclerView;
	private FilePickerViewModel viewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_file_picker);
		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.import_project);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recyclerView = findViewById(R.id.recycler_view);

		viewModel = new ViewModelProvider(this, new FilePickerViewModelFactory())
				.get(FilePickerViewModel.class);

		viewModel.getExternalFiles().observe(this, files -> {
			if (files == null || files.isEmpty()) {
				findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
				recyclerView.setVisibility(View.GONE);
			} else {
				initializeAdapter(files);
			}
		});

		viewModel.getIsBusy().observe(this, this::setShowProgressBar);

		viewModel.getIsImportFinished().observe(this, success -> {
			if (success == null) {
				return;
			}

			if (success) {
				setResult(RESULT_OK);
			} else {
				setResult(RESULT_CANCELED);
			}
			finish();
		});

		viewModel.getIsInitialised().observe(this, isInitialised -> {
			if (!isInitialised) {
				viewModel.getFiles(PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE, FilePickerActivity.this, this::getStorageRoots);
			}
		});

	}

	private void setShowProgressBar(boolean show) {
		findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		findViewById(R.id.recycler_view).setVisibility(show ? View.GONE : View.VISIBLE);
	}


	private void initializeAdapter(List<File> files) {
		FilePickerAdapter filePickerAdapter = new FilePickerAdapter(files);
		filePickerAdapter.setOnItemClickListener(item -> viewModel.importProjectFromFile(item, getContentResolver()));

		recyclerView.setAdapter(filePickerAdapter);
	}

	private List<File> getStorageRoots() {
		List<File> rootDirs = new ArrayList<>();
		for (File externalFilesDir : getExternalFilesDirs(null)) {
			String path = externalFilesDir.getAbsolutePath();
			String packageName = getApplicationContext().getPackageName();
			path = path.replaceAll("/Android/data/" + packageName + "/files", "");
			rootDirs.add(new File(path));
		}
		return rootDirs;
	}


}
