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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.UserBrickElementEditorFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UserScriptDefinitionBrick extends BrickBaseType implements ScriptBrick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private static final String TAG = UserScriptDefinitionBrick.class.getSimpleName();
	private static final String LINE_BREAK = "linebreak";

	private StartScript script;

	@XStreamAlias("userBrickElements")
	private List<UserScriptDefinitionBrickElement> userScriptDefinitionBrickElements;

	public UserScriptDefinitionBrick() {
		this.script = new StartScript(true);
		this.userScriptDefinitionBrickElements = new ArrayList<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserScriptDefinitionBrick)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		UserScriptDefinitionBrick definitionBrick = (UserScriptDefinitionBrick) obj;
		List<UserScriptDefinitionBrickElement> elements = definitionBrick.getUserScriptDefinitionBrickElements();

		if (userScriptDefinitionBrickElements.size() != elements.size()) {
			return false;
		}

		for (int elementPosition = 0; elementPosition < userScriptDefinitionBrickElements.size(); elementPosition++) {
			UserScriptDefinitionBrickElement elementToCompare = elements.get(elementPosition);
			UserScriptDefinitionBrickElement element = userScriptDefinitionBrickElements.get(elementPosition);
			if (!(elementToCompare.equals(element))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * TAG.hashCode();
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Brick brick : script.getBrickList()) {
			if (brick instanceof UserBrick && ((UserBrick) brick).getDefinitionBrick() == this) {
				continue;
			}
			resources |= brick.getRequiredResources();
		}
		return resources;
	}

	public void appendBrickToScript(Brick brick) {
		this.getScriptSafe().addBrick(brick);
	}

	@Override
	public CheckBox getCheckBox() {
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		UserScriptDefinitionBrick clonedBrick = new UserScriptDefinitionBrick();
		clonedBrick.userScriptDefinitionBrickElements = cloneDefinitionBrickElements();
		for (Brick brick : this.script.getBrickList()) {
			clonedBrick.script.addBrick(brick.copyBrickForSprite(sprite));
		}

		return clonedBrick;
	}

	public List<UserScriptDefinitionBrickElement> cloneDefinitionBrickElements() {
		List<UserScriptDefinitionBrickElement> cloneList = new ArrayList<>();
		for (UserScriptDefinitionBrickElement originalUserBrickElement : userScriptDefinitionBrickElements) {
			UserScriptDefinitionBrickElement clonedUserBrickElement = new UserScriptDefinitionBrickElement();
			clonedUserBrickElement.setText(originalUserBrickElement.getText());
			clonedUserBrickElement.setElementType(originalUserBrickElement.getElementType());
			clonedUserBrickElement.setNewLineHint(originalUserBrickElement.isNewLineHint());
			cloneList.add(clonedUserBrickElement);
		}
		return cloneList;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_user_definition, null);
		setCheckboxView(R.id.brick_user_definition_checkbox);
		onLayoutChanged();

		return view;
	}

	public void onLayoutChanged() {
		Context context = view.getContext();

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_user_definition_layout);
		layout.setFocusable(false);
		layout.setFocusableInTouchMode(false);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		View userBrickPrototype = getUserBrickPrototypeView(context);
		Bitmap brickImage = getBrickImage(userBrickPrototype);

		ImageView preview = getBorderedPreview(brickImage);

		TextView define = new TextView(context);
		define.setTextAppearance(context, R.style.BrickText);
		define.setText(context.getString(R.string.define).concat(" "));

		layout.addView(define);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) define.getLayoutParams();
		params.gravity = Gravity.CENTER_VERTICAL;

		// This stuff isn't being included by the style when I use setTextAppearance.
		define.setFocusable(false);
		define.setFocusableInTouchMode(false);
		define.setClickable(true);

		layout.setFocusable(false);
		layout.setFocusableInTouchMode(false);
		layout.setClickable(true);
		preview.setClickable(true);
		preview.setOnClickListener(this);
		layout.setOnClickListener(this);
		define.setOnClickListener(this);

		layout.addView(preview);
	}

	private View getUserBrickPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_user, null);
		BrickLayout layout = (BrickLayout) prototypeView.findViewById(R.id.brick_user_flow_layout);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		for (UserScriptDefinitionBrickElement element : getUserScriptDefinitionBrickElements()) {
			TextView currentTextView;
			if (element.isLineBreak()) {
				continue;
			} else if (element.isVariable()) {
				currentTextView = new EditText(context);
				currentTextView.setTextAppearance(context, R.style.BrickPrototypeTextView);
				currentTextView.setText(String.valueOf(0d));
				currentTextView.setVisibility(View.VISIBLE);
			} else {
				currentTextView = new TextView(context);
				currentTextView.setTextAppearance(context, R.style.BrickText_Multiple);
				currentTextView.setText(element.getText());
			}

			currentTextView.setFocusable(false);
			currentTextView.setFocusableInTouchMode(false);
			currentTextView.setClickable(false);
			layout.addView(currentTextView);

			if (element.isNewLineHint()) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) currentTextView.getLayoutParams();
				params.setNewLine(true);
				currentTextView.setLayoutParams(params);
			}
		}

		return prototypeView;
	}

	private Bitmap getBrickImage(View view) {

		boolean drawingCacheEnabled = view.isDrawingCacheEnabled();

		view.setDrawingCacheEnabled(true);

		view.measure(MeasureSpec.makeMeasureSpec(ScreenValues.SCREEN_WIDTH, MeasureSpec.EXACTLY), MeasureSpec
				.makeMeasureSpec(
						Utils.getPhysicalPixels(DragAndDropListView.WIDTH_OF_BRICK_PREVIEW_IMAGE, view.getContext()),
						MeasureSpec.AT_MOST));
		view.layout(0, 0, ScreenValues.SCREEN_WIDTH, view.getMeasuredHeight());

		view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		view.buildDrawingCache(true);

		if (view.getDrawingCache() == null) {
			view.setDrawingCacheEnabled(drawingCacheEnabled);
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(drawingCacheEnabled);

		return bitmap;
	}

	public ImageView getBorderedPreview(final Bitmap bitmap) {
		ImageView imageView = new ImageView(view.getContext());
		imageView.setBackgroundColor(Color.TRANSPARENT);

		int radius = 7;
		Bitmap result = getWithBorder(radius, bitmap, Color.argb(Math.round(0.25f * 255), 0, 0, Math.round(0.1f * 255)));
		imageView.setImageBitmap(result);

		return imageView;
	}

	public Bitmap getWithBorder(int radius, Bitmap bitmap, int color) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int borderedWidth = width + radius * 2;
		int borderedHeight = height + radius * 2;

		Bitmap toReturn = Bitmap.createBitmap(borderedWidth, borderedHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(toReturn);

		Bitmap border = Bitmap.createBitmap(borderedWidth, borderedHeight, Bitmap.Config.ARGB_8888);
		Canvas borderCanvas = new Canvas(border);

		Bitmap alpha = bitmap.extractAlpha();

		Paint paintBorder = new Paint();
		paintBorder.setColor(Color.WHITE);
		Paint paintBorder2 = new Paint();
		paintBorder2.setColor(color);
		Paint paint = new Paint();

		borderCanvas.drawBitmap(alpha, 0, 0, paintBorder);
		borderCanvas.drawBitmap(alpha, radius * 2, 0, paintBorder);
		borderCanvas.drawBitmap(alpha, 0, radius * 2, paintBorder);
		borderCanvas.drawBitmap(alpha, radius * 2, radius * 2, paintBorder);

		alpha = border.extractAlpha();

		canvas.drawBitmap(alpha, 0, 0, paintBorder2);
		canvas.drawBitmap(bitmap, radius, radius, paint);

		return toReturn;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_user_definition_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = alphaValue;
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public void onClick(View eventOrigin) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		UserBrickElementEditorFragment.showFragment(view, this);
	}

	@Override
	public Brick clone() {
		return new UserScriptDefinitionBrick();
	}

	@Override
	public Script getScriptSafe() {
		return getUserScript();
	}

	public Script getUserScript() {
		return script;
	}

	public int addUIText(String text) {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsText();
		element.setText(text);
		int toReturn = userScriptDefinitionBrickElements.size();
		userScriptDefinitionBrickElements.add(element);
		return toReturn;
	}

	public void addUILineBreak() {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsLineBreak();
		element.setText(LINE_BREAK);
		userScriptDefinitionBrickElements.add(element);
	}

	public int addUILocalizedVariable(String name) {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsVariable();
		element.setText(name);

		int toReturn = userScriptDefinitionBrickElements.size();
		userScriptDefinitionBrickElements.add(element);
		return toReturn;
	}

	public void renameUIElement(UserScriptDefinitionBrickElement element, String oldName, String newName, Context context) {
		if (element.getText().equals(oldName)) {
			element.setText(newName);
			if (element.isVariable()) {
				Scene currentScene = ProjectManager.getInstance().getCurrentScene();
				Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
				DataContainer dataContainer = currentScene.getDataContainer();
				if (dataContainer != null) {
					List<UserBrick> matchingBricks = currentSprite.getUserBricksByDefinitionBrick(this, true, true);
					for (UserBrick userBrick : matchingBricks) {
						UserVariable userVariable = dataContainer.getUserVariable(oldName, userBrick, currentSprite);
						if (userVariable != null) {
							userVariable.setName(newName);
						}
					}
				}
			}
		}

		renameVariablesInFormulasAndBricks(oldName, newName, context);
	}

	public void removeDataAt(int id, Context context) {
		removeVariablesInFormulas(getUserScriptDefinitionBrickElements().get(id).getText(), context);
		userScriptDefinitionBrickElements.remove(id);
	}

	/**
	 * Removes element at <b>from</b> and adds it after element at <b>to</b>
	 */
	public void reorderUIData(int from, int to) {

		if (to == -1) {
			UserScriptDefinitionBrickElement element = getUserScriptDefinitionBrickElements().remove(from);
			userScriptDefinitionBrickElements.add(0, element);
		} else if (from <= to) {
			UserScriptDefinitionBrickElement element = getUserScriptDefinitionBrickElements().remove(from);
			userScriptDefinitionBrickElements.add(to, element);
		} else {
			// from > to
			UserScriptDefinitionBrickElement element = getUserScriptDefinitionBrickElements().remove(from);
			userScriptDefinitionBrickElements.add(to + 1, element);
		}
	}

	public CharSequence getName() {
		CharSequence name = "";
		for (UserScriptDefinitionBrickElement element : getUserScriptDefinitionBrickElements()) {
			if (!element.isVariable()) {
				name = element.getText();
				break;
			}
		}
		return name;
	}

	public List<UserScriptDefinitionBrickElement> getUserScriptDefinitionBrickElements() {
		return userScriptDefinitionBrickElements;
	}

	public void renameVariablesInFormulasAndBricks(String oldName, String newName, Context context) {
		List<Brick> brickList = script.getBrickList();
		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				List<Formula> formulaList = ((UserBrick) brick).getFormulas();
				for (Formula formula : formulaList) {
					formula.updateVariableReferences(oldName, newName, context);
				}
			}
			if (brick instanceof FormulaBrick) {
				List<Formula> formulas = ((FormulaBrick) brick).getFormulas();
				for (Formula formula : formulas) {
					formula.updateVariableReferences(oldName, newName, context);
				}
			}
			if (brick instanceof ShowVariableBrick) {
				ShowVariableBrick showVariableBrick = (ShowVariableBrick) brick;
				if (showVariableBrick.getUserVariable().getName().equals(oldName)) {
					((ShowVariableBrick) brick).getUserVariable().setName(newName);
				}
			}
			if (brick instanceof HideVariableBrick) {
				HideVariableBrick showTextBrick = (HideVariableBrick) brick;
				if (showTextBrick.getUserVariable().getName().equals(oldName)) {
					((HideVariableBrick) brick).getUserVariable().setName(newName);
				}
			}
		}
	}

	public void removeVariablesInFormulas(String name, Context context) {
		if (ProjectManager.getInstance().getCurrentScript() == null) {
			return;
		}
		List<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				List<Formula> formulaList = ((UserBrick) brick).getFormulas();
				for (Formula formula : formulaList) {
					formula.removeVariableReferences(name, context);
				}
			}
			if (brick instanceof FormulaBrick) {
				List<Formula> formulas = ((FormulaBrick) brick).getFormulas();
				for (Formula formula : formulas) {
					formula.removeVariableReferences(name, context);
				}
			}
		}
	}
}
