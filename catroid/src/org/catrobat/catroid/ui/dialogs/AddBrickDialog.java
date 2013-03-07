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
import org.catrobat.catroid.common.BrickValues;
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
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
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
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION);
		placeAtBrick.setDefaultValues(context);
		motionBrickList.add(placeAtBrick);

		SetXBrick setXBrick = new SetXBrick(sprite, BrickValues.X_POSITION);
		setXBrick.setDefaultValues(context);
		motionBrickList.add(setXBrick);

		SetYBrick setYBrick = new SetYBrick(sprite, BrickValues.Y_POSITION);
		setYBrick.setDefaultValues(context);
		motionBrickList.add(setYBrick);

		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, BrickValues.CHANGE_X_BY);
		changeXByNBrick.setDefaultValues(context);
		motionBrickList.add(changeXByNBrick);

		ChangeYByNBrick changeYByNBrick = new ChangeYByNBrick(sprite, BrickValues.CHANGE_Y_BY);
		changeYByNBrick.setDefaultValues(context);
		motionBrickList.add(changeYByNBrick);

		motionBrickList.add(new IfOnEdgeBounceBrick(sprite));

		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, BrickValues.Move_Steps_Value);
		moveNStepsBrick.setDefaultValues(context);
		motionBrickList.add(moveNStepsBrick);

		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(sprite, BrickValues.TURN_REIGTH);
		turnLeftBrick.setDefaultValues(context);
		motionBrickList.add(turnLeftBrick);

		TurnRightBrick turnRightBrick = new TurnRightBrick(sprite, BrickValues.TURN_REIGTH);
		turnRightBrick.setDefaultValues(context);
		motionBrickList.add(turnRightBrick);

		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);
		pointInDirectionBrick.setDefaultValues(context);
		motionBrickList.add(pointInDirectionBrick);

		PointToBrick pointToBrick = new PointToBrick(sprite, null, context.getString(R.string.brick_point_to_prototype));
		pointToBrick.setDefaultValues(context);
		motionBrickList.add(pointToBrick);

		GlideToBrick glideToBrick = new GlideToBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS);
		glideToBrick.setDefaultValues(context);
		motionBrickList.add(glideToBrick);

		if (!isBackground(sprite)) {
			GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(sprite, BrickValues.GO_BACK);
			goNStepsBackBrick.setDefaultValues(context);
			motionBrickList.add(goNStepsBackBrick);
			motionBrickList.add(new ComeToFrontBrick(sprite));
		}

		brickMap.put(context.getString(R.string.category_motion), motionBrickList);

		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new SetLookBrick(sprite));
		looksBrickList.add(new NextLookBrick(sprite));

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, BrickValues.SET_SIZE_TO);
		setSizeToBrick.setDefaultValues(context);
		looksBrickList.add(setSizeToBrick);

		ChangeSizeByNBrick changeSizeByNBrick = new ChangeSizeByNBrick(sprite, BrickValues.CHANGE_SIZE_BY);
		changeSizeByNBrick.setDefaultValues(context);
		looksBrickList.add(changeSizeByNBrick);

		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));

		SetGhostEffectBrick setGhostEffectBrick = new SetGhostEffectBrick(sprite, BrickValues.SET_GHOST_EFFECT);
		setGhostEffectBrick.setDefaultValues(context);
		looksBrickList.add(setGhostEffectBrick);

		ChangeGhostEffectByNBrick changeGhostEffectByNBrick = new ChangeGhostEffectByNBrick(sprite,
				BrickValues.CHANGE_GHOST_EFFECT);
		changeGhostEffectByNBrick.setDefaultValues(context);
		looksBrickList.add(changeGhostEffectByNBrick);

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, BrickValues.SET_BRIGHTNESS_TO);
		setBrightnessBrick.setDefaultValues(context);
		looksBrickList.add(setBrightnessBrick);

		ChangeBrightnessByNBrick changeBrightnessByNBrick = new ChangeBrightnessByNBrick(sprite,
				BrickValues.CHANGE_BRITHNESS_BY);
		changeBrightnessByNBrick.setDefaultValues(context);
		looksBrickList.add(changeBrightnessByNBrick);

		looksBrickList.add(new ClearGraphicEffectBrick(sprite));

		brickMap.put(context.getString(R.string.category_looks), looksBrickList);

		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));

		SetVolumeToBrick setVolumeToBrick = new SetVolumeToBrick(sprite, BrickValues.SET_VOLUMEN_TO);
		soundBrickList.add(new SpeakBrick(sprite, ""));
		setVolumeToBrick.setDefaultValues(context);
		soundBrickList.add(setVolumeToBrick);

		ChangeVolumeByNBrick changeVolumeByNBrick = new ChangeVolumeByNBrick(sprite, BrickValues.CHANGE_VOLUMEN_BY);
		changeVolumeByNBrick.setDefaultValues(context);
		soundBrickList.add(changeVolumeByNBrick);

		SpeakBrick speakBrick = new SpeakBrick(sprite, context.getString(R.string.brick_speak_prototype));
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

		WaitBrick waitBrick = new WaitBrick(sprite, BrickValues.WAIT);
		waitBrick.setDefaultValues(context);
		controlBrickList.add(waitBrick);

		controlBrickList.add(new BroadcastReceiverBrick(sprite, new BroadcastScript(sprite)));

		controlBrickList.add(new BroadcastBrick(sprite));
		controlBrickList.add(new BroadcastWaitBrick(sprite));

		NoteBrick noteBrick = new NoteBrick(sprite, context.getString(R.string.brick_note_prototype));
		noteBrick.setDefaultValues(context);
		controlBrickList.add(noteBrick);

		controlBrickList.add(new ForeverBrick(sprite));

		RepeatBrick repeatBrick = new RepeatBrick(sprite, BrickValues.REPEAT);
		repeatBrick.setDefaultValues(context);
		controlBrickList.add(repeatBrick);

		brickMap.put(context.getString(R.string.category_control), controlBrickList);

		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		LegoNxtMotorTurnAngleBrick legoNxtMotorTurnAngleBrick = new LegoNxtMotorTurnAngleBrick(sprite,
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, BrickValues.ANGLE);
		legoNxtMotorTurnAngleBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorTurnAngleBrick);

		LegoNxtMotorStopBrick legoNxtMotorStopBrick = new LegoNxtMotorStopBrick(sprite,
				LegoNxtMotorStopBrick.Motor.MOTOR_A);
		legoNxtMotorStopBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorStopBrick);

		LegoNxtMotorActionBrick legoNxtMotorActionBrick = new LegoNxtMotorActionBrick(sprite,
				LegoNxtMotorActionBrick.Motor.MOTOR_A, BrickValues.SPEED);
		legoNxtMotorActionBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtMotorActionBrick);

		LegoNxtPlayToneBrick legoNxtPlayToneBrick = new LegoNxtPlayToneBrick(sprite, BrickValues.FREQUENCY,
				BrickValues.SECONDS);
		legoNxtPlayToneBrick.setDefaultValues(context);
		legoNXTBrickList.add(legoNxtPlayToneBrick);

		brickMap.put(context.getString(R.string.category_lego_nxt), legoNXTBrickList);

		return brickMap;
	}
}
