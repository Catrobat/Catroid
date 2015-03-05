/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AllowedAfterDeadEndBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.DeadEndBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.ui.BrickView;
import org.catrobat.catroid.ui.bricks.BrickViewFactory;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;

import static org.catrobat.catroid.utils.UiUtils.startBlinkAnimation;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener,
		ScriptActivityAdapterInterface {

	private static final String TAG = BrickAdapter.class.getSimpleName();
	public static final int ALPHA_FULL = 255;
	private static final int ALPHA_GREYED = 100;
	private Context context;
	private List<Class<? extends Brick>> viewTypes;

	public Context getContext() {
		return context;
	}

	private Sprite sprite;
	private UserBrick userBrick;
	private Script script;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private DragAndDropListView dragAndDropListView;
	private View insertionView;
	private boolean initInsertedBrick;
	private boolean addingNewBrick;
	private int positionOfInsertedBrick;

	private boolean firstDrag;
	private int fromBeginDrag;
	private int toEndDrag;
	private boolean retryScriptDragging;
	private boolean showDetails = false;

	private List<Brick> brickList;
	private Queue<Integer> toAnimatePositions;
	private BrickViewFactory brickViewFactory;

	private int selectMode;
	private OnBrickCheckedListener scriptFragment;
	private boolean actionMode = false;

	private Lock viewSwitchLock = new ViewSwitchLock();

	public int listItemCount = 0;

	private int clickItemPosition = 0;

	private AlertDialog alertDialog = null;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		brickViewFactory = new BrickViewFactory(context);
		dragAndDropListView = listView;
		toAnimatePositions = new LinkedBlockingQueue<Integer>();
		dragAndDropListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//Clear to animate brick positions before scroll.
				Log.v(TAG, "onScrollStateChanged clear positions: " + toAnimatePositions.size() + "; " + toAnimatePositions);
				toAnimatePositions.clear();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		addingNewBrick = false;
		firstDrag = true;
		retryScriptDragging = false;

		this.selectMode = ListView.CHOICE_MODE_NONE;
		initBrickList();
	}

	public void initBrickList() {
		brickList = new ArrayList<Brick>();

		if (userBrick != null) {
			initBrickListUserScript();
			return;
		}
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		int numberOfScripts = sprite.getNumberOfScripts();
		viewTypes = new ArrayList<Class<? extends Brick>>();
		for (int scriptPosition = 0; scriptPosition < numberOfScripts; scriptPosition++) {
			Script script = sprite.getScript(scriptPosition);
			ScriptBrick scriptBrick = script.getScriptBrick();
			if (!viewTypes.contains(scriptBrick.getClass())) {
				viewTypes.add(scriptBrick.getClass());
			}
			brickList.add(scriptBrick);
			for (Brick brick : script.getBrickList()) {
				brickList.add(brick);
				if (!viewTypes.contains(brick.getClass())) {
					viewTypes.add(brick.getClass());
				}
			}
		}
	}

	@Override
	public int getViewTypeCount() {
		/*Can't have a viewTypeCount < 1*/
		//TODO: IllyaBoyko display empty view in this case
		return viewTypes == null || viewTypes.isEmpty() ? 1 : viewTypes.size();
	}

	@Override
	public int getItemViewType(int position) {
		Brick brick = brickList.get(position);
		Class<? extends Brick> brickClass = brick.getClass();
		int index = viewTypes.indexOf(brickClass);
		return index;
	}

	private void initBrickListUserScript() {
		script = getUserScript();
		brickList = new ArrayList<Brick>();
		brickList.add(script.getScriptBrick());

		for (Brick brick : script.getBrickList()) {
			if (brick.getClass().equals(ChangeVariableBrick.class)) {
				ChangeVariableBrick changeVariableBrick = (ChangeVariableBrick) brick;
				changeVariableBrick.setInUserBrick(true);
			} else if (brick.getClass().equals(SetVariableBrick.class)) {
				SetVariableBrick setVariableBrick = (SetVariableBrick) brick;
				setVariableBrick.setInUserBrick(true);
			}
			brickList.add(brick);
		}
	}

	private Script getUserScript() {
		UserScriptDefinitionBrick defBrick = userBrick.getDefinitionBrick();
		return defBrick.getScriptSafe();
	}

	public boolean isActionMode() {
		return actionMode;
	}

	public void setActionMode(boolean actionMode) {
		this.actionMode = actionMode;
	}

	public List<Brick> getBrickList() {
		return brickList;
	}

	public void setBrickList(List<Brick> brickList) {
		this.brickList = brickList;
	}

	@Override
	public void drag(int from, int to) {

		int toOriginal = to;
		if (to < 0 || to >= brickList.size()) {
			to = brickList.size() - 1;
		}
		if (from < 0 || from >= brickList.size()) {
			from = brickList.size() - 1;
		}
		if (draggedBrick == null) {
			draggedBrick = (Brick) getItem(from);
			notifyDataSetChanged();
		}

		if (firstDrag) {
			fromBeginDrag = from;
			firstDrag = false;
		}

		if (draggedBrick instanceof NestingBrick) {
			NestingBrick nestingBrick = (NestingBrick) draggedBrick;
			if (nestingBrick.isInitialized()) {
				to = getDraggedNestingBricksToPosition(nestingBrick, to);
			}
		} else if (draggedBrick instanceof ScriptBrick) {
			int currentPosition = to;
			brickList.remove(draggedBrick);
			brickList.add(to, draggedBrick);
			to = getNewPositionForScriptBrick(to, draggedBrick);
			dragTargetPosition = to;
			if (currentPosition != to) {
				retryScriptDragging = true;
			} else {
				retryScriptDragging = false;
			}
		}
		to = getNewPositionIfEndingBrickIsThere(to, draggedBrick);

		if (!(draggedBrick instanceof ScriptBrick)) {
			if (to != 0) {
				dragTargetPosition = to;
			} else {
				dragTargetPosition = 1;
				to = 1;
			}
		}

		brickList.remove(draggedBrick);
		if (dragTargetPosition >= 0 && dragTargetPosition <= brickList.size()) {
			brickList.add(dragTargetPosition, draggedBrick);
			toEndDrag = to;
		} else {
			brickList.add(toOriginal, draggedBrick);
			toEndDrag = toOriginal;
		}

		toAnimatePositions.clear();

		notifyDataSetChanged();
	}

	private int getNewPositionIfEndingBrickIsThere(int to, Brick brick) {
		int currentPosition = brickList.indexOf(brick);

		if (getItem(to) instanceof AllowedAfterDeadEndBrick && !(getItem(to) instanceof DeadEndBrick)
				&& getItem(to - 1) instanceof DeadEndBrick) {
			if (currentPosition > to) {
				return to + 1;
			} else {
				return to;
			}
		} else if (getItem(to) instanceof DeadEndBrick) {
			for (int i = to - 1; i >= 0; i--) {
				if (!(getItem(i) instanceof DeadEndBrick)) {
					if (currentPosition > i) {
						return i + 1;
					} else {
						return i;
					}
				}
			}
		}

		return to;
	}

	private int getDraggedNestingBricksToPosition(NestingBrick nestingBrick, int to) {
		List<NestingBrick> nestingBrickList = nestingBrick.getAllNestingBrickParts(true);
		int restrictedTop = 0;
		int restrictedBottom = brickList.size();

		int tempPosition;
		int currentPosition = to;
		boolean passedBrick = false;
		for (NestingBrick temp : nestingBrickList) {
			tempPosition = brickList.indexOf(temp);
			if (temp != nestingBrick) {
				if (!passedBrick) {
					restrictedTop = tempPosition;
				}
				if (passedBrick) {
					restrictedBottom = tempPosition;
					break;
				}
			} else {
				passedBrick = true;
				currentPosition = tempPosition;
			}
		}

		for (int i = currentPosition; i > restrictedTop; i--) {
			if (checkIfScriptOrOtherNestingBrick(brickList.get(i), nestingBrickList)) {
				restrictedTop = i;
				break;
			}
		}

		for (int i = currentPosition; i < restrictedBottom; i++) {
			if (checkIfScriptOrOtherNestingBrick(brickList.get(i), nestingBrickList)) {
				restrictedBottom = i;
				break;
			}
		}

		to = to <= restrictedTop ? restrictedTop + 1 : to;
		to = to >= restrictedBottom ? restrictedBottom - 1 : to;

		return to;
	}

	private boolean checkIfScriptOrOtherNestingBrick(Brick brick, List<NestingBrick> nestingBrickList) {
		if (brick instanceof ScriptBrick) {
			return true;
		}
		if (brick instanceof NestingBrick && !nestingBrickList.contains(brick)) {
			return true;
		}

		return false;
	}

	@Override
	public void drop() {
		int to = toEndDrag;

		if (to < 0 || to >= brickList.size()) {
			to = brickList.size() - 1;
		}

		if (retryScriptDragging || to != getNewPositionForScriptBrick(to, draggedBrick)) {
			scrollToPosition(dragTargetPosition);
			draggedBrick = null;
			initInsertedBrick = true;
			positionOfInsertedBrick = dragTargetPosition;
			notifyDataSetChanged();

			retryScriptDragging = false;
			return;
		}
		int tempTo = getNewPositionIfEndingBrickIsThere(to, draggedBrick);
		if (to != tempTo) {
			to = tempTo;
		}

		if (addingNewBrick) {
			if (draggedBrick instanceof ScriptBrick) {
				addScriptToProject(to, (ScriptBrick) draggedBrick);
			} else {
				if (script != null) {
					addBrickToPositionInUserScript(to, draggedBrick);
				} else {
					addBrickToPositionInProject(to, draggedBrick);
				}
			}

			if (draggedBrick instanceof UserBrick) {
				int positionOfUserbrickInScript = 0;
				for (int index = 0; index <= to; index++) {
					if (brickList.get(index) instanceof UserBrick && ((UserBrick) brickList.get(index)).getUserBrickId() == ((UserBrick) draggedBrick).getUserBrickId()) {
						positionOfUserbrickInScript++;
					}
				}
				for (int parameterIndex = 0; parameterIndex < ProjectManager.getInstance().getCurrentProject().getUserVariables().getOrCreateVariableListForUserBrick(((UserBrick) draggedBrick).getUserBrickId()).size(); parameterIndex++) {
					((UserBrick) draggedBrick).addUserBrickPositionToParameter(Pair.create(positionOfUserbrickInScript, parameterIndex));
				}

				ArrayList<Pair<Integer, Integer>> userBrickPositionToParameterList = ((UserBrick) draggedBrick).getUserBrickPositionToParameter();

				int numberOfUserBricksInScript = userBrickPositionToParameterList.size();
				int frequencyOfEqualFirstParameters = 0;
				for (int newIndex = 0; newIndex < userBrickPositionToParameterList.size(); newIndex++) {
					if (userBrickPositionToParameterList.get(newIndex).first == userBrickPositionToParameterList.get(numberOfUserBricksInScript - 1).first) {
						frequencyOfEqualFirstParameters++;
					}
				}
				if (frequencyOfEqualFirstParameters != ProjectManager.getInstance().getCurrentProject().getUserVariables().getOrCreateVariableListForUserBrick(((UserBrick) draggedBrick).getUserBrickId()).size()) {
					for (int userBrickPosition = positionOfUserbrickInScript; userBrickPosition < numberOfUserBricksInScript; userBrickPosition++) {
						Pair<Integer, Integer> userBrickPositionToParameter = ((UserBrick) draggedBrick).getUserBrickPositionToParameter().get(userBrickPosition);
						if (userBrickPositionToParameter.first >= userBrickPosition) {
							((UserBrick) draggedBrick).setUserBrickPositionToParameter(Pair.create(userBrickPositionToParameter.first + 1, userBrickPositionToParameter.second), ((UserBrick) draggedBrick).getUserBrickIndexInScript(userBrickPositionToParameter));
						}
					}
				}
				//TODO: test if everything gets updated if userbrick gets moved (should work) and delete arraylist entry when userbrick gets deleted, when new variable gets created update
			}

			addingNewBrick = false;
		} else {
			if (script != null) {
				moveUserBrick(fromBeginDrag, toEndDrag);
			} else {
				moveExistingProjectBrick(fromBeginDrag, toEndDrag);
			}
		}

		draggedBrick = null;
		firstDrag = true;

		initBrickList();
		notifyDataSetChanged();

		int scrollTo = to;
		if (scrollTo >= brickList.size() - 1) {
			scrollTo = getCount() - 1;
		}
		dragAndDropListView.smoothScrollToPosition(scrollTo);
	}

	private void addScriptToProject(int position, ScriptBrick scriptBrick) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] temp = getScriptAndBrickIndexFromProject(position);

		int scriptPosition = temp[0];
		int brickPosition = temp[1];

		Script newScript = scriptBrick.getScriptSafe();
		if (currentSprite.getNumberOfBricks() > 0) {
			int addScriptTo = position == 0 ? 0 : scriptPosition + 1;
			currentSprite.addScript(addScriptTo, newScript);
		} else {
			currentSprite.addScript(newScript);
		}

		Script previousScript = currentSprite.getScript(scriptPosition);
		if (previousScript != null) {
			Brick brick;
			int size = previousScript.getBrickList().size();
			for (int i = brickPosition; i < size; i++) {
				brick = previousScript.getBrick(brickPosition);
				previousScript.removeBrick(brick);
				newScript.addBrick(brick);
			}
		}
		ProjectManager.getInstance().setCurrentScript(newScript);
	}

	private void moveUserBrick(int from, int to) {
		Brick brick = script.getBrick(getPositionInUserScript(from));
		script.removeBrick(brick);
		script.addBrick(getPositionInUserScript(to), brick);
	}

	private void moveExistingProjectBrick(int from, int to) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] tempFrom = getScriptAndBrickIndexFromProject(from);
		int scriptPositionFrom = tempFrom[0];
		int brickPositionFrom = tempFrom[1];

		Script fromScript = currentSprite.getScript(scriptPositionFrom);

		Brick brick = fromScript.getBrick(brickPositionFrom);
		if (draggedBrick != brick) {
			Log.e(TAG, "Want to save wrong brick");
			return;
		}
		fromScript.removeBrick(brick);

		int[] tempTo = getScriptAndBrickIndexFromProject(to);
		int scriptPositionTo = tempTo[0];
		int brickPositionTo = tempTo[1];

		Script toScript = currentSprite.getScript(scriptPositionTo);

		toScript.addBrick(brickPositionTo, brick);
	}

	private void addBrickToPositionInProject(int position, Brick brick) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] temp = getScriptAndBrickIndexFromProject(position);

		int scriptPosition = temp[0];
		int brickPosition = temp[1];

		Script script = currentSprite.getScript(scriptPosition);

		if (brick instanceof NestingBrick) {
			((NestingBrick) draggedBrick).initialize();
			List<NestingBrick> nestingBrickList = ((NestingBrick) draggedBrick).getAllNestingBrickParts(true);
			for (int i = 0; i < nestingBrickList.size(); i++) {
				if (nestingBrickList.get(i) instanceof DeadEndBrick) {
					if (i < nestingBrickList.size() - 1) {
						Log.w(TAG, "Adding a DeadEndBrick in the middle of the NestingBricks");
					}
					position = getPositionForDeadEndBrick(position);
					temp = getScriptAndBrickIndexFromProject(position);
					script.addBrick(temp[1], (Brick) nestingBrickList.get(i));
				} else {
					script.addBrick(brickPosition + i, (Brick) nestingBrickList.get(i));
				}
			}
		} else {
			script.addBrick(brickPosition, brick);
		}
	}

	private void addBrickToPositionInUserScript(int position, Brick brick) {
		position = getPositionInUserScript(position);
		if (brick instanceof NestingBrick) {
			((NestingBrick) draggedBrick).initialize();
			List<NestingBrick> nestingBrickList = ((NestingBrick) draggedBrick).getAllNestingBrickParts(true);
			for (int i = 0; i < nestingBrickList.size(); i++) {
				script.addBrick(position + i, (Brick) nestingBrickList.get(i));
			}
		} else {
			script.addBrick(position, brick);
		}
	}

	private int getPositionForDeadEndBrick(int position) {
		for (int i = position + 1; i < brickList.size(); i++) {
			if (brickList.get(i) instanceof AllowedAfterDeadEndBrick || brickList.get(i) instanceof DeadEndBrick) {
				return i;
			}

			if (brickList.get(i) instanceof NestingBrick) {
				List<NestingBrick> tempList = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts(true);
				int currentPosition = i;
				i = brickList.indexOf(tempList.get(tempList.size() - 1)) + 1;
				if (i < 0) {
					i = currentPosition;
				} else if (i >= brickList.size()) {
					return brickList.size();
				}
			}

			if (brickList.get(i) instanceof AllowedAfterDeadEndBrick || brickList.get(i) instanceof DeadEndBrick) {
				return i;
			}
		}

		return brickList.size();
	}

	private int getPositionInUserScript(int position) {
		position--;
		if (position < 0) {
			position = 0;
		}
		if (position >= brickList.size()) {
			return brickList.size() - 1;
		}

		List<Brick> brickListFromScript = script.getBrickList();

		Brick scriptBrick;
		if (brickListFromScript.size() != 0 && position < brickListFromScript.size()) {
			scriptBrick = brickListFromScript.get(position);
		} else {
			scriptBrick = null;
		}

		int returnValue = script.getBrickList().indexOf(scriptBrick);
		if (returnValue < 0) {
			returnValue = script.getBrickList().size();
		}
		return returnValue;
	}

	public int[] getScriptAndBrickIndexFromProject(int position) {
		int[] returnValue = new int[2];

		if (position >= brickList.size()) {

			returnValue[0] = sprite.getNumberOfScripts() - 1;
			if (returnValue[0] < 0) {
				returnValue[0] = 0;
				returnValue[1] = 0;
			} else {
				Script script = sprite.getScript(returnValue[0]);
				if (script != null) {
					returnValue[1] = script.getBrickList().size();
				} else {
					returnValue[1] = 0;
				}
			}

			return returnValue;
		}

		int scriptPosition = 0;
		int scriptOffset;
		for (scriptOffset = 0; scriptOffset < position; ) {
			scriptOffset += sprite.getScript(scriptPosition).getBrickList().size() + 1;
			if (scriptOffset < position) {
				scriptPosition++;
			}
		}
		scriptOffset -= sprite.getScript(scriptPosition).getBrickList().size();

		returnValue[0] = scriptPosition;
		List<Brick> brickListFromProject = sprite.getScript(scriptPosition).getBrickList();
		int brickPosition = position;
		if (scriptOffset > 0) {
			brickPosition -= scriptOffset;
		}

		Brick brickFromProject;
		if (brickListFromProject.size() != 0 && brickPosition < brickListFromProject.size()) {
			brickFromProject = brickListFromProject.get(brickPosition);
		} else {
			brickFromProject = null;
		}

		returnValue[1] = sprite.getScript(scriptPosition).getBrickList().indexOf(brickFromProject);
		if (returnValue[1] < 0) {
			returnValue[1] = sprite.getScript(scriptPosition).getBrickList().size();
		}

		return returnValue;
	}

	private void scrollToPosition(final int position) {
		DragAndDropListView list = dragAndDropListView;
		if (list.getFirstVisiblePosition() < position && position < list.getLastVisiblePosition()) {
			return;
		}

		list.setIsScrolling();
		if (position <= list.getFirstVisiblePosition()) {
			list.smoothScrollToPosition(0, position + 2);
		} else {
			list.smoothScrollToPosition(brickList.size() - 1, position - 2);
		}

	}

	public void addNewBrick(int position, Brick brickToBeAdded, boolean initInsertedBrick) {

		if (draggedBrick != null) {
			Log.w(TAG, "Want to add Brick while there is another one currently dragged.");
			return;
		}

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		int scriptCount = currentSprite.getNumberOfScripts();
		if (scriptCount == 0 && brickToBeAdded instanceof ScriptBrick) {
			currentSprite.addScript(((ScriptBrick) brickToBeAdded).getScriptSafe());
			initBrickList();
			notifyDataSetChanged();
			return;
		}

		if (position < 0) {
			position = 0;
		} else if (position > brickList.size()) {
			position = brickList.size();
		}

		if (brickToBeAdded instanceof ScriptBrick) {

			brickList.add(position, brickToBeAdded);
			position = getNewPositionForScriptBrick(position, brickToBeAdded);
			brickList.remove(brickToBeAdded);
			brickList.add(position, brickToBeAdded);
			scrollToPosition(position);

		} else {

			position = getNewPositionIfEndingBrickIsThere(position, brickToBeAdded);
			position = position <= 0 ? 1 : position;
			position = position > brickList.size() ? brickList.size() : position;
			brickList.add(position, brickToBeAdded);

		}

		this.initInsertedBrick = initInsertedBrick;
		this.positionOfInsertedBrick = position;

		if (scriptCount == 0 && userBrick == null) {
			Script script = new StartScript();
			currentSprite.addScript(script);
			brickList.add(0, script.getScriptBrick());
			ProjectManager.getInstance().setCurrentScript(script);
			positionOfInsertedBrick = 1;
		}

		notifyDataSetChanged();
	}

	private int getNewPositionForScriptBrick(int position, Brick brick) {
		if (brickList.size() == 0) {
			return 0;
		}
		if (!(brick instanceof ScriptBrick)) {
			return position;
		}

		int lastPossiblePosition = position;
		int nextPossiblePosition = position;

		for (int i = position; i < brickList.size(); i++) {
			if (brickList.get(i) instanceof NestingBrick) {
				List<NestingBrick> bricks = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts(true);
				int beginningPosition = brickList.indexOf(bricks.get(0));
				int endingPosition = brickList.indexOf(bricks.get(bricks.size() - 1));
				if (position >= beginningPosition && position <= endingPosition) {
					lastPossiblePosition = beginningPosition;
					nextPossiblePosition = endingPosition;
					i = endingPosition;
				}
			}

			if (brickList.get(i) instanceof ScriptBrick && brickList.get(i) != brick) {
				break;
			}
		}

		if (position <= lastPossiblePosition) {
			return position;
		} else if (position - lastPossiblePosition < nextPossiblePosition - position) {
			return lastPossiblePosition;
		} else {
			return nextPossiblePosition;
		}
	}

	@Override
	public void remove(int iWillBeIgnored) {
		// list will not be changed until user action ACTION_UP - therefore take the value from the begin
		removeFromBrickListAndProject(fromBeginDrag, false);
	}

	public void removeFromBrickListAndProject(int index, boolean removeScript) {
		if (addingNewBrick) {
			brickList.remove(draggedBrick);
		} else if (script == null) {
			int[] temp = getScriptAndBrickIndexFromProject(index);
			Script script = ProjectManager.getInstance().getCurrentSprite().getScript(temp[0]);
			if (script != null) {

				Brick brick = script.getBrick(temp[1]);
				if (brick instanceof NestingBrick) {
					for (NestingBrick tempBrick : ((NestingBrick) brick).getAllNestingBrickParts(true)) {
						script.removeBrick((Brick) tempBrick);
					}
				} else {
					script.removeBrick(brick);
				}
				if (removeScript) {
					brickList.remove(script);
				}
			}
		} else {

			Brick brick = script.getBrick(getPositionInUserScript(index));
			if (brick instanceof NestingBrick) {
				for (NestingBrick tempBrick : ((NestingBrick) brick).getAllNestingBrickParts(true)) {
					script.removeBrick((Brick) tempBrick);
				}
			} else {
				script.removeBrick(brick);
			}
		}

		firstDrag = true;
		draggedBrick = null;
		addingNewBrick = false;

		initBrickList();
		notifyDataSetChanged();
	}

	public void removeDraggedBrick() {
		if (!addingNewBrick) {
			draggedBrick = null;
			firstDrag = true;
			notifyDataSetChanged();

			return;
		}

		brickList.remove(draggedBrick);

		firstDrag = true;
		draggedBrick = null;
		addingNewBrick = false;

		initBrickList();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return brickList.size();
	}

	@Override
	public Object getItem(int element) {
		if (element < 0 || element >= brickList.size()) {
			return null;
		}
		return brickList.get(element);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (draggedBrick != null && dragTargetPosition == position) {
			return insertionView;
		}
		listItemCount = position + 1;

		Brick item = (Brick) getItem(position);

		//TODO: IllyaBoyko: NO Extra ScriptBrick handling now.
//		if (item instanceof ScriptBrick && (!initInsertedBrick || position != positionOfInsertedBrick)) {
//			View scriptBrickView = ((Brick) item).getView(context, position, this);
//			if (draggedBrick == null) {
//				scriptBrickView.setOnClickListener(this);
//			}
//			return scriptBrickView;
//		}

		BrickView currentBrickView = null;
		// dirty HACK
		// without the footer, position can be 0, and list.get(-1) caused an Indexoutofboundsexception
		// no clean solution was found
		if (convertView != null) {
			currentBrickView = (BrickView) convertView;
		} else {
			if (item instanceof AllowedAfterDeadEndBrick && brickList.get(position == 0 ? position : (position - 1)) instanceof DeadEndBrick) {
				currentBrickView = (BrickView) ((AllowedAfterDeadEndBrick) item).getNoPuzzleView(context, position, this);
			} else {
				currentBrickView = brickViewFactory.createView(item, parent);
			}
			convertView = currentBrickView;
		}

		int alpha = ALPHA_FULL;
		if (actionMode) {
			currentBrickView.addMode(BrickView.Mode.SELECTION);
			if (dragAndDropListView.isItemChecked(position) && !(item instanceof WhenStartedBrick)) {
				alpha = ALPHA_GREYED;
			}
		} else {
			// Remove selection mode
			currentBrickView.removeMode(BrickView.Mode.SELECTION);
		}
		currentBrickView.applyAlpha(alpha);


		if (position == positionOfInsertedBrick && initInsertedBrick && (selectMode == ListView.CHOICE_MODE_NONE)) {
			initInsertedBrick = false;
			addingNewBrick = true;
			dragAndDropListView.setInsertedBrick(position);

			dragAndDropListView.setDraggingNewBrick();
			dragAndDropListView.performDragging(currentBrickView, position);

			return insertionView;
		}

		Log.v(TAG, "getView '" + position + "' animate positions: " + toAnimatePositions.size() + "; " + toAnimatePositions);
		if (item != null && toAnimatePositions.contains(position)) {
			startBlinkAnimation(context, convertView);
			toAnimatePositions.remove(position);
		}
		return convertView;
	}

	public void updateProjectBrickList() {
		initBrickList();
		notifyDataSetChanged();
	}

	@Override
	public void setTouchedScript(int index) {
		if (index >= 0 && index < brickList.size() && brickList.get(index) instanceof ScriptBrick
				&& draggedBrick == null) {
			int scriptIndex = getScriptIndexFromProject(index);
			if (scriptIndex == -1) {
				Log.e(TAG, "setTouchedScript() Could not get ScriptIndex. index was " + index);
				return;
			}
			ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
		}
	}

	public int getScriptIndexFromProject(int index) {
		int scriptIndex = 0;
		Script temporaryScript = null;
		for (int i = 0; i < index;) {
			temporaryScript = sprite.getScript(scriptIndex);
			if (temporaryScript == null) {
				Log.e(TAG, "getScriptIndexFromProject() tmpScript was null. Index was " + index + " scriptIndex was " + scriptIndex);
				return -1;
			}
			i += temporaryScript.getBrickList().size() + 1;
			if (i <= index) {
				scriptIndex++;
			}
		}

		return scriptIndex;
	}

	public int getChildCountFromLastGroup() {
		return ProjectManager.getInstance().getCurrentSprite().getScript(getScriptCount() - 1).getBrickList().size();
	}

	public Brick getChild(int scriptPosition, int brickPosition) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		return sprite.getScript(scriptPosition).getBrick(brickPosition);
	}

	public int getScriptCount() {
		return ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public int getAmountOfCheckedItems() {
		throw new UnsupportedOperationException("ListView Api should be used!");
	}

	@Override
	public void clearCheckedItems() {
		throw new UnsupportedOperationException("ListView Api should be used!");
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return null;
	}

	public void checkAllItems() {
		for (Brick brick : brickList) {
			if (brick instanceof ScriptBrick) {
				smartBrickSelection(brickList.indexOf(brick), true);
			}
		}
	}

	public void clearToAnimatePositions() {
		toAnimatePositions.clear();
	}

	public void animateBricks(List<NestingBrick> nestingBrickParts) {
		if (nestingBrickParts == null || nestingBrickParts.isEmpty()) {
			return;
		}
		for (NestingBrick nestingBrickPart : nestingBrickParts) {
			if (nestingBrickPart instanceof Brick) {
				int position = brickList.indexOf((Brick) nestingBrickPart);
				if (position > -1) {
					//Start animation not from getView method.
					// Animation is started from dialog when list view has no focus.
					startBlinkAnimation(context, dragAndDropListView.getChildAt(position), true);
				}
			}
		}
	}

	public interface OnBrickCheckedListener {
		void onBrickChecked();
	}

	public void setOnBrickCheckedListener(OnBrickCheckedListener listener) {
		scriptFragment = listener;
	}

	public void handleCheck(int position, boolean checked) {
		// Method is called by list view in item check.
		// lisView requests layouting by itself.
		// no notify datadet needed.
		smartBrickSelection(position, checked);
	}

	/**
	 * Smart selection is applied to position. Method invalidated ListView.
	 * It will request layout by itself.
	 *
	 * @param position position to check.
	 * @param checked  true when item should be checked.
	 * @return true when smart selection applied
	 */
	private boolean smartBrickSelection(int position, boolean checked) {
		boolean smartSelection = false;
		Brick checkedBrick = (Brick) getItem(position);

		//Current checkedBrick is normally already checked by listView, and only animation is needed.
		// But checking from code should also work correctly.
		setBrickChecked(checked, position, checkedBrick);

		if (checkedBrick instanceof ScriptBrick) {

			int brickPosition = brickList.indexOf(checkedBrick) + 1;
			while ((brickPosition < brickList.size()) && !(brickList.get(brickPosition) instanceof ScriptBrick)) {
				Brick currentBrick = brickList.get(brickPosition);
				if (currentBrick == null) {
					break;
				} else if (currentBrick == checkedBrick) {
					/*already selected when same.*/
					continue;
				}

				setBrickChecked(checked, brickPosition, currentBrick);

				brickPosition++;
			}

			smartSelection = true;
		} else if (checkedBrick instanceof NestingBrick) {
			for (NestingBrick currentNestingBrick : ((NestingBrick) checkedBrick).getAllNestingBrickParts(true)) {
				if (currentNestingBrick == null) {
					break;
				} else if (currentNestingBrick == checkedBrick) {
					/*already selected when same.*/
					continue;
				}
				Brick currentBrick = (Brick) currentNestingBrick;
				setBrickChecked(checked, brickList.indexOf(currentBrick), currentBrick);
			}

			smartSelection = true;
		}

		if (scriptFragment != null) {
			scriptFragment.onBrickChecked();
		}

		return smartSelection;
	}

	/**
	 * @param checked true when checked
	 * @param brick   brick
	 */
	private void setBrickChecked(boolean checked, int position, Brick brick) {
		if (checked) {
			toAnimatePositions.add(position);
		}
		if (!UserScriptDefinitionBrick.class.equals(brick.getClass())) {
			Log.v(TAG, "Selecting Item position '" + position + "' for Brick '" + brick.getClass().getSimpleName() + "'");
			dragAndDropListView.setItemChecked(position, checked);
		}

	}

	public UserBrick getUserBrick() {
		return userBrick;
	}

	public void setUserBrick(UserBrick userBrick) {
		this.userBrick = userBrick;
	}
}
