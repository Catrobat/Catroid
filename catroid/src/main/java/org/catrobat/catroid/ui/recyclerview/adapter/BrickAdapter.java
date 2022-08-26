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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.EmptyEventBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ListSelectorBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.ui.dragndrop.BrickAdapterInterface;
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.ViewStateManager;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;

public class BrickAdapter extends BaseAdapter implements
		BrickAdapterInterface,
		AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {

	public static final float DISABLED_BRICK_ALPHA = .8f;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, ALL, SCRIPTS_ONLY, CONNECTED_ONLY})
	@interface CheckBoxMode {}
	public static final int NONE = 0;
	public static final int ALL = 1;
	public static final int SCRIPTS_ONLY = 2;
	public static final int CONNECTED_ONLY = 3;

	@CheckBoxMode
	private int checkBoxMode = NONE;

	private List<Script> scripts = new ArrayList<>();
	private List<Brick> items = new ArrayList<>();
	private int firstConnectedItem = -1;
	private int lastConnectedItem = -1;

	private MultiSelectionManager selectionManager = new MultiSelectionManager();
	private ViewStateManager viewStateManager = new ViewStateManager();

	private OnItemClickListener onItemClickListener;
	private SelectionListener selectionListener;

	private final Sprite sprite;

	public BrickAdapter(Sprite sprite) {
		this.sprite = sprite;
		updateItems(sprite);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setSelectionListener(SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void setCheckBoxMode(int checkBoxMode) {
		this.checkBoxMode = checkBoxMode;
		notifyDataSetChanged();
	}

	public void updateItems(Sprite sprite) {
		scripts = sprite.getScriptList();
		updateItemsFromCurrentScripts();
	}

	private void updateItemsFromCurrentScripts() {
		items.clear();
		sprite.removeAllEmptyScriptBricks();
		for (Script script : scripts) {
			script.setParents();
			script.addToFlatList(items);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		Brick item = items.get(position);

		View itemView = item.getView(parent.getContext());
		itemView.setVisibility(viewStateManager.isVisible(position) ? View.VISIBLE : View.INVISIBLE);
		itemView.setAlpha(viewStateManager.isEnabled(position) ? 1 : DISABLED_BRICK_ALPHA);

		View brickViewContainer = ((ViewGroup) itemView).getChildAt(1);
		if (item instanceof UserDefinedReceiverBrick) {
			brickViewContainer = ((ViewGroup) ((ViewGroup) itemView).getChildAt(1)).getChildAt(0);
		}

		Drawable background = brickViewContainer.getBackground();

		if (item.isCommentedOut() || item instanceof EmptyEventBrick) {
			colorAsCommentedOut(background);
		} else {
			background.clearColorFilter();
		}

		item.getCheckBox().setOnClickListener(view -> onCheckBoxClick(position));

		switch (checkBoxMode) {
			case NONE:
				item.getCheckBox().setVisibility(View.GONE);
				if (item instanceof FormulaBrick) {
					((FormulaBrick) item).setClickListeners();
				} else if (item instanceof ListSelectorBrick) {
					((ListSelectorBrick) item).setClickListeners();
				}
				break;
			case CONNECTED_ONLY:
				if (item instanceof UserDefinedReceiverBrick) {
					viewStateManager.setEnabled(false, position);
					itemView.setAlpha(DISABLED_BRICK_ALPHA);
				}
				item.getCheckBox().setVisibility(View.VISIBLE);
				item.disableSpinners();
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

	public static void colorAsCommentedOut(Drawable background) {
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		background.mutate();
		background.setColorFilter(filter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkBoxMode == NONE) {
			Brick item = items.get(position);
			onItemClickListener.onItemClick(item, position);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkBoxMode == NONE) {
			Brick item = items.get(position);
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
		Brick item = items.get(position);

		List<Brick> flatItems = new ArrayList<>();
		item.addToFlatList(flatItems);

		boolean scriptSelected = item instanceof ScriptBrick;
		int adapterPosition = -1;

		if (selected && noConnectedItemsSelected()) {
			firstConnectedItem = position - 1;
			lastConnectedItem = position + 1;
		}

		for (int i = 0; i < flatItems.size(); i++) {
			adapterPosition = items.indexOf(flatItems.get(i));
			selectionManager.setSelectionTo(selected, adapterPosition);
			if (i > 0) {
				viewStateManager.setEnabled(!selected, adapterPosition);
			}
		}

		if (checkBoxMode == CONNECTED_ONLY) {
			int firstFlatListPosition = items.indexOf(flatItems.get(0));
			updateConnectedItems(position, firstFlatListPosition, adapterPosition, selected, scriptSelected);
		}
	}

	private void updateConnectedItems(int selectedPosition, int firstFlatListPosition, 
			int lastFlatListPosition, boolean selected, boolean scriptSelected) {
		if (selected) {
			if (lastFlatListPosition >= lastConnectedItem) {
				lastConnectedItem = lastFlatListPosition + 1;
			}
			if (firstFlatListPosition <= firstConnectedItem) {
				firstConnectedItem = firstFlatListPosition - 1;
			}
		} else {
			if (selectedPosition == firstConnectedItem + 1) {
				firstConnectedItem = firstFlatListPosition;
			}
			if (selectedPosition == lastConnectedItem - 1) {
				lastConnectedItem = firstFlatListPosition;
			}
			if (selectionManager.getSelectedPositions().isEmpty()) {
				clearConnectedItems();
			}
		}
		for (Brick item : items) {
			int brickPosition = items.indexOf(item);
			viewStateManager.setEnabled(selectableForCopy(brickPosition, scriptSelected), brickPosition);
		}
	}

	private boolean selectableForCopy(int brickPosition, boolean scriptSelected) {
		return noConnectedItemsSelected()
				|| (isItemWithinConnectedRange(brickPosition, scriptSelected)
				&& !isItemOfNewScript(brickPosition, scriptSelected));
	}
	private boolean isItemWithinConnectedRange(int brickPosition, boolean scriptSelected) {
		return ((brickPosition >= firstConnectedItem && brickPosition <= firstConnectedItem + 1)
				|| (brickPosition <= lastConnectedItem && brickPosition >= lastConnectedItem - 1 && !scriptSelected));
	}
	private boolean isItemOfNewScript(int brickPosition, boolean scriptSelected) {
		return (lastConnectedItem == brickPosition && items.get(brickPosition) instanceof ScriptBrick)
				|| (scriptSelected && brickPosition <= firstConnectedItem);
	}
	private boolean noConnectedItemsSelected() {
		return firstConnectedItem == -1 && lastConnectedItem == -1;
	}

	private void clearConnectedItems() {
		firstConnectedItem = -1;
		lastConnectedItem = -1;
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
		clearConnectedItems();
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
		for (int i = 0; i < items.size(); i++) {
			setSelectionTo(items.get(i).isCommentedOut(), i);
		}
		notifyDataSetChanged();
	}

	public void addItem(int position, Brick item) {
		items.add(position, item);
		notifyDataSetChanged();
	}

	@Override
	public Brick getItem(int position) {
		return items.get(position);
	}

	public Brick findByHash(int hashCode) {
		for (Brick item : items) {
			if (item.hashCode() == hashCode) {
				return item;
			}
		}
		return null;
	}

	public List<Brick> getItems() {
		return new ArrayList<>(items);
	}

	@Override
	public boolean removeItems(List<Brick> items) {
		if (this.items.removeAll(items)) {
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	@Override
	public int getPosition(Brick brick) {
		return items.indexOf(brick);
	}

	@Override
	public boolean onItemMove(int sourcePosition, int targetPosition) {
		Brick source = items.get(sourcePosition);
		if (!(source instanceof ScriptBrick) && targetPosition == 0) {
			return false;
		}

		if (source.getAllParts().contains(items.get(targetPosition))) {
			return false;
		}

		Collections.swap(items, sourcePosition, targetPosition);
		return true;
	}

	@Override
	public void moveItemTo(int position, Brick itemToMove) {
		Brick brickAboveTargetPosition = getBrickAbovePosition(position);

		if (itemToMove instanceof ScriptBrick) {
			Script scriptToMove = itemToMove.getScript();
			Script scriptAtTargetPosition = brickAboveTargetPosition.getScript();

			List<Brick> bricksInScriptToMove = scriptToMove.getBrickList();
			List<Brick> bricksInScriptAtTargetPosition = scriptAtTargetPosition.getBrickList();

			boolean divideScriptAtPositionAndAddBricksToMovingScript = bricksInScriptToMove.isEmpty()
					&& !bricksInScriptAtTargetPosition.isEmpty();

			if (divideScriptAtPositionAndAddBricksToMovingScript) {
				int positionToDivideScriptAt = brickAboveTargetPosition.getPositionInScript() + 1;

				List<Brick> bricksToMove = new ArrayList<>();
				for (int i = positionToDivideScriptAt; i < bricksInScriptAtTargetPosition.size(); i++) {
					bricksToMove.add(bricksInScriptAtTargetPosition.get(i));
				}

				bricksInScriptToMove.addAll(bricksToMove);
				bricksInScriptAtTargetPosition.removeAll(bricksToMove);
			}

			scripts.remove(scriptToMove);

			int destinationPosition = scripts.indexOf(scriptAtTargetPosition) + 1;

			if (destinationPosition == scripts.size()) {
				scripts.add(scriptToMove);
			} else {
				scripts.add(destinationPosition, scriptToMove);
			}
		} else {
			for (Script script : scripts) {
				script.removeBrick(itemToMove);
			}

			int destinationPosition = brickAboveTargetPosition.getPositionInDragAndDropTargetList() + 1;

			List<Brick> destinationList = brickAboveTargetPosition.getDragAndDropTargetList();

			if (destinationPosition < destinationList.size()) {
				destinationList.add(destinationPosition, itemToMove);
			} else {
				destinationList.add(itemToMove);
			}
		}
		updateItemsFromCurrentScripts();
	}

	private Brick getBrickAbovePosition(int position) {
		if (position > 0) {
			position--;
		}
		return items.get(position);
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

		void onItemClick(Brick item, int position);

		boolean onItemLongClick(Brick item, int position);
	}
}
