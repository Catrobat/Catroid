/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.stage.StageListener;
import at.tugraz.ist.catroid.utils.Utils;

/**
 * @author
 * 
 */
public class StageDialog extends Dialog {
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
		setTitle(R.string.stage_dialog_title);

		getWindow().setGravity(Gravity.LEFT);

		Button closeDialogButton = (Button) findViewById(R.id.exit_stage_button);
		closeDialogButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				exitStage();
			}
		});

		Button resumeCurrentProjectButton = (Button) findViewById(R.id.resume_current_project_button);
		resumeCurrentProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				stageActivity.pauseOrContinue();
			}
		});

		Button restartCurrentProjectButton = (Button) findViewById(R.id.restart_current_project_button);
		restartCurrentProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				restartProject();
			}
		});

		Button axesToggleButton = (Button) findViewById(R.id.axes_toggle_button);
		axesToggleButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleAxes();
			}
		});

		Button maximizeButton = (Button) findViewById(R.id.maximize_button);
		if (stageActivity.getResizePossible()) {
			maximizeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					stageListener.changeScreenSize();
				}
			});
		} else {
			maximizeButton.setVisibility(View.GONE);
		}

		Button snapshotButton = (Button) findViewById(R.id.screenshot_button);
		snapshotButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (stageListener.makeScreenshot()) {
					Utils.displayToast(stageActivity, stageActivity.getString(R.string.notification_screenshot_ok));
				} else {
					Utils.displayToast(stageActivity, stageActivity.getString(R.string.error_screenshot_failed));
				}
			}
		});
	}

	private void exitStage() {
		this.dismiss();
		stageActivity.manageLoadAndFinish();
	}

	@Override
	public void onBackPressed() {
		exitStage();
	}

	private void restartProject() {
		dismiss();
		stageListener.reloadProject(stageActivity, this);
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stageActivity.pauseOrContinue();
	}

	private void toggleAxes() {
		Button axesToggleButton = (Button) findViewById(R.id.axes_toggle_button);
		if (stageListener.axesOn) {
			stageListener.axesOn = false;
			axesToggleButton.setText(R.string.stagemenu_axes_on);
		} else {
			stageListener.axesOn = true;
			axesToggleButton.setText(R.string.stagemenu_axes_off);
		}
	}
}
