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

import android.content.Intent;
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
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectRenameTask;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.transfers.GetTagsTask;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.SignInActivity;
import org.catrobat.catroid.ui.dialogs.SelectTagsDialogFragment;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.PROJECT_UPLOAD_DESCRIPTION;
import static org.catrobat.catroid.common.Constants.PROJECT_UPLOAD_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectUploadActivity extends BaseActivity implements
		ProjectLoadTask.ProjectLoadListener,
		CheckTokenTask.TokenCheckListener,
		GetTagsTask.TagResponseListener {

	public static final String TAG = ProjectUploadActivity.class.getSimpleName();
	public static final String PROJECT_NAME = "projectName";
	public static final int SIGN_IN_CODE = 42;

	private TextChangedListener textChangedListener = new TextChangedListener();
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
			ProjectLoadTask loaderTask = new ProjectLoadTask(this, this);
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
				&& !nameInputLayout.getEditText().getText().toString().isEmpty()
				&& nameInputLayout.getError() == null;
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
	public void onLoadFinished(boolean success) {
		if (success) {
			getTags();
			verifyUserIdentity();
		} else {
			ToastUtil.showError(this, R.string.error_load_project);
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
			startSignInWorkflow();
		}
	}

	private void onCreateView() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		ProjectAndSceneScreenshotLoader screenshotLoader = new ProjectAndSceneScreenshotLoader(this);
		screenshotLoader.loadAndShowScreenshot(currentProject.getName(),
				currentProject.getDefaultScene().getName(),
				false,
				(ImageView) findViewById(R.id.project_image_view));

		((TextView) findViewById(R.id.project_size_view)).setText(FileMetaDataExtractor.getSizeAsString(
				new File(PathBuilder.buildProjectPath(currentProject.getName())), this));

		nameInputLayout = findViewById(R.id.input_project_name);
		descriptionInputLayout = findViewById(R.id.input_project_description);

		nameInputLayout.getEditText().setText(currentProject.getName());
		descriptionInputLayout.getEditText().setText(currentProject.getDescription());

		nameInputLayout.getEditText().addTextChangedListener(textChangedListener);
		invalidateOptionsMenu();
	}

	private void onNextButtonClick() {
		String name = nameInputLayout.getEditText().getText().toString().trim();
		String description = descriptionInputLayout.getEditText().getText().toString().trim();

		String error = textChangedListener.validateInput(name);

		if (error != null) {
			nameInputLayout.setError(error);
			invalidateOptionsMenu();
			return;
		}

		Project project = ProjectManager.getInstance().getCurrentProject();

		if (Utils.isDefaultProject(project, this)) {
			nameInputLayout.setError(getString(R.string.error_upload_default_project));
			return;
		}

		if (ProjectRenameTask.task(project.getName(), name)) {
			ProjectLoadTask.task(name, this);
			project = ProjectManager.getInstance().getCurrentProject();
		}

		project.setDescription(description);
		project.setDeviceData(this);

		Bundle bundle = new Bundle();
		bundle.putString(PROJECT_UPLOAD_NAME, name);
		bundle.putString(PROJECT_UPLOAD_DESCRIPTION, description);

		SelectTagsDialogFragment dialog = new SelectTagsDialogFragment();
		dialog.setTags(tags);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), SelectTagsDialogFragment.TAG);
	}

	@Override
	public void onTokenCheckComplete(boolean tokenValid, boolean connectionFailed) {
		if (connectionFailed) {
			ToastUtil.showError(this, R.string.error_internet_connection);
			finish();
		} else if (!tokenValid) {
			startSignInWorkflow();
		} else {
			onCreateView();
			setShowProgressBar(false);
		}
	}

	public void startSignInWorkflow() {
		startActivityForResult(new Intent(this, SignInActivity.class), SIGN_IN_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SIGN_IN_CODE) {
			if (resultCode == RESULT_OK) {
				onCreateView();
				setShowProgressBar(false);
			} else {
				finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
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

		@Nullable
		public String validateInput(String input) {
			if (input.isEmpty()) {
				return getString(R.string.name_empty);
			}

			input = input.trim();

			if (input.isEmpty()) {
				return getString(R.string.name_consists_of_spaces_only);
			}
			if (input.equals(getString(R.string.default_project_name))) {
				return getString(R.string.error_upload_project_with_default_name);
			}
			if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(input)) {
				return getString(R.string.name_already_exists);
			}

			return null;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String input = s.toString();
			nameInputLayout.setError(validateInput(input));
			invalidateOptionsMenu();
		}
	}
}
