/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import android.content.ContentResolver;
import android.net.Uri;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.ProjectImportController;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class FilePickerViewModel extends ViewModel {

	private MutableLiveData<Boolean> isBusy = new MutableLiveData<>(false);
	private MutableLiveData<Boolean> isInitialised = new MutableLiveData<>(false);
	private MutableLiveData<List<File>> externalFiles = new MutableLiveData<>();
	private MutableLiveData<Boolean> isImportFinished = new MutableLiveData<>(false);

	public MutableLiveData<List<File>> getExternalFiles() {
		return externalFiles;
	}

	public MutableLiveData<Boolean> getIsInitialised() {
		return isInitialised;
	}

	public MutableLiveData<Boolean> getIsImportFinished() {
		return isImportFinished;
	}

	public LiveData<Boolean> getIsBusy() {
		return isBusy;
	}

	public interface StorageRootsProvider {
		List<File> get();
	}

	public void getFiles(final int permissionRequestId, AppCompatActivity activity, StorageRootsProvider storageRoots) {
		isBusy.setValue(true);
		isInitialised.setValue(true);

		new RequiresPermissionTask(permissionRequestId,
				Arrays.asList(READ_EXTERNAL_STORAGE),
				R.string.runtime_permission_general) {

			@Override
			public void task() {
				new ListProjectFilesTask(files -> onListProjectFilesComplete(files))
						.execute(storageRoots.get().toArray(new File[0]));
			}
		}.execute(activity);
	}



	private void onListProjectFilesComplete(List<File> files) {
		isBusy.setValue(false);
		externalFiles.setValue(files);
	}

	public void importProjectFromFile(File fileToImport, ContentResolver contentResolver) {
		isBusy.setValue(true);

		ProjectImportController projectImportController =
				new ProjectImportController(contentResolver, this::onImportFinished);

		Uri uriOfFile = Uri.fromFile(fileToImport);
		projectImportController.startImportOfProject(uriOfFile);
	}

	private void onImportFinished(boolean success) {
		isBusy.setValue(false);
		isImportFinished.setValue(success);
	}
}
