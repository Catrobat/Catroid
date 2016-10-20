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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.BackPackSceneController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SceneAdapter extends ArrayAdapter<Scene> implements ActionModeActivityAdapterInterface {
	private static final int INVALID_ID = -1;
	private HashMap<Scene, Integer> idMap = new HashMap<>();
	protected int selectMode;
	protected Set<Integer> checkedScenes = new TreeSet<Integer>();
	protected OnSceneEditListener onSceneEditListener;

	protected static class ViewHolder {
		protected RelativeLayout background;
		protected CheckBox checkbox;
		protected TextView sceneName;
		protected ImageView image;
	}

	protected static LayoutInflater inflater;
	protected ProjectAndSceneScreenshotLoader screenshotLoader;

	public SceneAdapter(Context context, int resource, int textViewResourceId, List<Scene> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screenshotLoader = new ProjectAndSceneScreenshotLoader(context);
		selectMode = ListView.CHOICE_MODE_NONE;
		for (int i = 0; i < objects.size(); ++i) {
			idMap.put(objects.get(i), i);
		}
	}

	public void setOnSceneEditListener(OnSceneEditListener listener) {
		onSceneEditListener = listener;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
	}

	@Override
	public boolean getShowDetails() {
		return false;
	}

	public Set<Integer> getCheckedScenes() {
		return checkedScenes;
	}

	public int getAmountOfCheckedScenes() {
		return checkedScenes.size();
	}

	public void addCheckedScene(int position) {
		checkedScenes.add(position);
	}

	public void clearCheckedScenes() {
		checkedScenes.clear();
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= idMap.size()) {
			return INVALID_ID;
		}
		Scene item = getItem(position);
		return idMap.get(item);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (getCount() != idMap.size()) {
			idMap.clear();
			for (int i = 0; i < getCount(); i++) {
				idMap.put(getItem(i), i);
			}
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View sceneView = convertView;
		final ViewHolder holder;
		if (sceneView == null) {
			sceneView = inflater.inflate(R.layout.activity_scenes_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) sceneView.findViewById(R.id.list_item_layout);
			holder.checkbox = (CheckBox) sceneView.findViewById(R.id.checkbox);
			holder.sceneName = (TextView) sceneView.findViewById(R.id.list_item_text_view);
			//holder.image = (ImageView) sceneView.findViewById(R.id.activity_scenes_list_item_image_view);
			sceneView.setTag(holder);
		} else {
			holder = (ViewHolder) sceneView.getTag();
		}

		// ------------------------------------------------------------
		final Scene scene = getItem(position);
		String sceneName = scene.getName();
		String displayName = position == 0 && !scene.isBackPackScene ? String.format(getContext().getString(R.string
				.start_scene_name), sceneName) : sceneName;
		String projectName = null;
		if (scene.getProject() != null) {
			projectName = scene.getProject().getName();
		}

		//set name of scene:
		holder.sceneName.setText(displayName);

		screenshotLoader.loadAndShowScreenshot(projectName, sceneName, scene.isBackPackScene, holder.image);

		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedScenes();
					}
					checkedScenes.add(position);
				} else {
					checkedScenes.remove(position);
				}
				notifyDataSetChanged();

				/*if (onSceneEditListener != null) {
					onSceneEditListener.onSceneChecked();
				}*/
			}
		});

		holder.background.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
		/*		if (scene.isBackPackScene && onSceneEditListener != null) {
					return false;
				}
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					return true;
				}*/
				return false;
			}
		});

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
/*				if (selectMode != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				} else if (onSceneEditListener != null) {
					onSceneEditListener.onSceneEdit(position, view);
				}*/
			}
		});

		holder.background.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(ScriptActivity.ACTION_SCENE_TOUCH_ACTION_UP);
					getContext().sendBroadcast(intent);
				}
				return false;
			}
		});

		if (checkedScenes.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
			clearCheckedScenes();
		}

		return sceneView;
	}

	public boolean onDestroyActionModeUnpacking() {
		List<Scene> scenesToUnpack = new ArrayList<>();
		for (Integer checkedPosition : checkedScenes) {
			scenesToUnpack.add(getItem(checkedPosition));
		}
		boolean success = (BackPackSceneController.getInstance().unpackScenes(scenesToUnpack) != null);

		return success;
	}

	public void returnToProjectActivity() {
		Intent intent = new Intent(getContext(), ProjectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getContext().startActivity(intent);
	}

	public int getAmountOfCheckedItems() {
		return checkedScenes.size();
	}

	public Set<Integer> getCheckedItems() {
		return checkedScenes;
	}

	public void clearCheckedItems() {
		checkedScenes.clear();
	}

	public interface OnSceneEditListener {
		void onSceneChecked();

		void onSceneEdit(int position, View view);
	}
}
