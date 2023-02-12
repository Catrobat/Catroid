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
package org.catrobat.catroid.ui.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.DSTFileGenerator;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageLifeCycleController;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.ExportEmbroideryFileLauncher;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME;

public class StageDialog extends Dialog implements View.OnClickListener {
	private static final String TAG = StageDialog.class.getSimpleName();
	private StageActivity stageActivity;
	private StageListener stageListener;

	public StageDialog(StageActivity stageActivity, StageListener stageListener, int theme) {
		super(stageActivity, theme);
		this.stageActivity = stageActivity;
		this.stageListener = stageListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_stage);
		getWindow().getAttributes();

		getWindow().getAttributes();

		int width = LayoutParams.MATCH_PARENT;
		int height = LayoutParams.WRAP_CONTENT;

		getWindow().setLayout(width, height);

		getWindow().setBackgroundDrawableResource(R.color.transparent);

		((Button) findViewById(R.id.stage_dialog_button_back)).setOnClickListener(this);
		((Button) findViewById(R.id.stage_dialog_button_continue)).setOnClickListener(this);
		((Button) findViewById(R.id.stage_dialog_button_restart)).setOnClickListener(this);
		((Button) findViewById(R.id.stage_dialog_button_toggle_axes)).setOnClickListener(this);
		((Button) findViewById(R.id.stage_dialog_button_screenshot)).setOnClickListener(this);
		if (stageActivity.getResizePossible()) {
			((ImageButton) findViewById(R.id.stage_dialog_button_maximize)).setOnClickListener(this);
		} else {
			((ImageButton) findViewById(R.id.stage_dialog_button_maximize)).setVisibility(View.GONE);
		}
	}

	@Override
	public void show() {
		super.show();
		if (stageListener.embroideryPatternManager != null && stageListener.embroideryPatternManager.validPatternExists()) {
			(findViewById(R.id.stage_layout_linear_share)).setVisibility(View.VISIBLE);
			(findViewById(R.id.stage_dialog_button_share)).setOnClickListener(this);
		} else {
			(findViewById(R.id.stage_layout_linear_share)).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.stage_dialog_button_back:
				onBackPressed();
				break;
			case R.id.stage_dialog_button_continue:
				onContinuePressed();
				break;
			case R.id.stage_dialog_button_restart:
				onRestartPressed();
				break;
			case R.id.stage_dialog_button_toggle_axes:
				toggleAxes();
				break;
			case R.id.stage_dialog_button_maximize:
				stageListener.toggleScreenMode();
				break;
			case R.id.stage_dialog_button_screenshot:
				makeScreenshot();
				break;
			case R.id.stage_dialog_button_share:
				shareEmbroideryFile();
				break;
			default:
				Log.w(TAG, "Unimplemented button clicked! This shouldn't happen!");
				break;
		}
	}

	@Override
	public void onBackPressed() {
		clearBroadcastMaps();
		resetEmbroideryThreadColor();
		dismiss();
		stageActivity.exit();
		new FinishThreadAndDisposeTexturesTask().execute(null, null, null);
	}

	private void shareEmbroideryFile() {
		if (stageListener.embroideryPatternManager.validPatternExists()) {
			String filename =
					FileMetaDataExtractor.encodeSpecialCharsForFileSystem(ProjectManager.getInstance().getCurrentProject().getName());
			DSTFileGenerator dstFileGenerator = new DSTFileGenerator(stageListener.embroideryPatternManager.getEmbroideryStream());
			File dstFile = new File(Constants.CACHE_DIRECTORY, filename + Constants.EMBROIDERY_FILE_EXTENSION);
			if (dstFile.exists()) {
				dstFile.delete();
			}

			try {
				if (dstFile.createNewFile()) {
					dstFileGenerator.writeToDSTFile(dstFile);
					new ExportEmbroideryFileLauncher(stageActivity, dstFile).startActivity();
				}
			} catch (IOException e) {
				ToastUtil.showError(stageActivity, R.string.error_embroidery_file_export);
				Log.e(TAG, "Writing to dst file failed");
			}
		}
		dismiss();
	}

	public void onContinuePressed() {
		if (ProjectManager.getInstance().getCurrentProject().isCastProject()
				&& !CastManager.getInstance().isConnected()) {
			ToastUtil.showError(getContext(), stageActivity.getResources().getString(R.string.cast_error_not_connected_msg));
			return;
		}
		dismiss();
		SensorHandler.timerReferenceValue += SystemClock.uptimeMillis() - SensorHandler.timerPauseValue;
		StageLifeCycleController.stageResume(stageActivity);
	}

	public void onRestartPressed() {
		if (ProjectManager.getInstance().getCurrentProject().isCastProject()
				&& !CastManager.getInstance().isConnected()) {
			ToastUtil.showError(getContext(), stageActivity.getResources().getString(R.string.cast_error_not_connected_msg));
			return;
		}

		clearBroadcastMaps();
		resetEmbroideryThreadColor();

		dismiss();
		SensorHandler.timerReferenceValue = SystemClock.uptimeMillis();
		restartProject();
	}

	private void makeScreenshot() {
		if (ProjectManager.getInstance().getCurrentProject().isCastProject()
				&& !CastManager.getInstance().isConnected()) {
			ToastUtil.showError(getContext(), stageActivity.getResources().getString(R.string.cast_error_not_connected_msg));
			return;
		}

		stageListener.requestTakingScreenshot(SCREENSHOT_MANUAL_FILE_NAME,
				success -> {
					if (success) {
						ToastUtil.showSuccess(stageActivity, R.string.notification_screenshot_ok);
						ProjectManager.getInstance().changedProject(ProjectManager.getInstance().getCurrentProject().getName());
					} else {
						ToastUtil.showError(stageActivity, R.string.error_screenshot_failed);
					}
				});
	}

	private void restartProject() {
		stageListener.reloadProject(this);
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Log.e(TAG, "Thread activated too early!", e);
			}
		}
		StageLifeCycleController.stageResume(stageActivity);
	}

	private void toggleAxes() {
		Button axesToggleButton = (Button) findViewById(R.id.stage_dialog_button_toggle_axes);
		if (stageListener.axesOn) {
			stageListener.axesOn = false;
			axesToggleButton.setText(R.string.stage_dialog_axes_on);
		} else {
			stageListener.axesOn = true;
			axesToggleButton.setText(R.string.stage_dialog_axes_off);
		}
	}

	private void clearBroadcastMaps() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.clearIdToEventThreadMap();
			}
		}
	}

	private void resetEmbroideryThreadColor() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				sprite.setEmbroideryThreadColor(Color.BLACK);
			}
		}
	}

	private class FinishThreadAndDisposeTexturesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			stageActivity.manageLoadAndFinish();
			return null;
		}
	}
}
