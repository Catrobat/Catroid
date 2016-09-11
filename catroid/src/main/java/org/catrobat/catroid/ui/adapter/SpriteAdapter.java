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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SpriteAdapter extends BaseExpandableListAdapter implements ActionModeActivityAdapterInterface {

	private static final int INVALID_ID_OR_POSITION = -1;

	private static LayoutInflater inflater = null;
	private Context context;
	private int selectMode;
	private boolean showDetails;
	private Set<Integer> checkedSprites = new TreeSet<>();
	private OnSpriteEditListener onSpriteEditListener;
	private SpritesListFragment spritesListFragment;

	private Map<String, Integer> idMap = new LinkedHashMap<>();
	private List<Sprite> spriteList = null;

	public SpriteAdapter(Context context, List<Sprite> objects) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		selectMode = ListView.CHOICE_MODE_NONE;
		showDetails = false;
		spriteList = objects;

		for (int i = 0; i < objects.size(); ++i) {
			idMap.put(objects.get(i).getName(), i);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (spriteList.size() != idMap.size()) {
			rebuildIdMap();
		}
	}

	private void rebuildIdMap() {
		idMap.clear();
		for (int i = 0; i < spriteList.size(); i++) {
			idMap.put(spriteList.get(i).getName(), i);
		}
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View spriteView = convertView;
		ViewHolder holder;

		if (convertView == null) {
			spriteView = inflater.inflate(R.layout.activity_project_spritelist_item, parent, false);
			holder = getHolderViews(spriteView);
			setOnTouchListener(holder);
			spriteView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		setVisibility(spriteView, groupPosition, 0);

		if (isGroupSpritePosition(groupPosition)) {
			setParentHeight(holder, true);
			setGroupTitle(holder, groupPosition);

			setViewsForExpandedAndCollapsedState(holder, isExpanded);
			setBackgroundResourcesForGroup(holder);
			setCheckboxListener(holder, false, false, true);
			setGroupOnClickListener(holder);
			handleCheckedSprites(groupPosition, 0, holder);
			resetNonGroupViews(holder);

			int childrenCount = getChildrenCountOfGroup(groupPosition);
			holder.scripts.setText(context.getResources().getString(R.string.number_of_objects).concat(" ").concat(Integer.toString(childrenCount)));

			setGroupSpriteExpandedState(groupPosition, isExpanded);
		} else {
			setParentHeight(holder, false);
			handleHolderViews(groupPosition, 0, holder);
			resetGroupViews(holder);

			if (groupPosition == 0 && !spritesListFragment.isBackPackActionMode()) {
				setBackgroundViewsAndListener(holder);
			} else {
				setSpriteViewsAndListener(holder, groupPosition, 0);
				setOnSpriteEditListenerForGroup(holder, groupPosition);
			}
		}

		return spriteView;
	}

	private void setGroupSpriteExpandedState(int groupPosition, boolean expanded) {
		Sprite sprite = (Sprite) getGroup(groupPosition);
		if (sprite != null && sprite instanceof GroupSprite) {
			((GroupSprite) sprite).setExpanded(expanded);
		}
	}

	private void setParentHeight(ViewHolder holder, boolean isGroup) {
		Resources resources = spritesListFragment.getActivity().getResources();
		float height = isGroup ? resources.getDimension(R.dimen.spritelist_group_item_height)
				: resources.getDimension(R.dimen.activity_linear_layout_height);

		holder.spritelistParent.getLayoutParams().height = (int) height;
		holder.spritelistParent.requestLayout();
	}

	private void setVisibility(View spriteView, int groupPosition, int childPosition) {
		int flatPosition = getFlatPositionByGroupAndChildPosition(groupPosition, childPosition);
		Sprite sprite = spriteList.get(flatPosition);
		if (sprite.isMobile()) {
			spriteView.setVisibility(View.INVISIBLE);
		} else {
			spriteView.setVisibility(View.VISIBLE);
		}
	}

	private void setGroupOnClickListener(final ViewHolder holder) {

		holder.groupBackground.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (selectMode != ListView.CHOICE_MODE_NONE && isCheckboxEnabled(false, false) && event.getAction()
						== MotionEvent.ACTION_DOWN) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
				return false;
			}
		});
	}

	private void setOnSpriteEditListenerForGroup(final ViewHolder holder, final int groupPosition) {
		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Sprite sprite = (Sprite) getGroup(groupPosition);
				int flatPosition = spriteList.indexOf(sprite);
				if (selectMode == ListView.CHOICE_MODE_NONE) {
					if (onSpriteEditListener != null) {
						onSpriteEditListener.onSpriteEdit(groupPosition);
					}
				} else if (spritesListFragment.shouldSpriteBeChecked(flatPosition)) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		});
	}

	private void setBackgroundResourcesForGroup(ViewHolder holder) {
		if (selectMode == ListView.CHOICE_MODE_NONE) {
			holder.groupBackground.setBackgroundResource(R.drawable.button_background_selector);
		} else {
			holder.groupBackground.setBackgroundResource(R.drawable.button_background_shadowed);
		}
	}

	private void resetGroupViews(ViewHolder holder) {
		holder.indicator.setVisibility(View.GONE);
		holder.indicator.setImageDrawable(null);
		holder.groupBackground.setVisibility(View.GONE);
		holder.groupText.setVisibility(View.GONE);

		holder.background.setVisibility(View.VISIBLE);
		holder.text.setVisibility(View.VISIBLE);
		holder.image.setVisibility(View.VISIBLE);
	}

	private void resetNonGroupViews(ViewHolder holder) {
		holder.details.setVisibility(View.GONE);
		holder.backgroundHeadline.setVisibility(View.GONE);
		holder.objectsHeadline.setVisibility(View.GONE);
		holder.background.setVisibility(View.GONE);
		holder.image.setVisibility(View.GONE);
		holder.text.setVisibility(View.GONE);

		holder.groupBackground.setVisibility(View.VISIBLE);
		holder.groupText.setVisibility(View.VISIBLE);
	}

	private void setViewsForExpandedAndCollapsedState(ViewHolder holder, boolean isExpanded) {
		Resources resources = spritesListFragment.getActivity().getResources();
		holder.indicator.setVisibility(View.VISIBLE);
		Drawable drawable = isExpanded ? resources.getDrawable(R.drawable.ic_play_down)
				: resources.getDrawable(R.drawable.ic_play);
		holder.indicator.setImageDrawable(drawable);
	}

	private void setBackgroundViewsAndListener(ViewHolder holder) {
		holder.backgroundHeadline.setVisibility(View.VISIBLE);
		holder.objectsHeadline.setVisibility(View.VISIBLE);
		holder.checkbox.setVisibility(View.GONE);

		if (selectMode == ListView.CHOICE_MODE_NONE) {
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
		} else {
			holder.background.setBackgroundResource(R.drawable.button_background);
		}

		holder.background.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				openBackPackMenuFromBackground();
				return selectMode != ListView.CHOICE_MODE_NONE;
			}
		});

		holder.background.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (selectMode == ListView.CHOICE_MODE_NONE && onSpriteEditListener != null) {
					onSpriteEditListener.onSpriteEdit(0);
				}
			}
		});
	}

	private void setSpriteViewsAndListener(final ViewHolder holder, final int groupPosition, final int childPosition) {
		setCheckboxListener(holder, true, true, false);
		holder.backgroundHeadline.setVisibility(View.GONE);
		holder.objectsHeadline.setVisibility(View.GONE);

		holder.background.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return selectMode != ListView.CHOICE_MODE_NONE || (groupPosition == 0 && childPosition == 0);
			}
		});
	}

	public void setCheckboxListener(ViewHolder holder, boolean enableCheckboxesForCopyMode, boolean
			enableCheckboxesForBackpackMode, boolean forGroup) {
		RelativeLayout layout = forGroup ? holder.groupBackground : holder.background;
		if (selectMode == ListView.CHOICE_MODE_NONE) {
			layout.setBackgroundResource(R.drawable.button_background_selector);
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			clearCheckedItems();
		} else if (isCheckboxEnabled(enableCheckboxesForCopyMode, enableCheckboxesForBackpackMode)) {
			holder.checkbox.setVisibility(View.VISIBLE);
			layout.setBackgroundResource(R.drawable.button_background_shadowed);
		}
	}

	private boolean isCheckboxEnabled(boolean enableCheckboxesForCopyMode, boolean enableCheckboxesForBackpackMode) {
		boolean isCopyActionMode = spritesListFragment.isCopyActionMode();
		boolean isBackpackActionMode = spritesListFragment.isBackPackActionMode();
		boolean isOtherActionMode = !(isCopyActionMode || isBackpackActionMode);
		return isBackpackActionMode && enableCheckboxesForBackpackMode || isCopyActionMode && enableCheckboxesForCopyMode || isOtherActionMode;
	}

	private void setOnTouchListener(ViewHolder holder) {
		holder.background.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(ScriptActivity.ACTION_SPRITE_TOUCH_ACTION_UP);
					context.sendBroadcast(intent);
				}
				return false;
			}
		});
	}

	private void setGroupTitle(ViewHolder holder, int groupPosition) {
		String groupName = ((Sprite) getGroup(groupPosition)).getName();
		holder.groupText.setText(groupName);
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View spriteView = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			spriteView = inflater.inflate(R.layout.activity_project_spritelist_item, parent, false);
			holder = getHolderViews(spriteView);
			setOnTouchListener(holder);
			spriteView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		setVisibility(spriteView, groupPosition, childPosition + 1);

		handleHolderViews(groupPosition, childPosition + 1, holder);
		setSpriteViewsAndListener(holder, groupPosition, childPosition + 1);
		setOnSpriteEditListenerForChild(holder, groupPosition, childPosition);
		indentViews(holder);

		return spriteView;
	}

	private void setOnSpriteEditListenerForChild(final ViewHolder holder, final int groupPosition, final int childPosition) {
		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Sprite sprite = (Sprite) getGroup(groupPosition);
				int flatPosition = spriteList.indexOf(sprite) + childPosition + 1;

				if (selectMode == ListView.CHOICE_MODE_NONE) {
					if (onSpriteEditListener != null) {
						onSpriteEditListener.onSpriteEdit(groupPosition, childPosition);
					} else if (spritesListFragment.shouldSpriteBeChecked(flatPosition)) {
						holder.checkbox.setChecked(!holder.checkbox.isChecked());
					}
				} else if (spritesListFragment.shouldSpriteBeChecked(flatPosition)) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		});
	}

	private void indentViews(ViewHolder holder) {
		if (holder.checkbox.getVisibility() != View.VISIBLE) {
			holder.checkbox.setVisibility(View.INVISIBLE);
		}
	}

	private ViewHolder getHolderViews(View spriteView) {
		ViewHolder holder = new ViewHolder();
		holder.spritelistParent = (LinearLayout) spriteView.findViewById(R.id.spritelist_parent);
		holder.background = (RelativeLayout) spriteView.findViewById(R.id.spritelist_item_background);
		holder.groupBackground = (RelativeLayout) spriteView.findViewById(R.id.spritelist_group_item_background);
		holder.checkbox = (CheckBox) spriteView.findViewById(R.id.sprite_checkbox);
		holder.text = (TextView) spriteView.findViewById(R.id.project_activity_sprite_title);
		holder.groupText = (TextView) spriteView.findViewById(R.id.project_activity_group_sprite_title);
		holder.backgroundHeadline = (LinearLayout) spriteView.findViewById(R.id.spritelist_background_headline);
		holder.objectsHeadline = (LinearLayout) spriteView.findViewById(R.id.spritelist_objects_headline);
		holder.image = (ImageView) spriteView.findViewById(R.id.sprite_img);
		holder.indicator = (ImageView) spriteView.findViewById(R.id.sprite_group_indicator);
		holder.scripts = (TextView) spriteView.findViewById(R.id.textView_number_of_scripts);
		holder.bricks = (TextView) spriteView.findViewById(R.id.textView_number_of_bricks);
		holder.looks = (TextView) spriteView.findViewById(R.id.textView_number_of_looks);
		holder.sounds = (TextView) spriteView.findViewById(R.id.textView_number_of_sounds);
		holder.details = spriteView.findViewById(R.id.project_activity_sprite_details);
		return holder;
	}

	private void openBackPackMenuFromBackground() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		CharSequence[] items = new CharSequence[] { context.getString(R.string.backpack_add) };
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					BackPackSpriteController.getInstance().backpackVisibleSprite((Sprite) getChild(0, 0));
					spritesListFragment.switchToBackPack();
				}
				dialog.dismiss();
			}
		});
		String title = context.getString(R.string.background);
		if (ProjectManager.getInstance().getCurrentSprite() != null) {
			title = ProjectManager.getInstance().getCurrentSprite().getName();
		}
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.show();
	}

	public void setSpritesListFragment(SpritesListFragment spritesListFragment) {
		this.spritesListFragment = spritesListFragment;
	}

	public void setOnSpriteEditListener(OnSpriteEditListener listener) {
		onSpriteEditListener = listener;
	}

	public void addCheckedSprite(int position) {
		checkedSprites.add(position);
	}

	public int getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedSprites.size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return checkedSprites;
	}

	@Override
	public void clearCheckedItems() {
		checkedSprites.clear();
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public void handleHolderViews(int groupPosition, final int childPosition, ViewHolder holder) {
		handleCheckedSprites(groupPosition, childPosition, holder);

		Sprite sprite = (Sprite) getChild(groupPosition, childPosition - 1);

		holder.text.setText(sprite.getName());
		setImage(holder, sprite);

		holder.scripts.setText(context.getResources().getString(R.string.number_of_scripts).concat(" ").concat(Integer.toString(sprite.getNumberOfScripts())));

		holder.bricks.setText(context.getResources().getString(R.string.number_of_bricks).concat(" ").concat(Integer.toString(sprite.getNumberOfScriptsAndBricks())));

		holder.looks.setText(context.getResources().getString(R.string.number_of_looks).concat(" ").concat(Integer.toString(sprite.getLookDataList().size())));

		holder.sounds.setText(context.getResources().getString(R.string.number_of_sounds).concat(" ").concat(Integer.toString(sprite.getSoundList().size())));

		setDetailsVisibility(holder);
	}

	private void handleCheckedSprites(int groupPosition, int childPosition, ViewHolder holder) {
		final int flatPosition = getFlatPositionByGroupAndChildPosition(groupPosition, childPosition);

		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedItems();
					}
					checkedSprites.add(flatPosition);
				} else {
					checkedSprites.remove(flatPosition);
				}
				notifyDataSetChanged();

				if (onSpriteEditListener != null) {
					onSpriteEditListener.onSpriteChecked();
				}
			}
		});

		if (checkedSprites.contains(flatPosition)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
	}

	private void setDetailsVisibility(ViewHolder holder) {
		if (!showDetails) {
			holder.details.setVisibility(View.GONE);
		} else {
			holder.details.setVisibility(View.VISIBLE);
		}
	}

	protected void setImage(ViewHolder holder, Sprite sprite) {
		LookData firstLookData = null;
		if (sprite.getLookDataList().size() > 0) {
			firstLookData = sprite.getLookDataList().get(0);
		}
		if (firstLookData == null) {
			holder.image.setImageBitmap(null);
		} else {
			holder.image.setImageBitmap(firstLookData.getThumbnailBitmap());
		}
	}

	@Override
	public int getGroupCount() {
		int groupCount = 0;
		for (Sprite sprite : spriteList) {
			if (sprite instanceof GroupSprite || sprite instanceof SingleSprite) {
				groupCount++;
			}
		}
		return groupCount;
	}

	public int getChildrenCount(Sprite groupSprite) {
		int groupSpriteIndex = spriteList.indexOf(groupSprite);
		return getChildCountWithGroupSpriteIndex(groupSpriteIndex);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (!isGroupSpritePosition(groupPosition)) {
			return 0;
		}
		return getChildrenCountOfGroup(groupPosition);
	}

	public int getChildrenCountOfGroup(int groupPosition) {
		Sprite sprite = (Sprite) getGroup(groupPosition);
		int groupSpriteIndex = spriteList.indexOf(sprite);
		return getChildCountWithGroupSpriteIndex(groupSpriteIndex);
	}

	public List<Sprite> getChildrenOfGroup(Sprite groupSprite) {
		int groupSpriteIndex = spriteList.indexOf(groupSprite);
		List<Sprite> childrenList = new ArrayList<>();
		for (int position = groupSpriteIndex + 1; position < groupSpriteIndex + 1 + getChildrenCount(groupSprite);
				position++) {
			childrenList.add(spriteList.get(position));
		}
		return childrenList;
	}

	@Override
	public Object getGroup(int groupPosition) {
		int count = 0;
		for (Sprite sprite : spriteList) {
			if ((sprite instanceof GroupSprite || sprite instanceof SingleSprite) && count == groupPosition) {
				return sprite;
			}
			if (!(sprite instanceof GroupItemSprite)) {
				count++;
			}
		}
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Sprite sprite = (Sprite) getGroup(groupPosition);
		int groupSpriteIndex = spriteList.indexOf(sprite);
		return spriteList.get(groupSpriteIndex + childPosition + 1);
	}

	@Override
	public long getGroupId(int groupPosition) {
		if (groupPosition < 0 || groupPosition >= idMap.size()) {
			return INVALID_ID_OR_POSITION;
		}
		Sprite item = (Sprite) getGroup(groupPosition);
		if (item == null || !idMap.containsKey(item.getName())) {
			return INVALID_ID_OR_POSITION;
		}
		return idMap.get(item.getName());
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		if (groupPosition < 0 || (groupPosition + childPosition) >= idMap.size()) {
			return INVALID_ID_OR_POSITION;
		}
		Sprite item = (Sprite) getChild(groupPosition, childPosition);
		if (item == null || !idMap.containsKey(item.getName())) {
			return INVALID_ID_OR_POSITION;
		}
		return idMap.get(item.getName());
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public long getGroupOrChildId(int flatPosition) {
		if (flatPosition >= spriteList.size() || flatPosition < 0) {
			return INVALID_ID_OR_POSITION;
		}

		Sprite spriteAtPosition = spriteList.get(flatPosition);
		if (spriteAtPosition instanceof GroupItemSprite) {
			Sprite groupSprite = getParentGroupSpriteOfGroupItemSprite(spriteAtPosition);
			int groupPosition = getGroupPositionByGroupSprite(groupSprite, true);
			int childPosition = spriteList.indexOf(spriteAtPosition) - spriteList.indexOf(groupSprite) - 1;
			return getChildId(groupPosition, childPosition);
		} else {
			int groupPosition = getGroupPositionByGroupSprite(spriteList.get(flatPosition), true);
			return getGroupId(groupPosition);
		}
	}

	public int getNumberOfGroups() {
		return getGroupNames().size();
	}

	public boolean isGroupSpritePosition(int groupPosition) {
		return getGroup(groupPosition) instanceof GroupSprite;
	}

	public boolean isGroupPosition(int flatPosition) {
		return spriteList.get(flatPosition) instanceof GroupSprite;
	}

	public int getChildCountWithGroupSpriteIndex(int groupSpriteIndex) {
		int childCount = 0;
		for (int childPosition = groupSpriteIndex + 1; childPosition < spriteList.size(); childPosition++) {
			if (spriteList.get(childPosition) instanceof GroupItemSprite) {
				childCount++;
			} else {
				break;
			}
		}
		return childCount;
	}

	public List<String> getGroupNames() {
		List<String> groupNames = new ArrayList<>();
		for (Sprite sprite : spriteList) {
			if (sprite instanceof GroupSprite) {
				groupNames.add(sprite.getName());
			}
		}
		return groupNames;
	}

	public List<Integer> getGroupSpritePositions() {
		List<Integer> groupSpritePositions = new ArrayList<>();
		int groupCount = 0;

		for (int position = 0; position < spriteList.size(); position++) {
			Sprite sprite = spriteList.get(position);
			if (sprite instanceof GroupSprite) {
				groupSpritePositions.add(groupCount);
			}
			if (sprite instanceof GroupSprite || sprite instanceof SingleSprite) {
				groupCount++;
			}
		}
		return groupSpritePositions;
	}

	public List<String> getNonGroupNames() {
		List<String> nonGroupNames = new ArrayList<>();
		for (Sprite sprite : spriteList) {
			if (sprite instanceof GroupItemSprite || sprite instanceof SingleSprite) {
				nonGroupNames.add(sprite.getName());
			}
		}
		return nonGroupNames;
	}

	public int getFlatPositionByGroupAndChildPosition(int groupPosition, int childPosition) {
		Sprite sprite = (Sprite) getGroup(groupPosition);
		int groupSpriteIndex = spriteList.indexOf(sprite);
		return groupSpriteIndex + childPosition;
	}

	public int getGroupOrChildPositionByFlatPosition(int flatPosition) {
		long packedPosition = spritesListFragment.getListView().getExpandableListPosition(flatPosition);

		int itemType = ExpandableListView.getPackedPositionType(packedPosition);
		int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
		int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

		if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			return groupPosition;
		} else {
			return childPosition;
		}
	}

	private Sprite getParentGroupSpriteOfGroupItemSprite(Sprite groupItemSprite) {
		for (int position = spriteList.indexOf(groupItemSprite); position >= 0; position--) {
			Sprite currentSprite = spriteList.get(position);
			if (currentSprite instanceof GroupSprite) {
				return currentSprite;
			}
		}
		return null;
	}

	public void replaceItemInIdMap(String oldName, String newName) {
		int id = idMap.get(oldName);
		idMap.remove(oldName);
		idMap.put(newName, id);
	}

	public int getAdapterPositionForVisibleListViewPosition(int listViewPosition) {
		int currentVisiblePosition = 0;

		for (Sprite sprite : spriteList) {
			if (listViewPosition == currentVisiblePosition && isVisibleListViewItem(sprite)) {
				return spriteList.indexOf(sprite);
			}
			if (isVisibleListViewItem(sprite)) {
				currentVisiblePosition++;
			}
		}
		return INVALID_ID_OR_POSITION;
	}

	private boolean isVisibleListViewItem(Sprite sprite) {
		if (sprite instanceof SingleSprite || sprite instanceof GroupSprite || sprite.isMobile()) {
			return true;
		}

		ExpandableListView listView = spritesListFragment.getListView();
		Sprite groupSprite = getParentGroupSpriteOfGroupItemSprite(sprite);
		int groupPosition = getGroupPositionByGroupSprite(groupSprite, true);
		return listView.isGroupExpanded(groupPosition);
	}

	private int getGroupPositionByGroupSprite(Sprite sprite, boolean includingSingleSpritePositions) {
		int groupPosition = 0;
		for (Sprite currentSprite : spriteList) {
			if (sprite.equals(currentSprite)) {
				break;
			}
			if (currentSprite instanceof GroupSprite) {
				groupPosition++;
			} else if (includingSingleSpritePositions && currentSprite instanceof SingleSprite) {
				groupPosition++;
			}
		}
		return groupPosition;
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}

	public void setExpandedIndicatorsForAllGroupSprites(boolean expanded) {
		for (Sprite currentSprite : spriteList) {
			if (currentSprite instanceof GroupSprite) {
				((GroupSprite) currentSprite).setExpanded(expanded);
			}
		}
	}

	public interface OnSpriteEditListener {
		void onSpriteChecked();

		void onSpriteEdit(int groupPosition, int childPosition);

		void onSpriteEdit(int groupPosition);
	}

	protected static class ViewHolder {
		protected LinearLayout spritelistParent;
		protected RelativeLayout background;
		protected RelativeLayout groupBackground;
		protected CheckBox checkbox;
		protected TextView text;
		protected TextView groupText;
		protected LinearLayout backgroundHeadline;
		protected LinearLayout objectsHeadline;
		protected ImageView image;
		protected ImageView indicator;
		protected TextView scripts;
		protected TextView bricks;
		protected TextView looks;
		protected TextView sounds;
		protected View details;
	}
}
