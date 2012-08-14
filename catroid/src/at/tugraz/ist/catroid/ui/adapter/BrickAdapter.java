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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.AllowedAfterDeadEndBrick;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.DeadEndBrick;
import at.tugraz.ist.catroid.content.bricks.NestingBrick;
import at.tugraz.ist.catroid.content.bricks.ScriptBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener, OnClickListener {

	public static final int FOCUS_BLOCK_DESCENDANTS = 2;

	private Context context;
	private Sprite sprite;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private DragAndDropListView dragAndDropListView;
	private View insertionView;
	private boolean initInsertedBrick;
	private boolean insertedBrick;
	private int positionOfInsertedBrick;

	private boolean firstDrag;
	private int fromBeginDrag, toEndDrag;
	private boolean retryScriptDragging;

	private List<Brick> brickList;
	private List<Brick> animatedBricks;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		dragAndDropListView = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		initInsertedBrick = false;
		insertedBrick = false;
		firstDrag = true;
		retryScriptDragging = false;
		animatedBricks = new ArrayList<Brick>();
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

	@Override
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
			if (nestingBrick.isInitialized()) {
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
	public void drop(int to) {
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

		scrollToPosition(to);
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

	private void dragBrickInProject(int from, int to) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int[] tempFrom = getScriptAndBrickIndexFromProject(from);
		int scriptPositionFrom = tempFrom[0];
		int brickPositionFrom = tempFrom[1];

		Script fromScript = currentSprite.getScript(scriptPositionFrom);

		Brick brick = fromScript.getBrick(brickPositionFrom);
		if (draggedBrick != brick) {
			Log.e("BrickAdapter", "Want to save wrong brick");
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
						Log.w("BrickAdapter", "Adding a DeadEndBrick in the middle of the NestingBricks");
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
			if (brickList.get(i) instanceof DeadEndBrick) {
				return i;
			}

			if (brickList.get(i) instanceof NestingBrick) {
				List<NestingBrick> tempList = ((NestingBrick) brickList.get(i)).getAllNestingBrickParts();
				int oldI = i;
				i = brickList.indexOf(tempList.get(tempList.size() - 1)) + 1;
				if (i < 0) {
					i = oldI;
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

	@Override
	public void remove(int iWillBeIgnored) {
		// list will not be changed until user action ACTION_UP - therefore take the value from the begin
		removeFromBrickListAndProject(fromBeginDrag);
	}

	private void removeFromBrickListAndProject(int index) {
		if (insertedBrick) {
			brickList.remove(draggedBrick);
		} else {
			int temp[] = getScriptAndBrickIndexFromProject(index);
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

		firstDrag = true;
		draggedBrick = null;
		insertedBrick = false;

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

	public int getBrickCount(int scriptIndex) {
		Script script = sprite.getScript(scriptIndex);
		if (script == null) {
			return -1;
		}

		return script.getBrickList().size();
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

		Object item = getItem(position);

		if (item instanceof ScriptBrick && (!initInsertedBrick || position != positionOfInsertedBrick)) {
			return ((ScriptBrick) item).getView(context, position, this);
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
			wrapper.setOnClickListener(this);
			if (!(item instanceof DeadEndBrick)) {
				wrapper.setOnLongClickListener(dragAndDropListView);
			}
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

	@Override
	public void setTouchedScript(int index) {
		if (index >= 0 && brickList.get(index) instanceof ScriptBrick && draggedBrick == null) {
			int scriptIndex = getScriptIndexFromProject(index);
			ProjectManager.getInstance().setCurrentScript(sprite.getScript(scriptIndex));
		}
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

	public View getInsertionView() {
		return insertionView;
	}

	@Override
	public void onClick(final View view) {
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

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		TextView textView = new TextView(context);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(23);
		textView.setGravity(Gravity.LEFT);
		textView.setPadding(10, 15, 0, 0);
		textView.setText(context.getText(R.string.brick_context_dialog_title));
		linearLayout.addView(textView);

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);

		if (view.getDrawingCache() == null) {
			linearLayout.addView(view);
		} else {
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(drawingCacheEnabled);

			ImageView imageView = dragAndDropListView.getGlowingBorder(bitmap);
			linearLayout.addView(imageView);
		}

		builder.setCustomTitle(linearLayout);

		builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items.get(item).equals(context.getText(R.string.brick_context_dialog_move_brick))) {
					view.performLongClick();
				} else if (items.get(item).equals(context.getText(R.string.brick_context_dialog_delete_brick))) {
					removeFromBrickListAndProject(itemPosition);
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
		alertDialog.show();
	}

	private int calculateItemPositionAndTouchPointY(View view) {
		int itemPosition = -1;
		itemPosition = dragAndDropListView.pointToPosition(view.getLeft(), view.getTop());

		return itemPosition;
	}

}
