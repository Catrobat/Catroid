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

import java.util.ArrayList;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ChangeXByBrick;
import at.tugraz.ist.catroid.content.brick.ChangeYByBrick;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfStartedBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.brick.SetXBrick;
import at.tugraz.ist.catroid.content.brick.SetYBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.adapter.PrototypeBrickAdapter;

public class AddBrickDialog extends Dialog {

	private ArrayList<Brick> prototypeBrickList;
	private ListView listView;
	private PrototypeBrickAdapter adapter;

	private void setupBrickPrototypes(Sprite sprite) {
		prototypeBrickList = new ArrayList<Brick>();
		prototypeBrickList.add(new WaitBrick(sprite, 1000));
		prototypeBrickList.add(new HideBrick(sprite));
		prototypeBrickList.add(new ShowBrick(sprite));
		prototypeBrickList.add(new PlaceAtBrick(sprite, 0, 0));
		prototypeBrickList.add(new SetXBrick(sprite, 0));
		prototypeBrickList.add(new SetYBrick(sprite, 0));
		prototypeBrickList.add(new ChangeXByBrick(sprite, 0));
		prototypeBrickList.add(new ChangeYByBrick(sprite, 0));
		prototypeBrickList.add(new SetCostumeBrick(sprite));
		prototypeBrickList.add(new ScaleCostumeBrick(sprite, 100));
		prototypeBrickList.add(new GoNStepsBackBrick(sprite, 1));
		prototypeBrickList.add(new ComeToFrontBrick(sprite));
		prototypeBrickList.add(new PlaySoundBrick(sprite));
		prototypeBrickList.add(new IfTouchedBrick(sprite, null));
		prototypeBrickList.add(new IfStartedBrick(sprite, null));
	}

	public AddBrickDialog(ScriptActivity scriptActivity, Sprite sprite) {
		super(scriptActivity);
		setupBrickPrototypes(sprite);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_toolbox);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		adapter = new PrototypeBrickAdapter(scriptActivity, prototypeBrickList);

		listView = (ListView) findViewById(R.id.toolboxListView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Brick addedBrick = adapter.getItem(position);
				final ProjectManager projectManager = ProjectManager.getInstance();

				if (addedBrick instanceof IfStartedBrick) {
					Script newScript = new Script("script", projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					projectManager.setCurrentScript(newScript);
					newScript.setTouchScript(false);
				} else if (addedBrick instanceof IfTouchedBrick) {
					Script newScript = new Script("script", projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					projectManager.setCurrentScript(newScript);
					newScript.setTouchScript(true);
				} else {
					if (projectManager.getCurrentSprite().getScriptList().isEmpty()) {
						Script newScript = new Script("script", projectManager.getCurrentSprite());
						projectManager.addScript(newScript);
						projectManager.setCurrentScript(newScript);
						newScript.setTouchScript(false);
						projectManager.addBrick(getBrickClone(adapter.getItem(position)));
					} else {
						projectManager.addBrick(getBrickClone(adapter.getItem(position)));
					}

				}
				dismiss();
			}
		});
	}

	public Brick getBrickClone(Brick brick) {
		return brick.clone();
	}
}
