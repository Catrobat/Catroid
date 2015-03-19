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

package org.catrobat.catroid.ui.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;

import java.util.ArrayList;

import static org.catrobat.catroid.ui.dialogs.NewSpriteDialog.SpinnerAdapterWrapper;

/**
 * Brick View Factory for PointToBrick.
 * Created by IllyaBoyko on 12/03/15.
 */
public class PointToBrickViewProvider extends BrickViewProvider {
	public PointToBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createPointToBrickView(final PointToBrick brick, ViewGroup parent) {

		View view = inflateBrickView(parent, R.layout.brick_point_to);

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);


		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper() {
			private DataSetObserver currentDataSetObserver;
			private ArrayAdapter<String> spinnerAdapter = getArrayAdapterFromSpriteList(context);
			private boolean isTouchInDropDownView = false;

			@Override
			public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
				currentDataSetObserver = paramDataSetObserver;
				spinnerAdapter.registerDataSetObserver(paramDataSetObserver);
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
				spinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
			}

			@Override
			public int getCount() {
				return spinnerAdapter.getCount();
			}

			@Override
			public Object getItem(int paramInt) {
				return spinnerAdapter.getItem(paramInt);
			}

			@Override
			public long getItemId(int paramInt) {
				String currentSpriteName = spinnerAdapter.getItem(paramInt);
				if (!currentSpriteName.equals(context.getString(R.string.new_broadcast_message))) {
					brick.setOldSelectedObject(currentSpriteName);
				}
				return spinnerAdapter.getItemId(paramInt);
			}

			@Override
			public boolean hasStableIds() {
				return spinnerAdapter.hasStableIds();
			}

			@Override
			public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
				if (isTouchInDropDownView) {
					isTouchInDropDownView = false;
					if (paramInt == 0) {
						showNewSpriteDialog();
					}
				}
				return spinnerAdapter.getView(paramInt, paramView, paramViewGroup);
			}

			@Override
			public int getItemViewType(int paramInt) {
				return spinnerAdapter.getItemViewType(paramInt);
			}

			@Override
			public int getViewTypeCount() {
				return spinnerAdapter.getViewTypeCount();
			}

			@Override
			public boolean isEmpty() {
				return spinnerAdapter.isEmpty();
			}

			@Override
			public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
				spinnerAdapter = getArrayAdapterFromSpriteList(context);
				registerDataSetObserver(currentDataSetObserver);
				View dropDownView = spinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);

				dropDownView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
						isTouchInDropDownView = true;
						return false;
					}
				});

				return dropDownView;
			}

			public ArrayAdapter<String> getAdapter() {
				return spinnerAdapter;
			}

			protected void showNewSpriteDialog() {
				NewSpriteDialog dialog = new NewSpriteDialog(this);
				dialog.show(((ScriptActivity) context).getSupportFragmentManager(), NewSpriteDialog.DIALOG_FRAGMENT_TAG);
			}

			public void refreshSpinnerAfterNewSprite(final Context context, final String newSpriteName) {
				Project project = ProjectManager.getInstance().getCurrentProject();
				for (Sprite sprite : project.getSpriteList()) {
					if (sprite.getName().equals(newSpriteName)) {
						brick.setPointedObject(sprite);
					}
				}

				setSpinnerSelection(spinner, brick);

				AlertDialog dialog = new CustomAlertDialogBuilder(context)
						.setTitle(R.string.dialog_new_object_switch_title)
						.setMessage(R.string.dialog_new_object_switch_message)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								ProjectManager.getInstance().setCurrentSprite(brick.getPointedObject());

								Intent intent = new Intent(context, ProgramMenuActivity.class);
								intent.putExtra(ProgramMenuActivity.FORWARD_TO_SCRIPT_ACTIVITY,
										ScriptActivity.FRAGMENT_SCRIPTS);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								context.startActivity(intent);

								dialog.dismiss();
							}
						}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								spinnerAdapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}

			public void updateSpinner() {
				setSpinnerSelection(spinner, brick);
			}
		};

		spinner.setAdapter(spinnerAdapterWrapper);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();

				if (itemSelected.equals(context.getString(R.string.new_broadcast_message))) {
					brick.setPointedObject(null);
				} else {
					final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
							.getCurrentProject().getSpriteList();

					for (Sprite sprite : spriteList) {
						String spriteName = sprite.getName();
						if (spriteName.equals(itemSelected)) {
							brick.setPointedObject(sprite);
							break;
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(spinner, brick);

		return view;
	}

	private ArrayAdapter<String> getArrayAdapterFromSpriteList(Context context) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.add(context.getString(R.string.new_broadcast_message));

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = ProjectManager.getInstance().getCurrentSprite().getName();
			if (!spriteName.equals(temp) && !spriteName.equals(context.getString(R.string.background))) {
				arrayAdapter.add(sprite.getName());
			}
		}

		return arrayAdapter;
	}

	private void setSpinnerSelection(Spinner spinner, PointToBrick brick) {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		if (spriteList.contains(brick.getPointedObject())) {
			brick.setOldSelectedObject(brick.getPointedObject().getName());
			spinner.setSelection(
					((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(brick.getPointedObject().getName()),
					true);
		} else {
			if (brick.getOldSelectedObject() != null && !brick.getOldSelectedObject().equals("")) {
				spinner.setSelection(
						((SpinnerAdapterWrapper) spinner.getAdapter()).getAdapter().getPosition(brick.getOldSelectedObject()),
						true);
			} else {
				if (spinner.getAdapter().getCount() > 1) {
					spinner.setSelection(1, true);
				} else {
					spinner.setSelection(0, true);
				}
			}
		}
	}


}
