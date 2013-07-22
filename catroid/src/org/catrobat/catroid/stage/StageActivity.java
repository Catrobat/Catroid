/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.dialogs.StageDialog;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class StageActivity extends AndroidApplication {
	public static final String TAG = StageActivity.class.getSimpleName();

	public static StageListener stageListener;
	private boolean resizePossible;
	private StageDialog stageDialog;

	public static final int STAGE_ACTIVITY_FINISH = 7777;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		calculateScreenSizes();
		initialize(stageListener, true);
	}

	@Override
	public void onBackPressed() {
		pause();
		stageDialog.show();
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		PreStageActivity.shutdownResources();
	}

	public void pause() {
		stageListener.menuPause();
	}

	public void resume() {
		stageListener.menuResume();
		SensorHandler.startSensorListener(this);
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	private void calculateScreenSizes() {
		ifLandscapeSwitchWidthAndHeight();
		int virtualScreenWidth = ProjectManager.INSTANCE.getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.INSTANCE.getCurrentProject().getXmlHeader().virtualScreenHeight;
		if (virtualScreenWidth == ScreenValues.SCREEN_WIDTH && virtualScreenHeight == ScreenValues.SCREEN_HEIGHT) {
			resizePossible = false;
			return;
		}
		resizePossible = true;
		stageListener.maximizeViewPortWidth = ScreenValues.SCREEN_WIDTH + 1;
		do {
			stageListener.maximizeViewPortWidth--;
			stageListener.maximizeViewPortHeight = (int) (((float) stageListener.maximizeViewPortWidth / (float) virtualScreenWidth) * virtualScreenHeight);
		} while (stageListener.maximizeViewPortHeight > ScreenValues.SCREEN_HEIGHT);

		stageListener.maximizeViewPortX = (ScreenValues.SCREEN_WIDTH - stageListener.maximizeViewPortWidth) / 2;
		stageListener.maximizeViewPortY = (ScreenValues.SCREEN_HEIGHT - stageListener.maximizeViewPortHeight) / 2;
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

}
