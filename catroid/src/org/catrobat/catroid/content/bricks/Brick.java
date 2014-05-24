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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.io.Serializable;
import java.util.List;

public interface Brick extends Serializable {

	//use bitwise | for using multiple ressources in a brick
	int NO_RESOURCES = 0x0;
	//	public static final int SOUND_MANAGER = 0x1;
	int TEXT_TO_SPEECH = 0x2;
	int BLUETOOTH_LEGO_NXT = 0x4;
	int ARDRONE_SUPPORT = 0x20;

	int CAMERA_LED = 0x10;
	int VIBRATOR = 0x20;

	//	public static final int BLUETOOTH_ARDUINO = 0x8;

	List<SequenceAction> addActionToSequence(SequenceAction sequence);

	Sprite getSprite();

	//needed for the Sprite#clone()-Method
	Brick copyBrickForSprite(Sprite sprite, Script script);

	View getView(Context context, int brickId, BaseAdapter adapter);

	View getPrototypeView(Context context);

	Brick clone();

	int getRequiredResources();

	void setCheckboxVisibility(int visibility);

	int getAlphaValue();

	void setBrickAdapter(BrickAdapter adapter);

	CheckBox getCheckBox();

	boolean isChecked();

	void setCheckedBoolean(boolean newValue);

	void setCheckboxView(int id);

	View getViewWithAlpha(int alphaValue);

	void setAnimationState(boolean animationState);
}
