/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PreviousLookBrick;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick;

import java.util.ArrayList;
import java.util.List;

import static org.koin.java.KoinJavaComponent.inject;

public class CategoryBeginnerBricksFactory extends CategoryBricksFactory {

	@Override
	protected List<Brick> setupEventCategoryList(Context context, boolean isBackgroundSprite) {
		List<Brick> eventBrickList = new ArrayList<>();
		eventBrickList.add(new WhenStartedBrick());
		eventBrickList.add(new WhenTouchDownBrick());
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		Project currentProject = projectManager.getCurrentProject();
		List<String> broadcastMessages = currentProject.getBroadcastMessageContainer().getBroadcastMessages();
		String broadcastMessage = context.getString(R.string.brick_broadcast_default_value);
		if (broadcastMessages.size() > 0) {
			broadcastMessage = broadcastMessages.get(0);
		}
		eventBrickList.add(new BroadcastReceiverBrick(new BroadcastScript(broadcastMessage)));
		eventBrickList.add(new BroadcastBrick(broadcastMessage));
		return eventBrickList;
	}

	@Override
	protected List<Brick> setupControlCategoryList(Context context) {
		List<Brick> controlBrickList = new ArrayList<>();
		controlBrickList.add(new WaitBrick(BrickValues.WAIT));
		controlBrickList.add(new ForeverBrick());
		controlBrickList.add(new CloneBrick());
		controlBrickList.add(new DeleteThisCloneBrick());
		controlBrickList.add(new WhenClonedBrick());
		return controlBrickList;
	}

	@Override
	protected List<Brick> setupMotionCategoryList(Context context, boolean isBackgroundSprite) {
		List<Brick> motionBrickList = new ArrayList<>();
		motionBrickList.add(new PlaceAtBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION));
		motionBrickList.add(new GoToBrick(null));
		if (!isBackgroundSprite) {
			motionBrickList.add(new IfOnEdgeBounceBrick());
		}
		motionBrickList.add(new MoveNStepsBrick(BrickValues.MOVE_STEPS));
		motionBrickList.add(new TurnLeftBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new TurnRightBrick(BrickValues.TURN_DEGREES));
		motionBrickList.add(new GlideToBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS));
		return motionBrickList;
	}

	@Override
	protected List<Brick> setupSoundCategoryList(Context context) {
		List<Brick> soundBrickList = new ArrayList<>();
		soundBrickList.add(new PlaySoundBrick());
		soundBrickList.add(new StopAllSoundsBrick());
		soundBrickList.add(new SpeakBrick(context.getString(R.string.brick_speak_default_value)));
		return soundBrickList;
	}

	@Override
	protected List<Brick> setupLooksCategoryList(Context context, boolean isBackgroundSprite) {
		List<Brick> looksBrickList = new ArrayList<>();

		if (!isBackgroundSprite) {
			looksBrickList.add(new SetLookBrick());
		}
		looksBrickList.add(new NextLookBrick());
		looksBrickList.add(new PreviousLookBrick());
		looksBrickList.add(new SetSizeToBrick(BrickValues.SET_SIZE_TO));
		looksBrickList.add(new ChangeSizeByNBrick(BrickValues.CHANGE_SIZE_BY));
		looksBrickList.add(new HideBrick());
		looksBrickList.add(new ShowBrick());
		looksBrickList.add(new AskBrick(context.getString(R.string.brick_ask_default_question)));
		if (!isBackgroundSprite) {
			looksBrickList.add(new SayBubbleBrick(context.getString(R.string.brick_say_bubble_default_value)));
			looksBrickList.add(new SayForBubbleBrick(context.getString(R.string.brick_say_bubble_default_value), 1.0f));
			looksBrickList.add(new ThinkBubbleBrick(context.getString(R.string.brick_think_bubble_default_value)));
			looksBrickList.add(new ThinkForBubbleBrick(context.getString(R.string.brick_think_bubble_default_value), 1.0f));
		}
		looksBrickList.add(new SetColorBrick(BrickValues.SET_COLOR_TO));
		looksBrickList.add(new ChangeColorByNBrick(BrickValues.CHANGE_COLOR_BY));
		looksBrickList.add(new SetBackgroundBrick());
		return looksBrickList;
	}

	@Override
	protected List<Brick> setupDataCategoryList(Context context, boolean isBackgroundSprite) {
		List<Brick> dataBrickList = new ArrayList<>();
		dataBrickList.add(new SetVariableBrick(BrickValues.SET_VARIABLE));
		dataBrickList.add(new ChangeVariableBrick(BrickValues.CHANGE_VARIABLE));
		dataBrickList.add(new ShowTextBrick(BrickValues.X_POSITION, BrickValues.Y_POSITION));
		dataBrickList.add(new HideTextBrick());
		return dataBrickList;
	}
}
