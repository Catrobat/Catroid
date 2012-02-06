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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeVolumeByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ClearGraphicEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorActionBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorStopBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorTurnAngleBrick;
import at.tugraz.ist.catroid.content.bricks.NXTPlayToneBrick;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick.Direction;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetVolumeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.SpeakBrick;
import at.tugraz.ist.catroid.content.bricks.StopAllSoundsBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.content.bricks.WhenBrick;
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.PrototypeBrickAdapter;

public class AddBrickDialog extends Dialog {

	private HashMap<String, List<Brick>> brickMap;

	private ListView listView;
	private PrototypeBrickAdapter adapter;
	private ScriptTabActivity scriptTabActivity;
	private String category;

	private static boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}

	private static HashMap<String, List<Brick>> setupBrickMap(Sprite sprite, Context context) {
		HashMap<String, List<Brick>> brickMap = new HashMap<String, List<Brick>>();

		List<Brick> motionBrickList = new ArrayList<Brick>();
		motionBrickList.add(new PlaceAtBrick(sprite, 0, 0));
		motionBrickList.add(new SetXBrick(sprite, 0));
		motionBrickList.add(new SetYBrick(sprite, 0));
		motionBrickList.add(new ChangeXByBrick(sprite, 100));
		motionBrickList.add(new ChangeYByBrick(sprite, 100));
		motionBrickList.add(new IfOnEdgeBounceBrick(sprite));
		motionBrickList.add(new MoveNStepsBrick(sprite, 10));
		motionBrickList.add(new TurnLeftBrick(sprite, 15));
		motionBrickList.add(new TurnRightBrick(sprite, 15));
		motionBrickList.add(new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT));
		motionBrickList.add(new PointToBrick(sprite, null));
		motionBrickList.add(new GlideToBrick(sprite, 800, 0, 1000));
		if (!isBackground(sprite)) {
			motionBrickList.add(new GoNStepsBackBrick(sprite, 1));
			motionBrickList.add(new ComeToFrontBrick(sprite));
		}
		brickMap.put(context.getString(R.string.category_motion), motionBrickList);

		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new SetCostumeBrick(sprite));
		looksBrickList.add(new NextCostumeBrick(sprite));
		looksBrickList.add(new SetSizeToBrick(sprite, 100));
		looksBrickList.add(new ChangeSizeByNBrick(sprite, 20));
		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));
		looksBrickList.add(new SetGhostEffectBrick(sprite, 0));
		looksBrickList.add(new ChangeGhostEffectBrick(sprite, 25));
		looksBrickList.add(new SetBrightnessBrick(sprite, 0));
		looksBrickList.add(new ChangeBrightnessBrick(sprite, 25));
		looksBrickList.add(new ClearGraphicEffectBrick(sprite));

		brickMap.put(context.getString(R.string.category_looks), looksBrickList);

		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));
		soundBrickList.add(new SetVolumeToBrick(sprite, 100));
		soundBrickList.add(new ChangeVolumeByBrick(sprite, 25));
		soundBrickList.add(new SpeakBrick(sprite, null));
		brickMap.put(context.getString(R.string.category_sound), soundBrickList);

		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new WhenStartedBrick(sprite, null));
		controlBrickList.add(new WhenBrick(sprite, null));
		controlBrickList.add(new WaitBrick(sprite, 1000));
		controlBrickList.add(new BroadcastReceiverBrick(sprite, null));
		controlBrickList.add(new BroadcastBrick(sprite));
		controlBrickList.add(new BroadcastWaitBrick(sprite));
		controlBrickList.add(new NoteBrick(sprite));
		controlBrickList.add(new ForeverBrick(sprite));
		controlBrickList.add(new RepeatBrick(sprite, 3));
		brickMap.put(context.getString(R.string.category_control), controlBrickList);

		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		legoNXTBrickList.add(new NXTMotorTurnAngleBrick(sprite, NXTMotorTurnAngleBrick.Motor.MOTOR_A, 180));
		legoNXTBrickList.add(new NXTMotorStopBrick(sprite, NXTMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new NXTMotorActionBrick(sprite, NXTMotorActionBrick.Motor.MOTOR_A, 100));
		legoNXTBrickList.add(new NXTPlayToneBrick(sprite, 200, 1000));
		brickMap.put(context.getString(R.string.category_lego_nxt), legoNXTBrickList);

		return brickMap;
	}

	public AddBrickDialog(ScriptTabActivity scriptTabActivity, String category) {
		super(scriptTabActivity, R.style.brick_dialog);
		this.scriptTabActivity = scriptTabActivity;
		this.category = category;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_toolbox);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageButton closeButton = (ImageButton) findViewById(R.id.btn_close_dialog);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

		TextView textView = (TextView) findViewById(R.id.tv_dialog_title);
		textView.setText(category);
	}

	@Override
	protected void onStart() {
		super.onStart();

		listView = (ListView) findViewById(R.id.toolboxListView);
		brickMap = setupBrickMap(ProjectManager.getInstance().getCurrentSprite(), scriptTabActivity);
		adapter = new PrototypeBrickAdapter(this.scriptTabActivity, brickMap.get(category));

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Brick addedBrick = adapter.getItem(position);
				ProjectManager projectManager = ProjectManager.getInstance();

				if (addedBrick instanceof WhenStartedBrick) {
					Script newScript = new StartScript(projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					if (projectManager.getCurrentScriptPosition() < 0) {
						projectManager.setCurrentScript(newScript);
					}
				} else if (addedBrick instanceof WhenBrick) {
					Script newScript = new WhenScript(projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					if (projectManager.getCurrentScriptPosition() < 0) {
						projectManager.setCurrentScript(newScript);
					}
				} else if (addedBrick instanceof WhenBrick) {
					Script newScript = new WhenScript(projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					if (projectManager.getCurrentScriptPosition() < 0) {
						projectManager.setCurrentScript(newScript);
					}
				} else if (addedBrick instanceof BroadcastReceiverBrick) {
					Script newScript = new BroadcastScript(projectManager.getCurrentSprite());
					projectManager.addScript(newScript);
					if (projectManager.getCurrentScriptPosition() < 0) {
						projectManager.setCurrentScript(newScript);
					}
				} else if (addedBrick instanceof LoopBeginBrick
						&& projectManager.getCurrentSprite().getNumberOfScripts() > 0
						&& projectManager.getCurrentScript().containsBrickOfType(LoopEndBrick.class)) {
					//Don't add new loop brick, only one loop per script for now
				} else {
					Brick brickClone = getBrickClone(adapter.getItem(position));
					if (projectManager.getCurrentSprite().getNumberOfScripts() == 0) {
						Script newScript = new StartScript(projectManager.getCurrentSprite());
						projectManager.addScript(newScript);

						Script temp;
						if (projectManager.getCurrentScriptPosition() < 0) {
							temp = newScript;
						} else {
							temp = projectManager.getCurrentScript();
						}
						projectManager.setCurrentScript(newScript);
						projectManager.getCurrentScript().addBrick(brickClone);
						projectManager.setCurrentScript(temp);
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
				scriptTabActivity.dismissDialog(ScriptTabActivity.DIALOG_ADD_BRICK);
				scriptTabActivity.dismissDialog(ScriptTabActivity.DIALOG_BRICK_CATEGORY);
			}
		});
	}

	public Brick getBrickClone(Brick brick) {
		return brick.clone();
	}
}
