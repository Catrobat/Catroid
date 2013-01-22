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
		motionBrickList.add(new PlaceAtBrick(sprite, 0, 0));
		motionBrickList.add(new SetXBrick(sprite, 0));
		motionBrickList.add(new SetYBrick(sprite, 0));
		motionBrickList.add(new ChangeXByNBrick(sprite, 100));
		motionBrickList.add(new ChangeYByNBrick(sprite, 100));
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
		looksBrickList.add(new ChangeGhostEffectByNBrick(sprite, 25));
		looksBrickList.add(new SetBrightnessBrick(sprite, 0));
		looksBrickList.add(new ChangeBrightnessByNBrick(sprite, 25));
		looksBrickList.add(new ClearGraphicEffectBrick(sprite));

		brickMap.put(context.getString(R.string.category_looks), looksBrickList);

		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new PlaySoundBrick(sprite));
		soundBrickList.add(new StopAllSoundsBrick(sprite));
		soundBrickList.add(new SetVolumeToBrick(sprite, 100));
		soundBrickList.add(new ChangeVolumeByNBrick(sprite, 25));
		soundBrickList.add(new SpeakBrick(sprite, null));
		brickMap.put(context.getString(R.string.category_sound), soundBrickList);

		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new WhenStartedBrick(sprite, null));
		controlBrickList.add(new WhenBrick(sprite, null));
		controlBrickList.add(new WaitBrick(sprite, 1000));
		controlBrickList.add(new BroadcastReceiverBrick(sprite, new BroadcastScript(sprite)));
		controlBrickList.add(new BroadcastBrick(sprite));
		controlBrickList.add(new BroadcastWaitBrick(sprite));
		controlBrickList.add(new NoteBrick(sprite));
		controlBrickList.add(new ForeverBrick(sprite));
		controlBrickList.add(new RepeatBrick(sprite, 3));
		brickMap.put(context.getString(R.string.category_control), controlBrickList);

		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		legoNXTBrickList.add(new LegoNxtMotorTurnAngleBrick(sprite, LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, 180));
		legoNXTBrickList.add(new LegoNxtMotorStopBrick(sprite, LegoNxtMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A, 100));
		legoNXTBrickList.add(new LegoNxtPlayToneBrick(sprite, 200, 1000));
		brickMap.put(context.getString(R.string.category_lego_nxt), legoNXTBrickList);

		return brickMap;
	}
}
