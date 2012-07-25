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
package at.tugraz.ist.catroid.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.NestingBrick;
import at.tugraz.ist.catroid.content.bricks.ScriptBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener {

	public static final int FOCUS_BLOCK_DESCENDANTS = 2;

	private Context context;
	private Sprite sprite;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private OnLongClickListener longClickListener;
	private View insertionView;
	private int currentScriptPosition;
	private boolean initInsertedBrick;
	private boolean insertedBrick;
	private int positionOfInsertedBrick;

	private boolean firstDrag;
	private int fromBeginDrag, toEndDrag;
	private boolean retryScriptDragging;

	private List<Brick> brickList;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		longClickListener = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		insertedBrick = false;
		firstDrag = true;
		retryScriptDragging = false;

		initBrickList();
	}

	private void initBrickList() {
		brickList = new ArrayList<Brick>();

		ProjectManager pm = ProjectManager.getInstance();
		Sprite sprite = pm.getCurrentSprite();

		for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
			Script script = sprite.getScript(i);
			brickList.add(script.getScriptBrick());
			for (Brick brick : script.getBrickList()) {
				brickList.add(brick);
			}
		}
	}

	public void drag(int from, int to) {
		if (to < 0) {
			to = brickList.size() - 1;
		}
		if (from < 0) {
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
			if (nestingBrick.isFullyCreated()) {
				to = getDraggedNestingBricksToPosition(nestingBrick, from, to);
			}
		} else if (draggedBrick instanceof ScriptBrick) {
			int oldTo = to;
			brickList.remove(draggedBrick);
			brickList.add(to, draggedBrick);
			to = getNewPositionForScriptBrick(to, draggedBrick);
			dragTargetPosition = to;
			if (oldTo != to) {
				retryScriptDragging = true;
			} else {
				retryScriptDragging = false;
			}
		}

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

		notifyDataSetChanged();
	}

	private int getDraggedNestingBricksToPosition(NestingBrick nestingBrick, int from, int to) {
		List<NestingBrick> nestingBrickList = nestingBrick.getAllNestingBrickParts();
		int restrictedTop = 0;
		int restrictedBottom = brickList.size() - 1;

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

	public void drop(int to) {
		if (retryScriptDragging) {
			scrollToPosition(dragTargetPosition);
			draggedBrick = null;
			initInsertedBrick = true;
			positionOfInsertedBrick = dragTargetPosition;
			notifyDataSetChanged();

			retryScriptDragging = false;
			return;
		}

		if (insertedBrick) {
			if (draggedBrick instanceof ScriptBrick) {
				addScriptToProject(to, (ScriptBrick) draggedBrick);
			} else {
				addBrickToPosition(to, draggedBrick);
			}

			insertedBrick = false;
		} else {
			dragBrickInProject(fromBeginDrag, toEndDrag);
		}

		draggedBrick = null;
		firstDrag = true;

		initBrickList();
		notifyDataSetChanged();
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
		ProjectManager.getInstance().saveProject();
	}

	private void dragBrickInProject(int from, int to) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] tempFrom = getScriptAndBrickIndexFromProject(from);
		int[] tempTo = getScriptAndBrickIndexFromProject(to);

		int scriptPositionFrom = tempFrom[0];
		int brickPositionFrom = tempFrom[1];
		int scriptPositionTo = tempTo[0];
		int brickPositionTo = tempTo[1];

		Script fromScript = currentSprite.getScript(scriptPositionFrom);
		Script toScript = currentSprite.getScript(scriptPositionTo);

		if (fromScript == null || toScript == null) {
			Log.e("BrickAdapter", "Want to save project, but scripts are null");
			return;
		}

		Brick brick = fromScript.getBrick(brickPositionFrom);
		if (draggedBrick != brick) {
			Log.e("BrickAdapter", "Want to save wrong brick");
			return;
		}

		fromScript.removeBrick(brick);
		toScript.addBrick(brickPositionTo, brick);
		ProjectManager.getInstance().saveProject();
	}

	private void addBrickToPosition(int position, Brick brick) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] temp = getScriptAndBrickIndexFromProject(position);

		int scriptPosition = temp[0];
		int brickPosition = temp[1];

		Script script = currentSprite.getScript(scriptPosition);

		if (brick instanceof NestingBrick) {
			((NestingBrick) draggedBrick).createFully();
			List<NestingBrick> nestingBrickList = ((NestingBrick) draggedBrick).getAllNestingBrickParts();
			for (int i = 0; i < nestingBrickList.size(); i++) {
				script.addBrick(brickPosition + i, nestingBrickList.get(i));
			}
		} else {
			script.addBrick(brickPosition, brick);
		}

		ProjectManager.getInstance().saveProject();
	}

	private int[] getScriptAndBrickIndexFromProject(int position) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] returnValue = new int[2];
		int countPosition = 0;
		int scriptCount = 0;

		if (position > 0) {
			position--;
			for (int i = 0; i < currentSprite.getNumberOfScripts(); i++) {
				if (countPosition + currentSprite.getScript(i).getBrickList().size() < position) {
					countPosition += currentSprite.getScript(i).getBrickList().size() + 1;
					scriptCount++;
				} else {
					break;
				}
			}
		} else {
			scriptCount = 0;
		}

		returnValue[0] = scriptCount;
		int brickPosition = position - countPosition;
		returnValue[1] = brickPosition < 0 ? currentSprite.getScript(scriptCount).getBrickList().size() : brickPosition;

		return returnValue;
	}

	private void scrollToPosition(int position) {
		ListView list = ((ListView) longClickListener);
		if (list.getFirstVisiblePosition() < position && position < list.getLastVisiblePosition()) {
			return;
		}

		if (position <= list.getFirstVisiblePosition()) {
			list.smoothScrollToPosition(0, position);
		} else {
			list.smoothScrollToPosition(brickList.size() - 1, position);
		}
	}

	public void addNewBrick(int position, Brick brickToBeAdded) {
		if (draggedBrick != null) {
			Log.w("BrickAdapter", "Want to add Brick while there is another one currently dragged.");
			return;
		}

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
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
			position = getNewPositionForScriptBrick(position, brickToBeAdded);
			scrollToPosition(position);
			brickList.add(position, brickToBeAdded);
		} else {
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

		int lastPossiblePosition = position;
		int nextPossiblePosition = position;

		for (int i = position; i < brickList.size(); i++) {
			if (brickList.get(i) instanceof NestingBrick) {
				List<NestingBrick> bricks = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts();
				int beginPos = brickList.indexOf(bricks.get(0));
				int endPos = brickList.indexOf(bricks.get(bricks.size() - 1));
				if (position >= beginPos && position <= endPos) {
					lastPossiblePosition = beginPos;
					nextPossiblePosition = endPos;
					i = endPos;
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

	public void remove(int index) {
		if (insertedBrick) {
			brickList.remove(draggedBrick);
		} else {
			int temp[] = getScriptAndBrickIndexFromProject(fromBeginDrag);
			Script script = ProjectManager.getInstance().getCurrentSprite().getScript(temp[0]);
			Brick brick = script.getBrick(temp[1]);
			if (brick instanceof NestingBrick) {
				for (Brick tempBrick : ((NestingBrick) brick).getAllNestingBrickParts()) {
					script.removeBrick(tempBrick);
				}
			} else {
				script.removeBrick(brick);
			}
		}

		ProjectManager.getInstance().saveProject();
		firstDrag = true;
		draggedBrick = null;
		insertedBrick = false;

		initBrickList();
		notifyDataSetChanged();
	}

	public OnLongClickListener getOnLongClickListener() {
		return longClickListener;
	}

	public int getCount() {
		return brickList.size();
	}

	public Object getItem(int element) {
		return brickList.get(element);
	}

	public int getBrickCount(int scriptIndex) {
		Script script = sprite.getScript(scriptIndex);
		if (script == null) {
			return -1;
		}

		return script.getBrickList().size();
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (draggedBrick != null && dragTargetPosition == position) {
			return insertionView;
		}

		Object item = getItem(position);

		if (item instanceof ScriptBrick && (!initInsertedBrick || position != positionOfInsertedBrick)) {
			// TODO delete me
			//return ((ScriptBrick) item).initScript(sprite).getScriptBrick().getView(context, position, this);
			return ((ScriptBrick) item).getView(context, position, this);
			//return null;
		}

		View currentBrickView = ((Brick) item).getView(context, position, this);

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
			wrapper.setOnLongClickListener(longClickListener);
		}

		if (position == positionOfInsertedBrick && initInsertedBrick) {
			initInsertedBrick = false;
			insertedBrick = true;
			((DragAndDropListView) parent).setInsertedBrick(position);
			DragAndDropListView listView = (DragAndDropListView) ((ScriptActivity) context)
					.findViewById(R.id.brick_list_view);

			listView.setDraggingNewBrick();
			listView.onLongClick(currentBrickView);

			return insertionView;
		}

		return wrapper;
	}

	public void updateProjectBrickList() {
		Log.e("blah", "blub");
		initBrickList();
		notifyDataSetChanged();
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

	public void setTouchedScript(int index) {
		int scriptIndex = getScriptIndexFromProject(index);
		ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
		setCurrentScriptPosition(index);
	}

	public void setCurrentScriptPosition(int position) {
		currentScriptPosition = position;
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

}
