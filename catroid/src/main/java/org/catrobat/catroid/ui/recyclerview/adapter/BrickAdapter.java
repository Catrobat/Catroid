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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.dragndrop.BrickAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.ViewStateManager;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrickAdapter extends BaseAdapter implements BrickAdapterInterface,
		AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

	private static final float DISABLED_BRICK_ALPHA = .8f;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, ALL, SCRIPTS_ONLY})
	@interface CheckBoxMode {}
	public static final int NONE = 0;
	public static final int ALL = 1;
	public static final int SCRIPTS_ONLY = 2;

	@CheckBoxMode
	public int checkBoxMode = NONE;

	private List<Script> scripts = new ArrayList<>();
	private List<BrickBaseType> items = new ArrayList<>();

	private MultiSelectionManager selectionManager = new MultiSelectionManager();
	private ViewStateManager viewStateManager = new ViewStateManager();

	private OnItemClickListener onItemClickListener;
	private SelectionListener selectionListener;

	private BrickController brickController = new BrickController();

	public BrickAdapter(Sprite sprite) {
		updateItems(sprite);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void updateItems(Sprite sprite) {
		scripts = sprite.getScriptList();
		updateItemsFromCurrentScripts();
	}

	private void updateItemsFromCurrentScripts() {
		items.clear();
		for (Script script : scripts) {
			items.add((BrickBaseType) script.getScriptBrick());
			for (Brick brick : script.getBrickList()) {
				items.add((BrickBaseType) brick);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		BrickBaseType item = items.get(position);

		View itemView = item.getView(parent.getContext());
		item.onViewCreated();
		itemView.setVisibility(viewStateManager.isVisible(position) ? View.VISIBLE : View.INVISIBLE);
		itemView.setAlpha(viewStateManager.isEnabled(position) ? 1 : DISABLED_BRICK_ALPHA);

		Drawable background = ((ViewGroup) itemView).getChildAt(1).getBackground();
		if (item.isCommentedOut()) {
			ColorMatrix matrix = new ColorMatrix();
			matrix.setSaturation(0);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
			background.setColorFilter(filter);
		} else {
			background.clearColorFilter();
		}

		item.getCheckBox().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onCheckBoxClick(position);
			}
		});

		switch (checkBoxMode) {
			case NONE:
				item.getCheckBox().setVisibility(View.GONE);
				if (item instanceof FormulaBrick) {
					((FormulaBrick) item).setClickListeners();
				}
				break;
			case ALL:
				item.getCheckBox().setVisibility(View.VISIBLE);
				item.disableSpinners();
				break;
			case SCRIPTS_ONLY:
				boolean isScriptBrick = item instanceof ScriptBrick;
				item.getCheckBox().setVisibility(isScriptBrick ? View.VISIBLE : View.INVISIBLE);
				item.disableSpinners();
				break;
		}
		item.getCheckBox().setChecked(selectionManager.isPositionSelected(position));
		item.getCheckBox().setEnabled(viewStateManager.isEnabled(position));
		return itemView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkBoxMode == NONE) {
			BrickBaseType item = items.get(position);
			onItemClickListener.onItemClick(item, position);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkBoxMode == NONE) {
			BrickBaseType item = items.get(position);
			onItemClickListener.onItemLongClick(item, position);
			return true;
		}
		return false;
	}

	private void onCheckBoxClick(int position) {
		boolean selected = !selectionManager.isPositionSelected(position);
		setSelectionTo(selected, position);
		selectionListener.onSelectionChanged(selectionManager.getSelectedPositions().size());
		notifyDataSetChanged();
	}

	private void setSelectionTo(boolean selected, int position) {
		BrickBaseType item = items.get(position);
		selectionManager.setSelectionTo(selected, position);

		if (item instanceof ScriptBrick) {
			Script script = ((ScriptBrick) item).getScript();
			for (BrickBaseType brick : items) {
				if (script.getBrickList().contains(brick)) {
					selectionManager.setSelectionTo(selected, items.indexOf(brick));
					viewStateManager.setEnabled(!selected, items.indexOf(brick));
				}
			}
		} else if (item instanceof ControlStructureBrick) {
			List<Brick> bricksInControlStructure = brickController
					.getBricksInControlStructure((ControlStructureBrick) item, new ArrayList<Brick>(items));
			for (BrickBaseType brick : items) {
				if (bricksInControlStructure.contains(brick)) {
					selectionManager.setSelectionTo(selected, items.indexOf(brick));
					viewStateManager.setEnabled(!selected, items.indexOf(brick));
				}
			}
			viewStateManager.setEnabled(true, items.indexOf(bricksInControlStructure.get(0)));
		}
	}

	public List<Brick> getSelectedItems() {
		List<Brick> selectedItems = new ArrayList<>();
		for (int position : selectionManager.getSelectedPositions()) {
			selectedItems.add(items.get(position));
		}
		return selectedItems;
	}

	public void clearSelection() {
		selectionManager.clearSelection();
		viewStateManager.clearDisabledPositions();
		notifyDataSetChanged();
	}

	@Override
	public void setItemVisible(int position, boolean visible) {
		viewStateManager.setVisible(position, visible);
	}

	@Override
	public void setAllPositionsVisible() {
		viewStateManager.setAllPositionsVisible();
	}

	public void selectAllCommentedOutBricks() {
		PeekingIterator<BrickBaseType> iterator = Iterators.peekingIterator(items.iterator());

		while (iterator.hasNext()) {
			Brick brick = iterator.next();
			if (brick instanceof ScriptBrick && brick.isCommentedOut()) {
				Script script = ((ScriptBrick) brick).getScript();
				setSelectionTo(true, items.indexOf(brick));
				while (iterator.hasNext() && script.getBrickList().contains(iterator.peek())) {
					iterator.next();
				}
			} else {
				setSelectionTo(brick.isCommentedOut(), items.indexOf(brick));
			}
		}
		notifyDataSetChanged();
	}

	public void addItem(int position, BrickBaseType item) {
		items.add(position, item);
		notifyDataSetChanged();
	}

	@Override
	public BrickBaseType getItem(int position) {
		return items.get(position);
	}

	public BrickBaseType findByHash(int hashCode) {
		for (BrickBaseType item : items) {
			if (item.hashCode() == hashCode) {
				return item;
			}
		}
		return null;
	}

	public List<BrickBaseType> getItems() {
		return new ArrayList<>(items);
	}

	@Override
	public boolean removeItems(List<BrickBaseType> items) {
		if (this.items.removeAll(items)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public List<Integer> getPositionsOfItems(List<Brick> bricks) {
		List<Integer> positions = new ArrayList<>();
		for (Brick brick : bricks) {
			int position = items.indexOf(brick);
			positions.add(position);
		}
		return positions;
	}

	@Override
	public boolean onItemMove(int srcPosition, int targetPosition) {
		BrickBaseType src = items.get(srcPosition);
		if (!(src instanceof ScriptBrick) && targetPosition == 0) {
			return false;
		}

		BrickBaseType target = items.get(targetPosition);
		if (src instanceof ControlStructureBrick) {
			boolean isFirstBrickOfControlStructure = src.equals(((ControlStructureBrick) src).getFirstBrick());
			if (!isFirstBrickOfControlStructure
					&& (target instanceof ControlStructureBrick || target instanceof ScriptBrick)) {
				return false;
			}
		}
		Collections.swap(items, srcPosition, targetPosition);
		return true;
	}

	@Override
	public void moveItemsTo(int position, List<BrickBaseType> itemsToMove) {
		if (itemsToMove.isEmpty()) {
			return;
		}

		BrickBaseType firstBrickInItemsToMove = itemsToMove.get(0);
		if (firstBrickInItemsToMove instanceof ScriptBrick) {

			Script scriptToInsert = ((ScriptBrick) firstBrickInItemsToMove).getScript();
			Script scriptAtPosition = getScriptAtPosition(position - 1);
			scripts.remove(scriptToInsert);

			boolean divideScriptAtPositionAndAddBricksToMovingScript = itemsToMove.size() == 1
					&& !scriptAtPosition.getBrickList().isEmpty();

			if (divideScriptAtPositionAndAddBricksToMovingScript) {
				List<Brick> bricksOfScriptAtPosition = scriptAtPosition.getBrickList();

				int positionToDivideScriptAt = getPositionWithinScript(position, scriptAtPosition);

				for (int i = 0; i < positionToDivideScriptAt; i++) {
					BrickBaseType brick = (BrickBaseType) bricksOfScriptAtPosition.get(i);
					if (brick instanceof ControlStructureBrick) {
						positionToDivideScriptAt = bricksOfScriptAtPosition
								.indexOf(((ControlStructureBrick) brick).getLastBrick()) + 1;
					}
				}

				while (positionToDivideScriptAt < bricksOfScriptAtPosition.size()) {
					scriptToInsert.addBrick(bricksOfScriptAtPosition.get(positionToDivideScriptAt));
					bricksOfScriptAtPosition.remove(positionToDivideScriptAt);
				}
			}

			int whereToInsertScript = getPositionToInsertScript((ScriptBrick) firstBrickInItemsToMove);
			if (whereToInsertScript == scripts.size()) {
				scripts.add(scriptToInsert);
			} else {
				scripts.add(whereToInsertScript, scriptToInsert);
			}
		} else {
			Script srcScript = brickController.getScriptThatContainsBrick(firstBrickInItemsToMove, scripts);
			if (srcScript != null) {
				srcScript.removeBricks(new ArrayList<Brick>(itemsToMove));
			}
			Script targetScript = getScriptAtPosition(position);
			if (targetScript == null) {
				return;
			}
			targetScript.addBricks(getPositionWithinScript(position, targetScript), new ArrayList<Brick>(itemsToMove));
		}
		updateItemsFromCurrentScripts();
	}

	private Script getScriptAtPosition(int position) {
		Script script = scripts.get(0);
		for (BrickBaseType item : items) {
			if (item instanceof ScriptBrick) {
				script = ((ScriptBrick) item).getScript();
			}
			if (items.indexOf(item) >= position) {
				return script;
			}
		}
		return script;
	}

	private int getPositionToInsertScript(ScriptBrick scriptBrick) {
		Script script = null;
		for (BrickBaseType item : items) {
			if (item instanceof ScriptBrick) {
				if (item.equals(scriptBrick)) {
					break;
				} else {
					script = ((ScriptBrick) item).getScript();
				}
			}
		}
		return scripts.indexOf(script) + 1;
	}

	private int getPositionWithinScript(int adapterPosition, @NonNull Script script) {
		int positionOfScriptBrick = items.indexOf(script.getScriptBrick());
		if (positionOfScriptBrick == -1) {
			throw new IllegalArgumentException(script.getClass() + " is not in adapter.");
		}
		if (adapterPosition == positionOfScriptBrick) {
			return 0;
		}
		return adapterPosition - positionOfScriptBrick - 1;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).hashCode();
	}

	public interface SelectionListener {

		void onSelectionChanged(int selectedItemCnt);
	}

	public interface OnItemClickListener {

		void onItemClick(BrickBaseType item, int position);

		boolean onItemLongClick(BrickBaseType item, int position);
	}
}
