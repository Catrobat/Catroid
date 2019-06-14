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

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.SignInActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.NO_OAUTH_PROVIDER;
import static org.catrobat.catroid.common.Constants.PLAY_STORE_PAGE_LINK;
import static org.catrobat.catroid.common.Constants.SHARE_PROGRAM_URL;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectUploadActivity extends BaseActivity implements
		ProjectLoadTask.ProjectLoadListener,
		CheckTokenTask.TokenCheckListener,
		GetTagsTask.TagResponseListener {

	public static final String TAG = ProjectUploadActivity.class.getSimpleName();
	public static final String PROJECT_DIR = "projectDir";
	public static final int SIGN_IN_CODE = 42;

	public static final int MAX_NUMBER_OF_TAGS_CHECKED = 3;
	public static final String NUMBER_OF_UPLOADED_PROJECTS = "number_of_uploaded_projects";

	private Project project;

	private AlertDialog uploadProgressDialog;

	private NameInputTextWatcher nameInputTextWatcher = new NameInputTextWatcher();

	private TextInputLayout nameInputLayout;
	private TextInputLayout descriptionInputLayout;

	private boolean enableNextButton = true;

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
			File projectDir = (File) bundle.getSerializable(PROJECT_DIR);
			new ProjectLoadTask(projectDir, this)
					.setListener(this)
					.execute();
		} else {
			finish();
		}
	}

	@Override
	public void onLoadFinished(boolean success) {
		if (success) {
			getTags();
			verifyUserIdentity();
			project = ProjectManager.getInstance().getCurrentProject();
		} else {
			ToastUtil.showError(this, R.string.error_load_project);
			setShowProgressBar(false);
			finish();
		}
	}

	private void onCreateView() {
		ProjectAndSceneScreenshotLoader screenshotLoader = new ProjectAndSceneScreenshotLoader(this);
		screenshotLoader.loadAndShowScreenshot(project.getDirectory().getName(),
				project.getDirectory().getName(),
				false,
				(ImageView) findViewById(R.id.project_image_view));

		TextView projectSizeView = findViewById(R.id.project_size_view);
		projectSizeView
				.setText(FileMetaDataExtractor.getSizeAsString(project.getDirectory(), this));

		nameInputLayout = findViewById(R.id.input_project_name);
		descriptionInputLayout = findViewById(R.id.input_project_description);

		nameInputLayout.getEditText().setText(project.getName());
		descriptionInputLayout.getEditText().setText(project.getDescription());

		nameInputLayout.getEditText().addTextChangedListener(nameInputTextWatcher);

		setShowProgressBar(false);
		setNextButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_upload, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
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

	public void setShowProgressBar(boolean show) {
		findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		findViewById(R.id.upload_layout).setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void setNextButtonEnabled(boolean enabled) {
		enableNextButton = enabled;
		invalidateOptionsMenu();
	}

	private void onNextButtonClick() {
		setNextButtonEnabled(false);

		String name = nameInputLayout.getEditText().getText().toString().trim();
		String error = nameInputTextWatcher.validateName(name);

		if (error != null) {
			nameInputLayout.setError(error);
			return;
		}

		setShowProgressBar(true);

		if (Utils.isDefaultProject(project, this)) {
			nameInputLayout.setError(getString(R.string.error_upload_default_project));
			nameInputLayout.getEditText().removeTextChangedListener(nameInputTextWatcher);
			nameInputLayout.setEnabled(false);
			descriptionInputLayout.setEnabled(false);
			setShowProgressBar(false);
			return;
		}

		showSelectTagsDialog();
	}

	private void showSelectTagsDialog() {
		final List<String> checkedTags = new ArrayList<>();
		final String[] availableTags = tags.toArray(new String[tags.size()]);

		new AlertDialog.Builder(this)
				.setTitle(R.string.upload_tag_dialog_title)
				.setMultiChoiceItems(availableTags, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
						if (isChecked) {
							if (checkedTags.size() >= MAX_NUMBER_OF_TAGS_CHECKED) {
								((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
							} else {
								checkedTags.add(availableTags[indexSelected]);
							}
						} else {
							checkedTags.remove(availableTags[indexSelected]);
						}
					}
				})
				.setPositiveButton(getText(R.string.next), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						project.setTags(checkedTags);
						showProgressDialogAndUploadProject();
					}
				})
				.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.invalidateLoginTokenIfUserRestricted(ProjectUploadActivity.this);
						setShowProgressBar(false);
						setNextButtonEnabled(true);
					}
				})
				.setCancelable(false)
				.show();
	}

	private void showProgressDialogAndUploadProject() {
		final String name = nameInputLayout.getEditText().getText().toString().trim();
		String description = descriptionInputLayout.getEditText().getText().toString().trim();

		if (!project.getName().equals(name)) {
			try {
				File renamedDirectory = ProjectRenameTask.task(project.getDirectory(), name);
				ProjectLoadTask.task(renamedDirectory, this);
				project = ProjectManager.getInstance().getCurrentProject();
			} catch (IOException e) {
				Log.e(TAG, "Creating renamed directory failed!", e);
			}
		}

		project.setDescription(description);
		project.setDeviceData(this);

		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		Intent intent = new Intent(this, ProjectUploadService.class);

		String[] sceneNames = project.getSceneNames().toArray(new String[0]);

		final int notificationId = StatusBarNotificationManager.getInstance()
				.createUploadNotification(this, name);

		UploadStatusPollingTask uploadStatusPollingTask = new UploadStatusPollingTask();

		uploadProgressDialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.upload_project_dialog_title))
				.setView(R.layout.dialog_upload_project_progress)
				.setPositiveButton(R.string.progress_upload_dialog_show_program, null)
				.setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						uploadStatusPollingTask.cancel(false);
						finish();
					}
				})
				.setCancelable(false)
				.create();

		uploadProgressDialog.show();
		uploadProgressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		intent.putExtra("uploadName", name);
		intent.putExtra("projectDescription", description);
		intent.putExtra("projectPath", project.getDirectory().getAbsolutePath());
		intent.putExtra("username", username);
		intent.putExtra("token", token);
		intent.putExtra("provider", NO_OAUTH_PROVIDER);
		intent.putExtra("sceneNames", sceneNames);
		intent.putExtra("notificationId", notificationId);

		startService(intent);

		uploadStatusPollingTask.execute();

		int numberOfUploadedProjects = sharedPreferences.getInt(NUMBER_OF_UPLOADED_PROJECTS, 0) + 1;
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, numberOfUploadedProjects)
				.commit();

		if (numberOfUploadedProjects != 2) {
			return;
		}

		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.rating_dialog_title))
				.setView(R.layout.dialog_rate_pocketcode)
				.setPositiveButton(R.string.rating_dialog_rate_now, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW,
									Uri.parse("market://details?id=" + getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						} catch (ActivityNotFoundException e) {
							startActivity(new Intent(Intent.ACTION_VIEW,
									Uri.parse(PLAY_STORE_PAGE_LINK + getPackageName())));
						}
					}
				})

				.setNeutralButton(getString(R.string.rating_dialog_rate_later), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sharedPreferences
								.edit()
								.putInt(NUMBER_OF_UPLOADED_PROJECTS, 0)
								.commit();
					}
				})
				.setNegativeButton(getString(R.string.rating_dialog_rate_never), null)
				.setCancelable(false)
				.show();
	}

	private void verifyUserIdentity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		boolean isTokenSetInPreferences = !token.equals(Constants.NO_TOKEN)
				&& token.length() == ServerCalls.TOKEN_LENGTH
				&& !token.equals(ServerCalls.TOKEN_CODE_INVALID);

		if (isTokenSetInPreferences) {
			new CheckTokenTask(this)
					.execute(token, username);
		} else {
			startSignInWorkflow();
		}
	}

	@Override
	public void onTokenCheckComplete(boolean tokenValid, boolean connectionFailed) {
		if (connectionFailed) {
			if (!tokenValid) {
				ToastUtil.showError(this, R.string.error_session_expired);
				Utils.logoutUser(this);
				startSignInWorkflow();
			} else {
				ToastUtil.showError(this, R.string.error_internet_connection);
				return;
			}
		} else if (!tokenValid) {
			startSignInWorkflow();
			return;
		}

		onCreateView();
	}

	public void startSignInWorkflow() {
		startActivityForResult(new Intent(this, SignInActivity.class), SIGN_IN_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SIGN_IN_CODE) {
			if (resultCode == RESULT_OK) {
				onCreateView();
			} else {
				finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
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

	private class UploadStatusPollingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			while (StatusBarNotificationManager.getInstance().getProgressPercent() != 100) {
				if (isCancelled()) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.e(TAG, "Well, that's awkward.");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			Button button = uploadProgressDialog.getButton(AlertDialog.BUTTON_POSITIVE);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String projectUrl = SHARE_PROGRAM_URL + ServerCalls.getInstance().getProjectId();
					Intent intent = new Intent(ProjectUploadActivity.this, WebViewActivity.class);
					intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, projectUrl);
					startActivity(intent);
					finish();
				}
			});
			button.setEnabled(true);
			uploadProgressDialog.findViewById(R.id.dialog_upload_progress_progressbar).setVisibility(View.GONE);

			ImageView image = uploadProgressDialog.findViewById(R.id.dialog_upload_progress_success_image);
			image.setImageResource(R.drawable.ic_upload_success);
			image.setVisibility(View.VISIBLE);
		}
	}

	private class NameInputTextWatcher implements TextWatcher {

		public String validateName(String name) {
			if (name.isEmpty()) {
				return getString(R.string.name_empty);
			}

			name = name.trim();

			if (name.isEmpty()) {
				return getString(R.string.name_consists_of_spaces_only);
			}

			if (name.equals(getString(R.string.default_project_name))) {
				return getString(R.string.error_upload_project_with_default_name);
			}

			if (!name.equals(project.getName())
					&& FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(name)) {
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
			String error = validateName(s.toString());
			nameInputLayout.setError(error);
			setNextButtonEnabled(error == null);
		}
	}
}
