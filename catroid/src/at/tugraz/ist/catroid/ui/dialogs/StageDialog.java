/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.stage.StageManager;

/**
 * @author
 * 
 */
public class StageDialog extends Dialog {
	private StageActivity stageActivity;
	private StageManager stageManager;

	public static final String backToConstruction = "BACK_TO_CONSTRUCTION";

	public StageDialog(StageActivity stageActivity, StageManager stageManager, int theme) {
		super(stageActivity, theme);
		this.stageActivity = stageActivity;
		this.stageManager = stageManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_stage);
		setTitle(R.string.stage_dialog_title);

		getWindow().setGravity(Gravity.LEFT);

		Button backToConstructionSiteButton = (Button) findViewById(R.id.back_to_construction_site_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				backToConstruction();
			}
		});

		Button resumeCurrentProjectButton = (Button) findViewById(R.id.resume_current_project_button);
		resumeCurrentProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stageActivity.pauseOrContinue();
			}
		});

		Button restartCurrentProjectButton = (Button) findViewById(R.id.restart_current_project_button);
		restartCurrentProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				restartProject();
			}
		});

		Button snapshotButton = (Button) findViewById(R.id.snapshot_button);
		snapshotButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Vibrator vibr = (Vibrator) stageActivity.getSystemService(Context.VIBRATOR_SERVICE);
				vibr.vibrate(100);
				String text = null;
				boolean success = stageManager.saveScreenshot();

				if (success) {
					text = stageActivity.getString(R.string.notification_screenshot_ok);
				} else {
					text = stageActivity.getString(R.string.error_screenshot_failed);
				}

				Toast toast = Toast.makeText(stageActivity, text, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	private void backToConstruction() {
		this.dismiss();
		stageActivity.manageLoadAndFinish();
	}

	@Override
	public void onBackPressed() {
		stageActivity.pauseOrContinue();
	}

	private void restartProject() {
		this.dismiss();
		stageActivity.restart();
	}
}
