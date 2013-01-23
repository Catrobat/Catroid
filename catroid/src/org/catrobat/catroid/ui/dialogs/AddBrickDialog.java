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
package org.catrobat.catroid.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextCostumeBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class AddBrickDialog extends DialogFragment {

	private static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_add_brick";

	private HashMap<String, List<Brick>> brickMap;

	private ListView listView;
	private PrototypeBrickAdapter adapter;
	private String selectedCategory;
	private ScriptFragment scriptFragment;

	//constants Motion
	public static final Float X_Position_Value = (float) 100;
	public static final Float Y_Position_Value = (float) 200;
	public static final Float Change_X_by_Value = (float) 10;
	public static final Float Change_Y_by_Value = (float) 10;
	public static final Float Move_Steps_Value = (float) 10;
	public static final Float Turn_right_Value = (float) 15;
	public static final Float Point_in_Direction_Value = (float) 90;
	public static final Float Glide_Seconds_Value = (float) 1;
	public static final Float Go_Back_Value = (float) 1;
	public static final String Point_Towards_Value = "Nothing...";

	//constants Looks
	public static final Float Set_Size_to_Value = (float) 60;
	public static final Float Change_Size_by_Value = (float) 10;
	public static final Float Set_Ghost_Effect_Value = (float) 50;
	public static final Float Change_Ghost_Effect_by_Value = (float) 25;
	public static final Float Set_Brightness_to_Value = (float) 50;
	public static final Float Change_Brightness_by_Value = (float) 25;

	//constants Sounds
	public static final Float Set_Volumen_to_Value = (float) 60;
	public static final Float Change_Volume_by_Value = (float) -10;
	public static final String Speak_Value = "Hello!";

	public static AddBrickDialog newInstance(String selectedCategory, ScriptFragment scriptFragment) {
		AddBrickDialog dialog = new AddBrickDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory);
		dialog.setArguments(arguments);
		dialog.scriptFragment = scriptFragment;

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);
		getScriptFragment().setCreateNewBrick(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_brick_add, null);

		ImageButton closeButton = (ImageButton) rootView.findViewById(R.id.dialog_brick_title_button_close);
		TextView textView = (TextView) rootView.findViewById(R.id.dialog_brick_title_text_view_title);
		listView = (ListView) rootView.findViewById(R.id.dialog_brick_add_list_view);

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				abort();
				dismiss();
			}
		});

		textView.setText(selectedCategory);

		Window window = getDialog().getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		Context context = getActivity();

		brickMap = setupBrickMap(ProjectManager.getInstance().getCurrentSprite(), context);
		adapter = new PrototypeBrickAdapter(context, brickMap.get(selectedCategory));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				scriptFragment.setCreateNewBrick(true);
				Brick brickToBeAdded = getBrickClone(adapter.getItem(position));
				scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded);

				if (brickToBeAdded instanceof ScriptBrick) {
					Script script = ((ScriptBrick) brickToBeAdded).initScript(ProjectManager.getInstance()
							.getCurrentSprite());
					ProjectManager.getInstance().setCurrentScript(script);
				}

				dismiss();

				BrickCategoryDialog brickCategoryDialog = (BrickCategoryDialog) getFragmentManager().findFragmentByTag(
						BrickCategoryDialog.DIALOG_FRAGMENT_TAG);
				brickCategoryDialog.dismiss();
			}

		});
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

	public Brick getBrickClone(Brick brick) {
		return brick.clone();
	}

	private void abort() {
		getScriptFragment().setCreateNewBrick(false);
	}

	private ScriptFragment getScriptFragment() {
		ScriptActivity scriptActivity = ((ScriptActivity) getActivity());
		return (ScriptFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
	}

	private static boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}

	private static HashMap<String, List<Brick>> setupBrickMap(Sprite sprite, Context context) {
		HashMap<String, List<Brick>> brickMap = new HashMap<String, List<Brick>>();
		List<Brick> motionBrickList = new ArrayList<Brick>();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, 0, 0);
		placeAtBrick.setDefaultValues(context);
		motionBrickList.add(placeAtBrick);

		SetXBrick setXBrick = new SetXBrick(sprite, 0);
		setXBrick.setDefaultValues(context);
		motionBrickList.add(setXBrick);

		SetYBrick setYBrick = new SetYBrick(sprite, 0);
		setYBrick.setDefaultValues(context);
		motionBrickList.add(setYBrick);

		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, 100);
		changeXByNBrick.setDefaultValues(context);
		motionBrickList.add(changeXByNBrick);

		ChangeYByNBrick changeYByNBrick = new ChangeYByNBrick(sprite, 100);
		changeYByNBrick.setDefaultValues(context);
		motionBrickList.add(changeYByNBrick);

		motionBrickList.add(new IfOnEdgeBounceBrick(sprite));

		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);
		moveNStepsBrick.setDefaultValues(context);
		motionBrickList.add(moveNStepsBrick);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, 15);
		turnLeftBrick.setDefaultValues(context);
		motionBrickList.add(turnLeftBrick);

		TurnRightBrick turnRightBrick = new TurnRightBrick(sprite, 15);
		turnRightBrick.setDefaultValues(context);
		motionBrickList.add(turnRightBrick);

		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);
		pointInDirectionBrick.setDefaultValues(context);
		motionBrickList.add(pointInDirectionBrick);

		PointToBrick pointToBrick = new PointToBrick(sprite, null);
		pointToBrick.setDefaultValues(context);
		motionBrickList.add(pointToBrick);

		GlideToBrick glideToBrick = new GlideToBrick(sprite, 800, 0, 1000);
		glideToBrick.setDefaultValues(context);
		motionBrickList.add(glideToBrick);

		if (!isBackground(sprite)) {
			GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(sprite, 1);
			goNStepsBackBrick.setDefaultValues(context);
			motionBrickList.add(goNStepsBackBrick);

			motionBrickList.add(new ComeToFrontBrick(sprite));
		}
		brickMap.put(context.getString(R.string.category_motion), motionBrickList);

		List<Brick> looksBrickList = new ArrayList<Brick>();

		looksBrickList.add(new SetCostumeBrick(sprite));

		looksBrickList.add(new NextCostumeBrick(sprite));

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, Set_Size_to_Value);
		setSizeToBrick.setDefaultValues(context);
		looksBrickList.add(setSizeToBrick);

		ChangeSizeByNBrick changeSizeByNBrick = new ChangeSizeByNBrick(sprite, Change_Size_by_Value);
		changeSizeByNBrick.setDefaultValues(context);
		looksBrickList.add(changeSizeByNBrick);

		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));

		SetGhostEffectBrick setGhostEffectBrick = new SetGhostEffectBrick(sprite, Set_Ghost_Effect_Value);
		setGhostEffectBrick.setDefaultValues(context);
		looksBrickList.add(setGhostEffectBrick);

		ChangeGhostEffectByNBrick changeGhostEffectByNBrick = new ChangeGhostEffectByNBrick(sprite,
				Change_Ghost_Effect_by_Value);
		changeGhostEffectByNBrick.setDefaultValues(context);
		looksBrickList.add(changeGhostEffectByNBrick);

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, Set_Brightness_to_Value);
		setBrightnessBrick.setDefaultValues(context);
		looksBrickList.add(setBrightnessBrick);

		ChangeBrightnessByNBrick changeBrightnessByNBrick = new ChangeBrightnessByNBrick(sprite,
				Change_Brightness_by_Value);
		changeBrightnessByNBrick.setDefaultValues(context);
		looksBrickList.add(changeBrightnessByNBrick);

		looksBrickList.add(new ClearGraphicEffectBrick(sprite));

		brickMap.put(context.getString(R.string.category_looks), looksBrickList);

		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));

		SetVolumeToBrick setVolumeToBrick = new SetVolumeToBrick(sprite, Set_Volumen_to_Value);
		setVolumeToBrick.setDefaultValues(context);
		soundBrickList.add(setVolumeToBrick);

		ChangeVolumeByNBrick changeVolumeByNBrick = new ChangeVolumeByNBrick(sprite, Change_Volume_by_Value);
		changeVolumeByNBrick.setDefaultValues(context);
		soundBrickList.add(changeVolumeByNBrick);

		SpeakBrick speakBrick = new SpeakBrick(sprite, Speak_Value);
		speakBrick.setDefaultValues(context);
		soundBrickList.add(speakBrick);

		brickMap.put(context.getString(R.string.category_sound), soundBrickList);

		List<Brick> controlBrickList = new ArrayList<Brick>();
		WhenStartedBrick whenStartedBrick = new WhenStartedBrick(sprite, null);
		whenStartedBrick.setDefaultValues(context);
		controlBrickList.add(whenStartedBrick);

		WhenBrick whenBrick = new WhenBrick(sprite, null);
		whenBrick.setDefaultValues(context);
		controlBrickList.add(whenBrick);

		WaitBrick waitBrick = new WaitBrick(sprite, 1000);
		waitBrick.setDefaultValues(context);
		controlBrickList.add(waitBrick);

		BroadcastReceiverBrick broadcastReceiverBrick = new BroadcastReceiverBrick(sprite, new BroadcastScript(sprite));
		broadcastReceiverBrick.setDefaultValues(context);
		controlBrickList.add(broadcastReceiverBrick);

		controlBrickList.add(new BroadcastBrick(sprite));
		controlBrickList.add(new BroadcastWaitBrick(sprite));
		controlBrickList.add(new NoteBrick(sprite));
		controlBrickList.add(new ForeverBrick(sprite));

		RepeatBrick repeatBrick = new RepeatBrick(sprite, 3);
		repeatBrick.setDefaultValues(context);
		controlBrickList.add(repeatBrick);

		brickMap.put(context.getString(R.string.category_control), controlBrickList);

		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		LegoNxtMotorTurnAngleBrick legoNxtMotorTurnAngleBrick = new LegoNxtMotorTurnAngleBrick(sprite,
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, 180);
		legoNxtMotorTurnAngleBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorTurnAngleBrick);

		LegoNxtMotorStopBrick legoNxtMotorStopBrick = new LegoNxtMotorStopBrick(sprite,
				LegoNxtMotorStopBrick.Motor.MOTOR_A);
		legoNxtMotorStopBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorStopBrick);

		LegoNxtMotorActionBrick legoNxtMotorActionBrick = new LegoNxtMotorActionBrick(sprite,
				LegoNxtMotorActionBrick.Motor.MOTOR_A, 100);
		legoNxtMotorActionBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorActionBrick);

		LegoNxtPlayToneBrick legoNxtPlayToneBrick = new LegoNxtPlayToneBrick(sprite, 200, 1000);
		legoNxtPlayToneBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtPlayToneBrick);

		brickMap.put(context.getString(R.string.category_lego_nxt), legoNXTBrickList);

		return brickMap;
	}
}
