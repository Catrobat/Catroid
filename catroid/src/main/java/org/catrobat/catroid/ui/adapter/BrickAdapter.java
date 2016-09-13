/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AllowedAfterDeadEndBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.content.bricks.DeadEndBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserBrickParameter;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListener;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.UtilDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class BrickAdapter extends BrickBaseAdapter implements DragAndDropListener, OnClickListener,
		ActionModeActivityAdapterInterface {

	public enum ActionModeEnum {
		NO_ACTION, COPY_DELETE, BACKPACK, COMMENT_OUT
	}

	private static final String TAG = BrickAdapter.class.getSimpleName();
	public int listItemCount = 0;
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
	private Script scriptToDelete;

	private boolean firstDrag;
	private int fromBeginDrag;
	private int toEndDrag;
	private boolean retryScriptDragging;
	private boolean showDetails = false;
	public boolean isDragging = false;

	private List<Brick> animatedBricks;

	private int selectMode;

	private Lock viewSwitchLock = new ViewSwitchLock();
	private int clickItemPosition = 0;
	private AlertDialog alertDialog = null;

	private ActionModeEnum actionMode = ActionModeEnum.NO_ACTION;

	public BrickAdapter(ScriptFragment scriptFragment, Sprite sprite, DragAndDropListView listView) {
		this.scriptFragment = scriptFragment;
		this.context = scriptFragment.getActivity();
		this.sprite = sprite;
		dragAndDropListView = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		addingNewBrick = false;
		firstDrag = true;
		retryScriptDragging = false;
		animatedBricks = new ArrayList<>();
		this.selectMode = ListView.CHOICE_MODE_NONE;
		initBrickList();
	}

	public Context getContext() {
		return context;
	}

	public void initBrickList() {
		brickList = new ArrayList<>();

		if (userBrick != null) {
			initBrickListUserScript();
			return;
		}
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		int numberOfScripts = sprite.getNumberOfScripts();
		for (int scriptPosition = 0; scriptPosition < numberOfScripts; scriptPosition++) {
			Script script = sprite.getScript(scriptPosition);
			brickList.add(script.getScriptBrick());
			script.getScriptBrick().setBrickAdapter(this);
			for (Brick brick : script.getBrickList()) {
				brickList.add(brick);
				brick.setBrickAdapter(this);
			}
		}
	}

	private void initBrickListUserScript() {
		script = getUserScript();
		brickList = new ArrayList<>();
		brickList.add(script.getScriptBrick());

		script.getScriptBrick().setBrickAdapter(this);
		for (Brick brick : script.getBrickList()) {
			brickList.add(brick);
			brick.setBrickAdapter(this);
		}
	}

	private Script getUserScript() {
		UserScriptDefinitionBrick definitionBrick = userBrick.getDefinitionBrick();
		return definitionBrick.getScriptSafe();
	}

	public void resetAlphas() {
		for (Brick brick : brickList) {
			brick.setAlpha(BrickViewProvider.ALPHA_FULL);
		}
		notifyDataSetChanged();
	}

	public ActionModeEnum getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionModeEnum actionMode) {
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
				if (nestingBrick.getAllNestingBrickParts(true).get(0) == nestingBrick) {
					to = adjustNestingBrickDraggedPosition(nestingBrick, fromBeginDrag, to);
				} else {
					to = getDraggedNestingBricksToPosition(nestingBrick, to);
				}
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

		animatedBricks.clear();

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

	private int adjustNestingBrickDraggedPosition(NestingBrick nestingBrick, int from, int to) {
		List<NestingBrick> nestingBrickList = nestingBrick.getAllNestingBrickParts(true);
		NestingBrick endBrick = nestingBrickList.get(nestingBrickList.size() - 1);
		int endBrickPosition = brickList.indexOf(endBrick);

		boolean isNewPositionBetweenStartAndEndNestedBrick = to > from && to < endBrickPosition;
		if (isNewPositionBetweenStartAndEndNestedBrick) {
			return endBrickPosition;
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

			if (!draggedBrick.isCommentedOut()) {
				enableCorrespondingScriptBrick(to);
			}

			if (draggedBrick instanceof UserBrick) {
				((UserBrick) draggedBrick).updateUserBrickParametersAndVariables();
			}

			addingNewBrick = false;
		} else {
			if (script != null) {
				moveUserBrick(fromBeginDrag, toEndDrag);
			} else {
				if (draggedBrick instanceof NestingBrick) {
					moveNestingBrick(fromBeginDrag, toEndDrag);
				} else {
					moveExistingProjectBrick(fromBeginDrag, toEndDrag);
				}
			}

			if (!draggedBrick.isCommentedOut()) {
				enableCorrespondingScriptBrick(toEndDrag);
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
		setSpinnersEnabled(true);
		isDragging = false;
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

	private void moveNestingBrick(int from, int to) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] tempFrom = getScriptAndBrickIndexFromProject(from);
		int scriptPositionFrom = tempFrom[0];
		int brickPositionFrom = tempFrom[1];

		Script fromScript = currentSprite.getScript(scriptPositionFrom);
		Brick brick = fromScript.getBrick(brickPositionFrom);

		NestingBrick nestingBrick = (NestingBrick) brick;
		List<NestingBrick> nestingBricks = nestingBrick.getAllNestingBrickParts(true);
		NestingBrick endNestingBrick = nestingBricks.get(nestingBricks.size() - 1);
		List<Brick> fromScriptBrickList = fromScript.getBrickList();
		int endPosition = fromScriptBrickList.indexOf(endNestingBrick);
		int count = endPosition - brickPositionFrom;
		boolean isNewPositionBetweenStartAndEndNestedBrick = to > from && count > to - from;
		if (isNewPositionBetweenStartAndEndNestedBrick) {
			return; // moved nested block into itself. prevent this by the UI!
		}

		List<Brick> block = fromScriptBrickList.subList(brickPositionFrom, endPosition + 1);
		List<Brick> removedBlock = new ArrayList<>();
		removedBlock.add(block.remove(0));

		int[] tempTo = getScriptAndBrickIndexFromProject(to);
		int scriptPositionTo = tempTo[0];
		int brickPositionTo = tempTo[1];
		Script toScript = currentSprite.getScript(scriptPositionTo);

		removedBlock.addAll(block);
		block.clear();

		int finalBrickPositionTo = brickPositionTo;
		boolean moveBrickInSameScript = scriptPositionTo == scriptPositionFrom && to > from;
		if (moveBrickInSameScript) {
			finalBrickPositionTo -= count;
		}
		toScript.getBrickList().addAll(finalBrickPositionTo, removedBlock);
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

	private int[] getScriptAndBrickIndexFromProject(int position) {
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
			clearCheckedItems();
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

				BrickBaseType brick = (BrickBaseType) script.getBrick(temp[1]);
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

			BrickBaseType brick = (BrickBaseType) script.getBrick(getPositionInUserScript(index));
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

		BrickBaseType brick = (BrickBaseType) getItem(position);

		View currentBrickView = brick.getView(context, position, this);
		BrickViewProvider.setSaturationOnView(currentBrickView, brick.isCommentedOut());
		currentBrickView.setOnClickListener(this);

		if (!(brick instanceof ScriptBrick)) {
			currentBrickView.setOnLongClickListener(dragAndDropListView);
		}

		boolean enableSpinners = !isDragging && actionMode == ActionModeEnum.NO_ACTION;
		setSpinnersEnabled(enableSpinners);

		if (position == positionOfInsertedBrick && initInsertedBrick && (selectMode == ListView.CHOICE_MODE_NONE)) {
			initInsertedBrick = false;
			addingNewBrick = true;
			dragAndDropListView.setInsertedBrick(position);

			dragAndDropListView.setDraggingNewBrick();
			dragAndDropListView.onLongClick(currentBrickView);

			return insertionView;
		}

		return currentBrickView;
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

	private int getScriptIndexFromProject(int index) {
		int scriptIndex = 0;
		Script temporaryScript;
		for (int i = 0; i < index; ) {
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

	public AlertDialog getAlertDialog() {
		return alertDialog;
	}

	@Override
	public void onClick(final View view) {
		if (actionMode != ActionModeEnum.NO_ACTION) {
			return;
		}

		if (isDragging) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}

		animatedBricks.clear();
		final int itemPosition = calculateItemPositionAndTouchPointY(view);
		final List<CharSequence> items = new ArrayList<>();

		if (brickList.get(itemPosition) instanceof ScriptBrick) {
			int scriptIndex = getScriptIndexFromProject(itemPosition);
			ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
		}

		if (!(brickList.get(itemPosition) instanceof DeadEndBrick)
				&& !(brickList.get(itemPosition) instanceof ScriptBrick)) {
			items.add(context.getText(R.string.brick_context_dialog_move_brick));
		}
		if ((brickList.get(itemPosition) instanceof UserBrick)) {
			items.add(context.getText(R.string.brick_context_dialog_show_source));
		}
		if (brickList.get(itemPosition) instanceof NestingBrick) {
			items.add(context.getText(R.string.brick_context_dialog_animate_bricks));
		}
		if (!(brickList.get(itemPosition) instanceof ScriptBrick)) {
			items.add(context.getText(R.string.brick_context_dialog_copy_brick));
			items.add(context.getText(R.string.brick_context_dialog_delete_brick));
		} else {
			items.add(context.getText(R.string.brick_context_dialog_delete_script));
			items.add(context.getText(R.string.backpack_add));
		}
		if (brickHasAFormula(brickList.get(itemPosition))) {
			items.add(context.getText(R.string.brick_context_dialog_formula_edit_brick));
		}
		if (brickList.get(itemPosition).isCommentedOut()) {
			items.add(context.getText(R.string.brick_context_dialog_comment_in));
		} else {
			items.add(context.getText(R.string.brick_context_dialog_comment_out));
		}
		if (!(brickList.get(itemPosition) instanceof UserBrick)) {
			items.add(context.getText(R.string.brick_context_dialog_help));
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		view.buildDrawingCache(true);

		if (view.getDrawingCache() != null) {
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(drawingCacheEnabled);

			ImageView imageView = dragAndDropListView.getGlowingBorder(bitmap);
			builder.setCustomTitle(imageView);
		}

		builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				CharSequence clickedItemText = items.get(item);
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_move_brick))) {
					view.performLongClick();
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_show_source))) {
					launchAddBrickAndSelectBrickAt(context, itemPosition);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_copy_brick))) {
					copyBrickListAndProject(itemPosition);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_delete_brick))
						|| clickedItemText.equals(context.getText(R.string.brick_context_dialog_delete_script))) {
					showConfirmDeleteDialog(itemPosition);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_animate_bricks))) {
					int itemPosition = calculateItemPositionAndTouchPointY(view);
					Brick brick = brickList.get(itemPosition);
					if (brick instanceof NestingBrick) {
						List<NestingBrick> list = ((NestingBrick) brick).getAllNestingBrickParts(true);
						for (NestingBrick tempBrick : list) {
							animatedBricks.add((Brick) tempBrick);
						}
					}
					notifyDataSetChanged();
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_formula_edit_brick))) {
					clickedEditFormula(brickList.get(itemPosition), view);
				} else if (clickedItemText.equals(context.getText(R.string.backpack_add))) {
					int currentPosition = itemPosition;
					checkedBricks.add(brickList.get(currentPosition));
					currentPosition++;
					while (!(currentPosition >= brickList.size()
							|| brickList.get(currentPosition) instanceof ScriptBrick)) {
						checkedBricks.add(brickList.get(currentPosition));
						currentPosition++;
					}
					List<String> backPackedScriptGroups = BackPackListManager.getInstance().getAllBackPackedScriptGroups();
					showNewGroupBackPackDialog(backPackedScriptGroups, false);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_comment_in))) {
					commentBrickOut(brickList.get(itemPosition), false);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_comment_out))) {
					commentBrickOut(brickList.get(itemPosition), true);
				} else if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_help))) {
					openHelpPageForBrick(brickList.get(itemPosition));
				}
			}
		});
		alertDialog = builder.create();

		if ((selectMode == ListView.CHOICE_MODE_NONE)) {
			alertDialog.show();
		}
	}

	protected void copyBrickListAndProject(int itemPosition) {
		Brick origin = (Brick) (dragAndDropListView.getItemAtPosition(itemPosition));
		Brick copy;
		try {
			copy = origin.clone();
			copy.setCommentedOut(origin.isCommentedOut());
			addNewBrick(itemPosition, copy, true);
			notifyDataSetChanged();
		} catch (CloneNotSupportedException exception) {
			Log.e(TAG, Log.getStackTraceString(exception));
		}
	}

	private void showConfirmDeleteDialog(int itemPosition) {
		this.clickItemPosition = itemPosition;
		int titleId;

		if (getItem(clickItemPosition) instanceof ScriptBrick) {
			titleId = R.string.dialog_confirm_delete_script_title;
		} else {
			titleId = R.string.dialog_confirm_delete_brick_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(context);
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_brick_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (getItem(clickItemPosition) instanceof ScriptBrick) {
					scriptToDelete = ((ScriptBrick) getItem(clickItemPosition)).getScriptSafe();
					handleScriptDelete(sprite, scriptToDelete);
					scriptToDelete = null;
				} else {
					removeFromBrickListAndProject(clickItemPosition, false);
				}
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				scriptToDelete = null;
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void clickedEditFormula(Brick brick, View view) {
		FormulaBrick formulaBrick = null;
		if (brick instanceof FormulaBrick) {
			formulaBrick = (FormulaBrick) brick;
		}
		if (brick instanceof UserBrick) {
			List<UserBrickParameter> userBrickParameters = ((UserBrick) brick).getUserBrickParameters();
			if (userBrickParameters != null && userBrickParameters.size() > 0) {
				formulaBrick = userBrickParameters.get(0);
			}
		}

		if (formulaBrick != null) {
			formulaBrick.showFormulaEditorToEditFormula(view);
		}
	}

	private boolean brickHasAFormula(Brick brick) {
		boolean multiFormulaValid = false;
		if (brick instanceof UserBrick) {
			multiFormulaValid = ((UserBrick) brick).getFormulas().size() > 0;
		}
		return (brick instanceof FormulaBrick || multiFormulaValid);
	}

	public void launchAddBrickAndSelectBrickAt(Context context, int index) {
		int[] temp = getScriptAndBrickIndexFromProject(index);
		Script script = ProjectManager.getInstance().getCurrentSprite().getScript(temp[0]);
		if (script != null) {
			Brick brick = script.getBrick(temp[1]);

			if (!viewSwitchLock.tryLock()) {
				return;
			}

			if (brick instanceof UserBrick) {
				UserBrick selectedUserBrick = (UserBrick) brick;
				selectedUserBrick.updateUserBrickParametersAndVariables();
				AddBrickFragment.launchUserBrickScriptActivity(context, selectedUserBrick);
			}
		}
	}

	private void openHelpPageForBrick(Brick brick) {
		CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();
		String language = UtilDeviceInfo.getUserLanguageCode();
		String category = categoryBricksFactory.getBrickCategory(brick, sprite, context);
		String name = brick.getClass().getSimpleName();

		if (!language.equals("en") && !language.equals("de") && !language.equals("es")) {
			language = "en";
		}
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wiki.catrob.at/index"
				+ ".php?title=" + category + "_Bricks/" + language + "#" + name));
		getContext().startActivity(browserIntent);
	}

	private int calculateItemPositionAndTouchPointY(View view) {
		return dragAndDropListView.pointToPosition(view.getLeft(), view.getTop());
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return getCheckedBricks().size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return null;
	}

	@Override
	public void clearCheckedItems() {
		actionMode = ActionModeEnum.NO_ACTION;
		checkedBricks.clear();
		enableAllBricks();
		notifyDataSetChanged();
	}

	public void setCheckbox(Brick brick, boolean enabled) {
		CheckBox checkBox = brick.getCheckBox();
		if (checkBox != null) {
			checkBox.setChecked(enabled);
		}
	}

	private void enableAllBricks() {
		unCheckAllItems();
		for (Brick brick : brickList) {
			BrickViewProvider.setCheckboxVisibility(brick, View.GONE);
			BrickViewProvider.setAlphaForBrick(brick, BrickViewProvider.ALPHA_FULL);
		}
	}

	public void checkAllItems() {
		for (Brick brick : brickList) {
			setCheckbox(brick, true);
			handleCheck(brick, true);
		}
	}

	private void unCheckAllItems() {
		for (Brick brick : brickList) {
			setCheckbox(brick, false);
			handleCheck(brick, false);
		}
	}

	public void checkCommentedOutItems() {
		for (Brick brick : brickList) {
			if (brick.isCommentedOut()) {
				setCheckbox(brick, true);
			}
		}
	}

	public void setCheckboxVisibility() {
		for (Brick brick : brickList) {
			switch (actionMode) {
				case NO_ACTION:
					BrickViewProvider.setCheckboxVisibility(brick, View.GONE);
					break;
				case BACKPACK:
					if (brick instanceof ScriptBrick) {
						BrickViewProvider.setCheckboxVisibility(brick, View.VISIBLE);
					} else {
						BrickViewProvider.setCheckboxVisibility(brick, View.INVISIBLE);
					}
					break;
				case COPY_DELETE:
				case COMMENT_OUT:
					BrickViewProvider.setCheckboxVisibility(brick, View.VISIBLE);
					break;
			}
		}
	}

	private void commentBrickOut(Brick brick, boolean commentOut) {
		actionMode = ActionModeEnum.COMMENT_OUT;
		handleCheck(brick, commentOut);
		actionMode = ActionModeEnum.NO_ACTION;
	}

	public void setSpinnersEnabled(boolean enabled) {
		for (Brick brick : brickList) {
			BrickViewProvider.setSpinnerClickability(((BrickBaseType) brick).view, enabled);
		}
	}

	public void handleCheck(Brick brick, boolean checked) {
		int positionFrom = brickList.indexOf(brick);
		int positionTo = brickList.indexOf(brick);

		if (brick instanceof NestingBrick) {
			List<NestingBrick> nestingBricks = ((NestingBrick) brick).getAllNestingBrickParts(true);
			NestingBrick firstNestingBrick = nestingBricks.get(0);
			NestingBrick lastNestingBrick = nestingBricks.get(nestingBricks.size() - 1);

			if (actionMode != ActionModeEnum.NO_ACTION) {
				setCheckbox((Brick) firstNestingBrick, checked);
			}

			positionFrom = brickList.indexOf(firstNestingBrick);
			positionTo = brickList.indexOf(lastNestingBrick);
		} else if (brick instanceof ScriptBrick) {
			positionTo = brickList.size() - 1;
		}

		if (checked) {
			addElementToCheckedBricks(brick);
		} else {
			checkedBricks.remove(brick);
		}

		if (actionMode == ActionModeEnum.COMMENT_OUT) {
			brick.setCommentedOut(checked);
			BrickViewProvider.setSaturationOnBrick(brick, checked);
		}

		positionFrom++;

		for (int position = positionFrom; position <= positionTo; position++) {
			Brick currentBrick = brickList.get(position);
			if (currentBrick == null) {
				break;
			}
			if (currentBrick instanceof ScriptBrick) {
				break;
			}

			if (checked) {
				addElementToCheckedBricks(currentBrick);
			} else {
				checkedBricks.remove(currentBrick);
			}

			switch (actionMode) {
				case NO_ACTION:
					break;
				case COPY_DELETE:
				case BACKPACK:
					int alphaValue = checked ? BrickViewProvider.ALPHA_GREYED : BrickViewProvider.ALPHA_FULL;
					BrickViewProvider.setAlphaForBrick(currentBrick, alphaValue);
					setCheckbox(currentBrick, checked);
					BrickViewProvider.setCheckboxClickability(currentBrick, !checked);
					break;
				case COMMENT_OUT:
					currentBrick.setCommentedOut(checked);
					BrickViewProvider.setSaturationOnBrick(currentBrick, checked);
					setCheckbox(currentBrick, checked);
					BrickViewProvider.setCheckboxClickability(currentBrick, !checked);
					break;
			}
		}
		if (scriptFragment.getActionModeActive()) {
			scriptFragment.updateActionModeTitle();
		}
	}

	void enableCorrespondingScriptBrick(int indexBegin) {
		for (int i = indexBegin; i >= 0; i--) {
			Brick currentBrick = brickList.get(i);
			if (currentBrick instanceof ScriptBrick) {
				currentBrick.setCommentedOut(false);
				BrickViewProvider.setSaturationOnBrick(currentBrick, false);
				break;
			}
		}
	}

	private void addElementToCheckedBricks(Brick brick) {
		if (!(checkedBricks.contains(brick)) && !(brick instanceof UserScriptDefinitionBrick)) {
			checkedBricks.add(brick);
		}
	}

	public void onDestroyActionModeBackPack() {
		actionMode = ActionModeEnum.NO_ACTION;
		List<String> backPackedScriptGroups = BackPackListManager.getInstance().getAllBackPackedScriptGroups();
		showNewGroupBackPackDialog(backPackedScriptGroups, false);
	}

	public void handleScriptDelete(Sprite spriteToEdit, Script scriptToDelete) {
		spriteToEdit.removeScript(scriptToDelete);
		if (spriteToEdit.getNumberOfScripts() == 0) {
			ProjectManager.getInstance().setCurrentScript(null);
			updateProjectBrickList();
		} else {
			int lastScriptIndex = spriteToEdit.getNumberOfScripts() - 1;
			Script lastScript = spriteToEdit.getScript(lastScriptIndex);
			ProjectManager.getInstance().setCurrentScript(lastScript);
			updateProjectBrickList();
		}
	}

	public List<Brick> getCheckedBricks() {
		return checkedBricks;
	}

	public List<Brick> getReversedCheckedBrickList() {
		List<Brick> reverseCheckedList = new ArrayList<>();
		for (int counter = checkedBricks.size() - 1; counter >= 0; counter--) {
			reverseCheckedList.add(checkedBricks.get(counter));
		}
		return reverseCheckedList;
	}

	public UserBrick getUserBrick() {
		return userBrick;
	}

	public void setUserBrick(UserBrick userBrick) {
		this.userBrick = userBrick;
	}

	public void animateUnpackingFromBackpack(int numberOfInsertedBricks) {
		int insertedBricksStartPosition = brickList.size() - 1 - numberOfInsertedBricks;
		if (insertedBricksStartPosition < 0) {
			return;
		}
		int maxNumberAnimatedBricks = 4;
		for (int position = insertedBricksStartPosition; position < brickList.size() && ((position
				- insertedBricksStartPosition) < maxNumberAnimatedBricks); position++) {
			if (position < brickList.size()) {
				animatedBricks.add(brickList.get(position));
			}
		}
		scrollToPosition(insertedBricksStartPosition);
	}
}
