/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

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
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListener;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.UtilDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class BrickAdapter extends BrickBaseAdapter implements DragAndDropListener, OnClickListener {

	public enum ActionModeEnum {
		NO_ACTION, COPY_DELETE, BACKPACK, COMMENT_OUT
	}

	private static final String TAG = BrickAdapter.class.getSimpleName();
	private Sprite sprite;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private BrickListView brickDragAndDropListView;
	private View insertionView;
	private boolean initInsertedBrick;
	private boolean addingNewBrick;
	private int positionOfInsertedBrick;

	private boolean firstDrag;
	private int fromBeginDrag;
	private int toEndDrag;
	private boolean retryScriptDragging;
	public boolean isDragging = false;

	private ActionModeEnum actionMode = ActionModeEnum.NO_ACTION;

	public BrickAdapter(ScriptFragment scriptFragment, Sprite sprite, BrickListView listView) {
		this.scriptFragment = scriptFragment;
		this.context = scriptFragment.getActivity();
		this.sprite = sprite;
		brickDragAndDropListView = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		addingNewBrick = false;
		firstDrag = true;
		retryScriptDragging = false;
		refreshBrickList();
	}

	public Context getContext() {
		return context;
	}

	public void refreshBrickList() {
		brickList = ProjectManager.getInstance().getCurrentSprite().getAllBricks();
	}

	public ActionModeEnum getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionModeEnum actionMode) {
		this.actionMode = actionMode;
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
			retryScriptDragging = currentPosition != to;
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
			if (isScriptOrInNestingBrickList(brickList.get(i), nestingBrickList)) {
				restrictedTop = i;
				break;
			}
		}

		for (int i = currentPosition; i < restrictedBottom; i++) {
			if (isScriptOrInNestingBrickList(brickList.get(i), nestingBrickList)) {
				restrictedBottom = i;
				break;
			}
		}

		to = to <= restrictedTop ? restrictedTop + 1 : to;
		to = to >= restrictedBottom ? restrictedBottom - 1 : to;

		return to;
	}

	private boolean isScriptOrInNestingBrickList(Brick brick, List<NestingBrick> nestingBrickList) {
		return brick instanceof ScriptBrick || brick instanceof NestingBrick && !nestingBrickList.contains(brick);
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
				addBrickToPositionInProject(to, draggedBrick);
			}

			if (!draggedBrick.isCommentedOut()) {
				enableCorrespondingScriptBrick(to);
			}

			if (draggedBrick instanceof UserBrick) {
				((UserBrick) draggedBrick).updateUserBrickParametersAndVariables();
			}

			addingNewBrick = false;
		} else {
			if (draggedBrick instanceof NestingBrick) {
				moveNestingBrick(fromBeginDrag, toEndDrag);
			} else {
				moveExistingProjectBrick(fromBeginDrag, toEndDrag);
			}

			if (!draggedBrick.isCommentedOut()) {
				enableCorrespondingScriptBrick(toEndDrag);
			}
		}

		draggedBrick = null;
		firstDrag = true;

		refreshBrickList();
		notifyDataSetChanged();

		int scrollTo = to;
		if (scrollTo >= brickList.size() - 1) {
			scrollTo = getCount() - 1;
		}
		brickDragAndDropListView.smoothScrollToPosition(scrollTo);

		setSpinnersEnabled(true);
		isDragging = false;

		SnackbarUtil.showHintSnackbar(((Activity) getContext()), R.string.hint_brick_added);
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
		boolean isPositionCurrentlyVisible = brickDragAndDropListView.getFirstVisiblePosition() < position
				&& position < brickDragAndDropListView.getLastVisiblePosition();

		if (isPositionCurrentlyVisible) {
			return;
		}

		brickDragAndDropListView.setIsScrolling();
		if (position <= brickDragAndDropListView.getFirstVisiblePosition()) {
			brickDragAndDropListView.smoothScrollToPosition(0, position + 2);
		} else {
			brickDragAndDropListView.smoothScrollToPosition(brickList.size() - 1, position - 2);
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
			refreshBrickList();
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

		if (scriptCount == 0) {
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
		removeFromBrickListAndProject(fromBeginDrag, false);
	}

	public void removeFromBrickListAndProject(int index, boolean removeScript) {
		if (addingNewBrick) {
			brickList.remove(draggedBrick);
		} else {
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
		}

		firstDrag = true;
		draggedBrick = null;
		addingNewBrick = false;

		refreshBrickList();
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

		BrickBaseType brick = (BrickBaseType) getItem(position);

		View view = brick.getView(context, this);
		view.setOnClickListener(this);

		if (!(brick instanceof ScriptBrick)) {
			view.setOnLongClickListener(brickDragAndDropListView);
		}

		boolean spinnersClickable = !isDragging && actionMode == ActionModeEnum.NO_ACTION;
		BrickViewProvider.setSpinnerClickable(view, spinnersClickable);

		if (position == positionOfInsertedBrick && initInsertedBrick && actionMode == ActionModeEnum.NO_ACTION) {
			initInsertedBrick = false;
			addingNewBrick = true;

			brickDragAndDropListView.setInsertedBrick(position);
			brickDragAndDropListView.setDraggingNewBrick();
			brickDragAndDropListView.onLongClick(view);
			return insertionView;
		}

		return view;
	}

	@Override
	public void setTouchedScript(int index) {
		if (index >= 0 && index < brickList.size()
				&& brickList.get(index) instanceof ScriptBrick
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

	@Override
	public void onClick(final View view) {
		if (actionMode != ActionModeEnum.NO_ACTION || isDragging) {
			return;
		}

		final int itemPosition = brickDragAndDropListView.pointToPosition(view.getLeft(), view.getTop());
		final Brick brick = brickList.get(itemPosition);
		final List<CharSequence> items = new ArrayList<>();

		if (brick instanceof ScriptBrick) {
			items.add(context.getText(R.string.backpack_add));
			items.add(context.getText(R.string.brick_context_dialog_delete_script));
		} else {
			if (!(brick instanceof DeadEndBrick)) {
				items.add(context.getText(R.string.brick_context_dialog_move_brick));
			}
			items.add(context.getText(R.string.brick_context_dialog_copy_brick));
			items.add(context.getText(R.string.brick_context_dialog_delete_brick));
		}

		if (brick.isCommentedOut()) {
			items.add(context.getText(R.string.brick_context_dialog_comment_in));
		} else {
			items.add(context.getText(R.string.brick_context_dialog_comment_out));
		}

		if (brick instanceof FormulaBrick) {
			items.add(context.getText(R.string.brick_context_dialog_formula_edit_brick));
		}

		if (hasDescription(brick)) {
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

			ImageView imageView = brickDragAndDropListView.getGlowingBorder(bitmap);
			builder.setCustomTitle(imageView);
		}

		builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				CharSequence clickedItemText = items.get(item);
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_move_brick))) {
					view.performLongClick();
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_copy_brick))) {
					copyBrick(itemPosition, brick);
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_delete_brick))
						|| clickedItemText.equals(context.getText(R.string.brick_context_dialog_delete_script))) {
					showDeleteAlert(brick);
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_formula_edit_brick))) {
					if (!(brick instanceof FormulaBrick)) {
						throw new IllegalStateException(brick.getClass().getSimpleName() + " is not a FormulaBrick: "
								+ "You probably incorrectly added the \"Edit Formula\" option to th context menu for "
								+ "this brick.");
					}
					((FormulaBrick) brick).showFormulaEditorToEditFormula(view);
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.backpack_add))) {
					int currentPosition = itemPosition;
					checkedBricks.add(brickList.get(currentPosition));
					currentPosition++;
					while (!(currentPosition >= brickList.size() || brickList.get(currentPosition) instanceof ScriptBrick)) {
						checkedBricks.add(brickList.get(currentPosition));
						currentPosition++;
					}
					scriptFragment.showNewScriptGroupDialog();
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_comment_in))) {
					commentBrickOut(brick, false);
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_comment_out))) {
					commentBrickOut(brick, true);
					return;
				}
				if (clickedItemText.equals(context.getText(R.string.brick_context_dialog_help))) {
					openHelpPageForBrick(brick);
				}
			}
		});

		builder.create().show();
	}

	private void copyBrick(int currentPosition, Brick brick) {
		try {
			Brick clone = brick.clone();
			// The next line should be removed once we are sure that all bricks call super.clone() in their clone method
			// so that the comment state is correctly set on the new instance.
			clone.setCommentedOut(brick.isCommentedOut());
			addNewBrick(currentPosition, clone, true);
			notifyDataSetChanged();
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

//	private void copyBrick(Brick brick) {
//		if (brick instanceof NestingBrick
//				&& (brick instanceof AllowedAfterDeadEndBrick || brick instanceof DeadEndBrick)) {
//			return;
//		}
//
//		if (brick instanceof ScriptBrick) {
//			try {
//				Script clonedScript = ((ScriptBrick) brick).getScriptSafe().clone();
//				sprite.addScript(clonedScript);
//				adapter.refreshBrickList();
//				adapter.notifyDataSetChanged();
//			} catch (CloneNotSupportedException e) {
//				Log.e(TAG, Log.getStackTraceString(e));
//			}
//			return;
//		}
//
//		int brickId = adapter.getBrickList().indexOf(brick);
//		if (brickId == -1) {
//			return;
//		}
//
//		int newPosition = adapter.getCount();
//
//		try {
//			Brick copiedBrick = brick.clone();
//
//			Script scriptList = ProjectManager.getInstance().getCurrentScript();
//
//			if (brick instanceof NestingBrick) {
//				NestingBrick nestingBrickCopy = (NestingBrick) copiedBrick;
//				nestingBrickCopy.initialize();
//
//				for (NestingBrick nestingBrick : nestingBrickCopy.getAllNestingBrickParts(true)) {
//					scriptList.addBrick((Brick) nestingBrick);
//				}
//			} else {
//				scriptList.addBrick(copiedBrick);
//			}
//
//			adapter.addNewBrick(newPosition, copiedBrick, false);
//			adapter.refreshBrickList();
//
//			ProjectManager.getInstance().saveProject(getActivity().getApplicationContext());
//			adapter.notifyDataSetChanged();
//		} catch (CloneNotSupportedException exception) {
//			Log.e(getTag(), "Copying a Brick failed", exception);
//			ToastUtil.showError(getActivity(), R.string.error_copying_brick);
//		}
//	}

	private void showDeleteAlert(final Brick brick) {
		String alertDialogTitle = brick instanceof ScriptBrick
				? context.getResources().getQuantityString(R.plurals.delete_scripts, 1)
				: context.getResources().getQuantityString(R.plurals.delete_bricks, 1);

		new AlertDialog.Builder(context)
				.setTitle(alertDialogTitle)
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteBrick(brick);
					}
				})
				.setNegativeButton(R.string.no, null)
				.setCancelable(false)
				.create()
				.show();
	}

	private void deleteBrick(Brick brick) {
//		if (brick instanceof ScriptBrick) {
//			scriptToEdit = ((ScriptBrick) brick).getScriptSafe();
//			adapter.handleScriptDelete(sprite, scriptToEdit);
//			return;
//		}
//		int brickId = adapter.getBrickList().indexOf(brick);
//		if (brickId == -1) {
//			return;
//		}
//		adapter.removeFromBrickListAndProject(brickId, true);
//		spriteToEdit.removeScript(scriptToDelete);
//		if (spriteToEdit.getNumberOfScripts() == 0) {
//			ProjectManager.getInstance().setCurrentScript(null);
//		} else {
//			int lastScriptIndex = spriteToEdit.getNumberOfScripts() - 1;
//			Script lastScript = spriteToEdit.getScript(lastScriptIndex);
//			ProjectManager.getInstance().setCurrentScript(lastScript);
//		}
	}

	private boolean hasDescription(Brick brick) {
		if (brick instanceof IfLogicElseBrick) {
			return false;
		}
		if (brick instanceof IfLogicEndBrick) {
			return false;
		}
		if (brick instanceof IfThenLogicEndBrick) {
			return false;
		}
		if (brick instanceof LoopEndlessBrick) {
			return false;
		}
		if (brick instanceof LoopEndBrick) {
			return false;
		}
		if (brick instanceof UserBrick) {
			return false;
		}
		return true;
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

	public void clearCheckedItems() {
		actionMode = ActionModeEnum.NO_ACTION;
		checkedBricks.clear();

		for (Brick brick : brickList) {
			setCheckbox(brick, false);
			handleCheck(brick, false);
			BrickViewProvider.setCheckboxVisibility(brick, View.GONE);
			BrickViewProvider.setAlphaOnBrick(brick, BrickViewProvider.ALPHA_FULL);
		}

		notifyDataSetChanged();
	}

	public void setCheckbox(Brick brick, boolean enabled) {
		CheckBox checkBox = brick.getCheckBox();
		if (checkBox != null) {
			checkBox.setChecked(enabled);
		}
	}

	public void checkCommentedOutItems() {
		for (Brick brick : brickList) {
			if (brick.isCommentedOut()) {
				setCheckbox(brick, true);
			}
		}
	}

	public void updateCheckBoxVisibility() {
		for (Brick brick : brickList) {
			switch (actionMode) {
				case NO_ACTION:
					brick.getCheckBox().setVisibility(View.GONE);
					break;
				case BACKPACK:
					if (brick instanceof ScriptBrick) {
						brick.getCheckBox().setVisibility(View.VISIBLE);
					} else {
						brick.getCheckBox().setVisibility(View.INVISIBLE);
					}
					break;
				case COPY_DELETE:
				case COMMENT_OUT:
					brick.getCheckBox().setVisibility(View.VISIBLE);
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
			BrickViewProvider.setSpinnerClickable(((BrickBaseType) brick).view, enabled);
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
					BrickViewProvider.setAlphaOnBrick(currentBrick, alphaValue);
					setCheckbox(currentBrick, checked);
					BrickViewProvider.setCheckBoxClickable(currentBrick, !checked);
					break;
				case COMMENT_OUT:
					currentBrick.setCommentedOut(checked);
					BrickViewProvider.setSaturationOnBrick(currentBrick, checked);
					setCheckbox(currentBrick, checked);
					BrickViewProvider.setCheckBoxClickable(currentBrick, !checked);
					break;
			}
		}

		scriptFragment.updateActionModeTitle(checkedBricks.size());
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

	public List<Brick> getCheckedBricks() {
		return checkedBricks;
	}
}
