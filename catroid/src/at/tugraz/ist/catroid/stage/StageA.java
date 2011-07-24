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
package at.tugraz.ist.catroid.stage;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;

import com.badlogic.gdx.backends.android.AndroidApplication;

/**
 * @author Johannes Iber
 * 
 */
public class StageA extends AndroidApplication {
	private boolean stagePlaying = true;
	private StageListener stageListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stageListener = new StageListener();
		initialize(stageListener, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.stage_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.stagemenuStart:
				pauseOrContinue();
				break;
			case R.id.stagemenuConstructionSite:
				manageLoadAndFinish();
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		manageLoadAndFinish();
	}

	@Override
	protected void onDestroy() {
		if (stagePlaying) {
			this.manageLoadAndFinish();
		}
		super.onDestroy();
	}

	private void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();
		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		stagePlaying = false;
		finish();
	}

	private void pauseOrContinue() {
		if (stagePlaying) {
			stageListener.pause();
			stagePlaying = false;
		} else {
			stageListener.resume();
			stagePlaying = true;
		}
	}
}
