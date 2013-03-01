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
package org.catrobat.catroid.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AllowedAfterDeadEndBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DeadEndBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener, OnClickListener,
		ScriptActivityAdapterInterface {

	private static final String TAG = BrickAdapter.class.getSimpleName();
	private Context context;
	private Sprite sprite;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private DragAndDropListView dragAndDropListView;
	private View insertionView;
	private boolean initInsertedBrick;
	private boolean addingNewBrick;
	private int positionOfInsertedBrick;

	private boolean firstDrag;
	private int fromBeginDrag, toEndDrag;
	private boolean retryScriptDragging;
	private boolean showDetails = false;

	//public for testing:
	public List<Brick> brickList;
	private List<Brick> animatedBricks;
	//private SortedSet<Integer> checkedBricks = new TreeSet<Integer>();
	private List<Brick> checkedBricks = new ArrayList<Brick>();

	private int selectMode;
	private OnBrickEditListener onBrickEditListener;

	public int listItemCount = 0;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		dragAndDropListView = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		addingNewBrick = false;
		firstDrag = true;
		retryScriptDragging = false;
		animatedBricks = new ArrayList<Brick>();
		this.selectMode = Constants.SELECT_NONE;
		initBrickList();
	}

	private void initBrickList() {
		brickList = new ArrayList<Brick>();

		Sprite sprite = ProjectManager.INSTANCE.getCurrentSprite();

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

	@Override
	public void drag(int from, int to) {
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
				to = getDraggedNestingBricksToPosition(nestingBrick, from, to);
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
		brickList.add(dragTargetPosition, draggedBrick);

		toEndDrag = to;

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

	private int getDraggedNestingBricksToPosition(NestingBrick nestingBrick, int from, int to) {
		List<NestingBrick> nestingBrickList = nestingBrick.getAllNestingBrickParts();
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
				addBrickToPosition(to, draggedBrick);
			}

			addingNewBrick = false;
		} else {
			moveExistingProjectBrick(fromBeginDrag, toEndDrag);
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

		Script newScript = scriptBrick.initScript(currentSprite);
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

	private void addBrickToPosition(int position, Brick brick) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] temp = getScriptAndBrickIndexFromProject(position);

		int scriptPosition = temp[0];
		int brickPosition = temp[1];

		Script script = currentSprite.getScript(scriptPosition);

		if (brick instanceof NestingBrick) {
			((NestingBrick) draggedBrick).initialize();
			List<NestingBrick> nestingBrickList = ((NestingBrick) draggedBrick).getAllNestingBrickParts();
			for (int i = 0; i < nestingBrickList.size(); i++) {
				if (nestingBrickList.get(i) instanceof DeadEndBrick) {
					if (i < nestingBrickList.size() - 1) {
						Log.w(TAG, "Adding a DeadEndBrick in the middle of the NestingBricks");
					}
					position = getPositionForDeadEndBrick(position);
					temp = getScriptAndBrickIndexFromProject(position);
					script.addBrick(temp[1], nestingBrickList.get(i));
				} else {
					script.addBrick(brickPosition + i, nestingBrickList.get(i));
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
				List<NestingBrick> tempList = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts();
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

			//just a hack for the moment:
			//position = brickList.size() - 1;

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
		for (scriptOffset = 0; scriptOffset < position;) {
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

	public void addNewBrick(int position, Brick brickToBeAdded) {
		if (draggedBrick != null) {
			Log.w(TAG, "Want to add Brick while there is another one currently dragged.");
			return;
		}

		Sprite currentSprite = ProjectManager.INSTANCE.getCurrentSprite();
		int scriptCount = currentSprite.getNumberOfScripts();
		if (scriptCount == 0 && brickToBeAdded instanceof ScriptBrick) {
			currentSprite.addScript(((ScriptBrick) brickToBeAdded).initScript(currentSprite));
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

		initInsertedBrick = true;
		positionOfInsertedBrick = position;

		if (scriptCount == 0) {
			Script script = new StartScript(currentSprite);
			currentSprite.addScript(script);
			brickList.add(0, script.getScriptBrick());
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
				List<NestingBrick> bricks = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts();
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
		} else {
			int temp[] = getScriptAndBrickIndexFromProject(index);
			Script script = ProjectManager.INSTANCE.getCurrentSprite().getScript(temp[0]);
			if (script != null) {

				Brick brick = script.getBrick(temp[1]);
				if (brick instanceof NestingBrick) {
					for (Brick tempBrick : ((NestingBrick) brick).getAllNestingBrickParts()) {
						script.removeBrick(tempBrick);
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

	public OnLongClickListener getOnLongClickListener() {
		return dragAndDropListView;
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

		Object item = getItem(position);

		if (item instanceof ScriptBrick && (!initInsertedBrick || position != positionOfInsertedBrick)) {
			return ((Brick) item).getView(context, position, this);
		}

		View currentBrickView;
		// dirty HACK
		// without the footer, position can be 0, and list.get(-1) caused an Indexoutofboundsexception
		// no clean solution was found
		if (position == 0) {
			if (item instanceof AllowedAfterDeadEndBrick && brickList.get(position) instanceof DeadEndBrick) {
				currentBrickView = ((AllowedAfterDeadEndBrick) item).getNoPuzzleView(context, position, this);
			} else {
				currentBrickView = ((Brick) item).getView(context, position, this);
			}
		} else {
			if (item instanceof AllowedAfterDeadEndBrick && brickList.get(position - 1) instanceof DeadEndBrick) {
				currentBrickView = ((AllowedAfterDeadEndBrick) item).getNoPuzzleView(context, position, this);
			} else {
				currentBrickView = ((Brick) item).getView(context, position, this);
			}
		}

		// this one is working but causes null pointer exceptions on movement and control bricks?!
		//		currentBrickView.setOnLongClickListener(longClickListener);

		// Hack!!!
		// if wrapper isn't used the longClick event won't be triggered
		ViewGroup wrapper = (ViewGroup) View.inflate(context, R.layout.brick_wrapper, null);
		if (currentBrickView.getParent() != null) {
			((ViewGroup) currentBrickView.getParent()).removeView(currentBrickView);
		}

		wrapper.addView(currentBrickView);
		if (draggedBrick == null) {
			if ((selectMode == Constants.SELECT_NONE)) {
				wrapper.setOnClickListener(this);
				if (!(item instanceof DeadEndBrick)) {
					wrapper.setOnLongClickListener(dragAndDropListView);
				}
			}
		}

		if (position == positionOfInsertedBrick && initInsertedBrick && (selectMode == Constants.SELECT_NONE)) {
			initInsertedBrick = false;
			addingNewBrick = true;
			dragAndDropListView.setInsertedBrick(position);

			dragAndDropListView.setDraggingNewBrick();
			dragAndDropListView.onLongClick(currentBrickView);

			return insertionView;
		}

		if (animatedBricks.contains(brickList.get(position))) {
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);
			wrapper.startAnimation(animation);
			animatedBricks.remove(brickList.get(position));
		}

		return wrapper;
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
			ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
		}
	}

	private int getScriptIndexFromProject(int index) {
		int scriptIndex = 0;
		for (int i = 0; i < index;) {

			i += sprite.getScript(scriptIndex).getBrickList().size() + 1;
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
	public void onClick(final View view) {
		/*
		 * if (selectMode == Constants.SELECT_NONE) {
		 * return;
		 * }
		 */
		animatedBricks.clear();
		final int itemPosition = calculateItemPositionAndTouchPointY(view);

		final List<CharSequence> items = new ArrayList<CharSequence>();
		if (!(brickList.get(itemPosition) instanceof DeadEndBrick)) {
			items.add(context.getText(R.string.brick_context_dialog_move_brick));
		}
		if (brickList.get(itemPosition) instanceof NestingBrick) {
			items.add(context.getText(R.string.brick_context_dialog_animate_bricks));
		}
		items.add(context.getText(R.string.brick_context_dialog_delete_brick));

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
		view.setDrawingCacheEnabled(true);
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
				if (items.get(item).equals(context.getText(R.string.brick_context_dialog_move_brick))) {
					view.performLongClick();
				} else if (items.get(item).equals(context.getText(R.string.brick_context_dialog_delete_brick))) {
					removeFromBrickListAndProject(itemPosition, false);
				} else if (items.get(item).equals(context.getText(R.string.brick_context_dialog_animate_bricks))) {
					int itemPosition = calculateItemPositionAndTouchPointY(view);
					Brick brick = brickList.get(itemPosition);
					if (brick instanceof NestingBrick) {
						List<NestingBrick> list = ((NestingBrick) brick).getAllNestingBrickParts();
						for (Brick tempBrick : list) {
							animatedBricks.add(tempBrick);
						}
					}
					notifyDataSetChanged();
				}
			}
		});
		AlertDialog alertDialog = builder.create();

		if ((selectMode == Constants.SELECT_NONE)) {
			alertDialog.show();
		}
	}

	private int calculateItemPositionAndTouchPointY(View view) {
		int itemPosition = AdapterView.INVALID_POSITION;
		itemPosition = dragAndDropListView.pointToPosition(view.getLeft(), view.getTop());

		return itemPosition;
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
		return getCheckedBricks().size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return null;
	}

	public List<Brick> getCheckedBrickList() {
		return getCheckedBricks();
	}

	@Override
	public void clearCheckedItems() {
		getCheckedBricks().clear();
		setCheckboxVisibility(View.GONE);
		uncheckAllItems();
		notifyDataSetChanged();
	}

	private void uncheckAllItems() {
		for (Brick brick : brickList) {
			CheckBox checkbox = brick.getCheckBox();
			if (checkbox != null) {
				checkbox.setChecked(false);
			}
		}
	}

	public void setCheckboxVisibility(int visibility) {
		for (Brick brick : brickList) {
			brick.setCheckboxVisibility(visibility);
		}
	}

	public interface OnBrickEditListener {

		public void onBrickEdit(View v);

		public void onBrickChecked();
	}

	public void setOnBrickEditListener(OnBrickEditListener listener) {
		onBrickEditListener = listener;
	}

	public void handleCheck(Brick brick, boolean isChecked) {
		if (isChecked) {
			if (selectMode == Constants.SINGLE_SELECT) {
				clearCheckedItems();
			}
			if (brick.getCheckBox() != null && brick.getCheckBox().isChecked() && smartBrickSelection(brick, isChecked)) {
				return;
			}
			addElementToCheckedBricks(brick);
		} else {
			if (brick.getCheckBox() != null && !brick.getCheckBox().isChecked()
					&& smartBrickSelection(brick, isChecked)) {
				return;
			}
			getCheckedBricks().remove(brick);
		}
		notifyDataSetChanged();

		if (onBrickEditListener != null) {
			onBrickEditListener.onBrickChecked();
		}
	}

	private void handleBrickEnabledState(Brick brick, boolean enableState) {
		if (brick.getCheckBox() != null) {
			brick.getCheckBox().setEnabled(enableState);
		}

		if (enableState) {
			brick.getViewWithAlpha(255);
		} else {
			brick.getViewWithAlpha(50);
		}
	}

	private boolean smartBrickSelection(Brick brick, boolean check) {

		if (brick instanceof ScriptBrick) {
			if (check) {
				addElementToCheckedBricks(brick);
				animatedBricks.add(brick);
			} else {
				getCheckedBricks().remove(brick);
			}
			brick.getCheckBox().setChecked(check);
			notifyDataSetChanged();

			int brickPosition = brickList.indexOf(brick) + 1;
			while ((brickPosition < brickList.size()) && !(brickList.get(brickPosition) instanceof ScriptBrick)) {
				Brick currentBrick = brickList.get(brickPosition);
				if (check) {
					addElementToCheckedBricks(currentBrick);
					animatedBricks.add(currentBrick);
				} else {
					getCheckedBricks().remove(currentBrick);
				}
				if (currentBrick.getCheckBox() != null) {
					currentBrick.getCheckBox().setChecked(check);
				}
				handleBrickEnabledState(currentBrick, !check);
				notifyDataSetChanged();
				brickPosition++;
			}

			animateSelectedBricks();

			if (onBrickEditListener != null) {
				onBrickEditListener.onBrickChecked();
			}
			return true;
		} else if (brick instanceof NestingBrick) {
			int counter = 1;
			int from = 0;
			int to = 0;
			for (Brick currentBrick : ((NestingBrick) brick).getAllNestingBrickParts()) {
				if (check) {
					animatedBricks.add(currentBrick);
					addElementToCheckedBricks(currentBrick);
				} else {
					getCheckedBricks().remove(currentBrick);
				}
				if (counter == 1) {
					from = brickList.indexOf(currentBrick);
					counter++;
				} else {
					to = brickList.indexOf(currentBrick);
				}
				currentBrick.getCheckBox().setChecked(check);
				notifyDataSetChanged();
			}
			Brick ToBrick = brickList.get(to);
			if (from > to) {
				handleBrickEnabledState(ToBrick, !check);
				int temp = from;
				from = to;
				to = temp;
			} else {
				handleBrickEnabledState(ToBrick, !check);
			}
			from++;
			while (from < to) {
				Brick currentBrick = brickList.get(from);
				if (check) {
					animatedBricks.add(currentBrick);
					addElementToCheckedBricks(currentBrick);
				} else {
					getCheckedBricks().remove(currentBrick);
				}
				currentBrick.getCheckBox().setChecked(check);
				handleBrickEnabledState(currentBrick, !check);
				notifyDataSetChanged();
				from++;
			}

			animateSelectedBricks();

			if (onBrickEditListener != null) {
				onBrickEditListener.onBrickChecked();
			}
			return true;
		}
		return false;
	}

	private void animateSelectedBricks() {
		if (!animatedBricks.isEmpty()) {
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink);
			for (Brick animationBrick : animatedBricks) {
				int position = animatedBricks.indexOf(animationBrick);
				View view = animationBrick.getView(context, position, this);
				view.startAnimation(animation);
			}
		}
		animatedBricks.clear();
	}

	private void addElementToCheckedBricks(Brick brick) {
		if (!(getCheckedBricks().contains(brick))) {
			getCheckedBricks().add(brick);
		}
	}

	public void handleScriptDelete(Sprite spriteToEdit, Script scriptToDelete) {
		spriteToEdit.removeScript(scriptToDelete);
		if (spriteToEdit.getNumberOfScripts() == 0) {
			ProjectManager.INSTANCE.setCurrentScript(null);
			updateProjectBrickList();
		} else {
			int lastScriptIndex = spriteToEdit.getNumberOfScripts() - 1;
			Script lastScript = spriteToEdit.getScript(lastScriptIndex);
			ProjectManager.INSTANCE.setCurrentScript(lastScript);
			updateProjectBrickList();
		}
	}

	public List<Brick> getCheckedBricks() {
		return checkedBricks;
	}

	public void setCheckedBricks(List<Brick> checkedBricks) {
		this.checkedBricks = checkedBricks;
	}

	public List<Brick> getReversedCheckedBrickList() {
		List<Brick> reverseCheckedList = new ArrayList<Brick>();
		for (int counter = checkedBricks.size() - 1; counter >= 0; counter--) {
			reverseCheckedList.add(checkedBricks.get(counter));
		}
		return reverseCheckedList;
	}
}
