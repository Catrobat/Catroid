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

package org.catrobat.catroid.ui.recyclerview.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.transfers.GetTagsTask;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.dialogs.SelectTagsDialogFragment;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInDialog;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ServerCalls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectUploadActivity extends BaseActivity implements
		ProjectLoaderTask.ProjectLoaderListener,
		SignInDialog.SignInCompleteListener,
		CheckTokenTask.TokenCheckListener,
		GetTagsTask.TagResponseListener {

	public static final String TAG = ProjectUploadActivity.class.getSimpleName();
	public static final String PROJECT_NAME = "projectName";

	private TextInputLayout nameInputLayout;
	private TextInputLayout descriptionInputLayout;

	private List<String> tags = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_upload);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.upload_project_dialog_title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setShowProgressBar(true);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			ProjectLoaderTask loaderTask = new ProjectLoaderTask(this, this);
			loaderTask.execute(bundle.getString(PROJECT_NAME));
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_upload, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean enableNextButton = nameInputLayout != null
				&& !nameInputLayout.getEditText().getText().toString().isEmpty();
		menu.findItem(R.id.next).setEnabled(enableNextButton);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.next:
				onNextButtonClick();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		if (success) {
			getTags();
			verifyUserIdentity();
		} else {
			ToastUtil.showError(this, message);
			finish();
		}
	}

	private void verifyUserIdentity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		boolean isTokenSetInPreferences = !token.equals(Constants.NO_TOKEN)
				&& token.length() == ServerCalls.TOKEN_LENGTH
				&& !token.equals(ServerCalls.TOKEN_CODE_INVALID);

		if (isTokenSetInPreferences) {
			new CheckTokenTask(this).execute(token, username);
		} else {
			SignInDialog dialog = new SignInDialog();
			dialog.setSignInCompleteListener(this);
			dialog.show(getFragmentManager(), SignInDialog.TAG);
		}
	}

	private void onCreateView() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		ProjectAndSceneScreenshotLoader screenshotLoader = new ProjectAndSceneScreenshotLoader(this);
		screenshotLoader.loadAndShowScreenshot(currentProject.getName(), currentProject.getDefaultScene().getName(),
				false, (ImageView) findViewById(R.id.project_image_view));

		((TextView) findViewById(R.id.project_size_view))
				.setText(FileMetaDataExtractor.getSizeAsString(new File(PathBuilder.buildProjectPath(currentProject.getName())), this));

		nameInputLayout = findViewById(R.id.input_project_name);
		descriptionInputLayout = findViewById(R.id.input_project_description);

		nameInputLayout.getEditText().setText(currentProject.getName());
		descriptionInputLayout.getEditText().setText(currentProject.getDescription());

		nameInputLayout.getEditText().addTextChangedListener(new TextChangedListener());
		invalidateOptionsMenu();
	}

	private void onNextButtonClick() {
		String name = nameInputLayout.getEditText().getText().toString().trim();
		String description = descriptionInputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			nameInputLayout.setError(getString(R.string.error_no_program_name_entered));
			return;
		}

		if (name.equals(getString(R.string.default_project_name))) {
			nameInputLayout.setError(getString(R.string.error_upload_project_with_default_name));
			return;
		}

		//TODO: check if project is same as default project.
		ProjectManager projectManager = ProjectManager.getInstance();
		if (!name.equals(projectManager.getCurrentProject().getName())) {
			projectManager.renameProject(name, this);
		}

		projectManager.getCurrentProject().setDescription(description);
		projectManager.getCurrentProject().setDeviceData(this);

		Bundle bundle = new Bundle();
		bundle.putString(Constants.PROJECT_UPLOAD_NAME, name);
		bundle.putString(Constants.PROJECT_UPLOAD_DESCRIPTION, description);

		SelectTagsDialogFragment dialog = new SelectTagsDialogFragment();
		dialog.setTags(tags);
		dialog.setArguments(bundle);
		dialog.show(getFragmentManager(), SelectTagsDialogFragment.TAG);
	}

	@Override
	public void onTokenCheckComplete(boolean tokenValid, boolean connectionFailed) {
		if (connectionFailed) {
			ToastUtil.showError(this, R.string.error_internet_connection);
			finish();
		} else if (!tokenValid) {
			SignInDialog dialog = new SignInDialog();
			dialog.setSignInCompleteListener(this);
			dialog.show(getFragmentManager(), SignInDialog.TAG);
		} else {
			onCreateView();
			setShowProgressBar(false);
		}
	}

	@Override
	public void onLoginSuccessful(Bundle bundle) {
		onCreateView();
		setShowProgressBar(false);
	}

	@Override
	public void onLoginCancel() {
		finish();
	}

	public void setShowProgressBar(boolean show) {
		findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		findViewById(R.id.upload_layout).setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void getTags() {
		GetTagsTask getTagsTask = new GetTagsTask();
		getTagsTask.setOnTagsResponseListener(this);
		getTagsTask.execute();
	}

	@Override
	public void onTagsReceived(List<String> tags) {
		this.tags = tags;
	}

	private class TextChangedListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			nameInputLayout.setError(null);
			invalidateOptionsMenu();
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}
}
