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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeVolumeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByNBrick;
import at.tugraz.ist.catroid.content.bricks.ClearGraphicEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtMotorActionBrick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtMotorStopBrick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtPlayToneBrick;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick.Direction;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.ScriptBrick;
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
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;

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
		View rootView = inflater.inflate(R.layout.dialog_add_brick, null);

		ImageButton closeButton = (ImageButton) rootView.findViewById(R.id.btn_close_dialog);
		TextView textView = (TextView) rootView.findViewById(R.id.tv_dialog_title);
		listView = (ListView) rootView.findViewById(R.id.addBrickDialogListView);

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
						"dialog_brick_category");
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
		ScriptTabActivity activity = ((ScriptTabActivity) getActivity());
		return (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
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
