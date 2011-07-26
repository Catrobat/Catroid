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
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.R.string;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.StopAllSoundsBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.adapter.PrototypeBrickAdapter;

public class AddBrickDialog extends Dialog {

	private HashMap<String, List<Brick>> brickMap;

	private ListView listView;
	private PrototypeBrickAdapter adapter;
	private ScriptActivity scriptActivity;
	private Dialog parentDialog;
	private String category;

	private void setupBrickMap(Sprite sprite) {
		brickMap = new HashMap<String, List<Brick>>();

		List<Brick> motionBrickList = new ArrayList<Brick>();
		motionBrickList.add(new PlaceAtBrick(sprite, 0, 0));
		motionBrickList.add(new SetXBrick(sprite, 0));
		motionBrickList.add(new SetYBrick(sprite, 0));
		motionBrickList.add(new ChangeXByBrick(sprite, 100));
		motionBrickList.add(new ChangeYByBrick(sprite, 100));
		if (!sprite.getName().equals(scriptActivity.getString(string.background))) {
			motionBrickList.add(new GoNStepsBackBrick(sprite, 1));
			motionBrickList.add(new ComeToFrontBrick(sprite));
			motionBrickList.add(new IfOnEdgeBounceBrick(sprite));
			motionBrickList.add(new MoveNStepsBrick(sprite, 10));
			motionBrickList.add(new TurnLeftBrick(sprite, 15));
			motionBrickList.add(new TurnRightBrick(sprite, 15));
			motionBrickList.add(new PointInDirectionBrick(sprite, 90));
		}
		motionBrickList.add(new GlideToBrick(sprite, 800, 0, 1000));
		brickMap.put(getContext().getString(R.string.category_motion), motionBrickList);

		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new SetCostumeBrick(sprite));
		looksBrickList.add(new SetSizeToBrick(sprite, 100));
		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));
		brickMap.put(getContext().getString(R.string.category_looks), looksBrickList);

		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));
		brickMap.put(getContext().getString(R.string.category_sound), soundBrickList);

		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new IfStartedBrick(sprite, null));
		controlBrickList.add(new IfTouchedBrick(sprite, null));
		controlBrickList.add(new WaitBrick(sprite, 1000));
		controlBrickList.add(new BroadcastReceiverBrick(sprite, null));
		controlBrickList.add(new BroadcastBrick(sprite));
		controlBrickList.add(new BroadcastWaitBrick(sprite));
		controlBrickList.add(new NoteBrick(sprite));
		controlBrickList.add(new ForeverBrick(sprite));
		controlBrickList.add(new RepeatBrick(sprite, 3));
		brickMap.put(getContext().getString(R.string.category_control), controlBrickList);
	}

	public AddBrickDialog(Dialog parentDialog, ScriptActivity scriptActivity, String category) {
		super(scriptActivity);
		this.parentDialog = parentDialog;
		this.scriptActivity = scriptActivity;
		this.category = category;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_toolbox);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	@Override
	protected void onStart() {
		super.onStart();

		listView = (ListView) findViewById(R.id.toolboxListView);
		setupBrickMap(ProjectManager.getInstance().getCurrentSprite());
		adapter = new PrototypeBrickAdapter(this.scriptActivity, brickMap.get(category));

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Brick addedBrick = adapter.getItem(position);
				final ProjectManager projectManager = ProjectManager.getInstance();

				if (addedBrick instanceof IfStartedBrick) {
					Script newScript = new StartScript("script", projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					projectManager.setCurrentScript(newScript);
				} else if (addedBrick instanceof IfTouchedBrick) {
					Script newScript = new TapScript("script", projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					projectManager.setCurrentScript(newScript);
				} else if (addedBrick instanceof BroadcastReceiverBrick) {
					Script newScript = new BroadcastScript("script", projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					projectManager.setCurrentScript(newScript);
				} else if (addedBrick instanceof LoopBeginBrick
						&& projectManager.getCurrentSprite().getNumberOfScripts() > 0
						&& projectManager.getCurrentScript().containsLoopBrick()) {
					//Don't add new loop brick, only one loop per script for now
				} else {
					Brick brickClone = getBrickClone(adapter.getItem(position));
					if (projectManager.getCurrentSprite().getNumberOfScripts() == 0) {
						Script newScript = new StartScript("script", projectManager.getCurrentSprite());
						projectManager.addScript(newScript);
						projectManager.setCurrentScript(newScript);
						projectManager.getCurrentScript().addBrick(brickClone);
					} else {
						projectManager.getCurrentScript().addBrick(brickClone);
					}

					if (addedBrick instanceof LoopBeginBrick) {
						LoopEndBrick loopEndBrick = new LoopEndBrick(projectManager.getCurrentSprite(),
								(LoopBeginBrick) brickClone);
						projectManager.getCurrentScript().addBrick(loopEndBrick);
						((LoopBeginBrick) brickClone).setLoopEndBrick(loopEndBrick);
					}
				}
				dismiss();
				parentDialog.dismiss();
			}
		});

	}

	public Brick getBrickClone(Brick brick) {
		return brick.clone();
	}
}
