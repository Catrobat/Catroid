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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserBrickParameter;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.BrickLayout;

/**
 * UseBrick View Factory.
 * Created by Illya Boyko on 16/03/15.
 */
public class UserBrickViewProvider extends BrickViewProvider {
	public UserBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createUserBrickView(final UserBrick brick, ViewGroup parent) {

		final View view = inflateBrickView(parent, R.layout.brick_user);


		if (brick.getLastDataVersion() < brick.getUserScriptDefinitionBrickElements().getVersion() || brick.getUserBrickParameters() == null) {
			brick.updateUserBrickParameters(null);
		}
		boolean prototype = isPrototypeLayout();

		Context context = view.getContext();

		BrickLayout layout = (BrickLayout) view.findViewById(R.id.brick_user_flow_layout);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		int id = 0;
		for (final UserBrickParameter parameter : brick.getUserBrickParameters()) {
			TextView currentTextView;
			final UserScriptDefinitionBrickElement uiData = getUserScriptDefinitionBrickElement(brick, parameter);
			if (uiData.isEditModeLineBreak) {
				continue;
			}
			if (uiData.isVariable) {
				currentTextView = new EditText(context);

				currentTextView.setTextAppearance(context, R.style.BrickEditText);
				if (prototype) {
					try {
						currentTextView.setText(String
								.valueOf(parameter.getFormulaWithBrickField(Brick.BrickField.USER_BRICK).interpretInteger(ProjectManager
										.getInstance().getCurrentSprite())));
					} catch (InterpretationException interpretationException) {
						Log.e(TAG, "InterpretationException!", interpretationException);
					}
				} else {
					currentTextView.setId(id);

					parameter.getFormulaWithBrickField(Brick.BrickField.USER_BRICK).setTextFieldId(currentTextView.getId());
					String formulaString = parameter.getFormulaWithBrickField(Brick.BrickField.USER_BRICK).getDisplayString(currentTextView.getContext());
					parameter.getFormulaWithBrickField(Brick.BrickField.USER_BRICK).refreshTextField(currentTextView, formulaString);

					// This stuff isn't being included by the style when I use setTextAppearance.
					currentTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View eventSource) {
							if (clickAllowed(view)) {

//								for (UserBrickParameter userBrickParameter : brick.getUserBrickParameters()) {
//									UserScriptDefinitionBrickElement userBrickElement = getUserScriptDefinitionBrickElement(brick, userBrickParameter);
//
//									if (userBrickElement.isVariable && userBrickParameter.textView.getId() == eventSource.getId()) {

								onClickDispatcher.dispatch(brick, view, parameter.getFormulaWithBrickField(Brick.BrickField.USER_BRICK));
//									}
//								}
							}
						}
					});
				}
				currentTextView.setVisibility(View.VISIBLE);
			} else {
				currentTextView = new TextView(context);
				currentTextView.setTextAppearance(context, R.style.BrickText_Multiple);

				currentTextView.setText(uiData.name);
			}

			// This stuff isn't being included by the style when I use setTextAppearance.
			if (prototype) {
				currentTextView.setFocusable(false);
				currentTextView.setFocusableInTouchMode(false);
				currentTextView.setClickable(false);
			}

			layout.addView(currentTextView);

			if (uiData.newLineHint) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) currentTextView.getLayoutParams();
				params.setNewLine(true);
				currentTextView.setLayoutParams(params);
			}

			id++;
		}

		return view;
	}

	private static UserScriptDefinitionBrickElement getUserScriptDefinitionBrickElement(UserBrick brick, UserBrickParameter parameter) {
		return brick.getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(parameter.parameterIndex);
	}
}
