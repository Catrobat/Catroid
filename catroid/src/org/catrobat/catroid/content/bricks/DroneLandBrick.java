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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.util.List;

public class DroneLandBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	@XStreamOmitField
	private transient View view;

	public DroneLandBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {
		try {
			//DroneHandler.getInstance().getDrone().land();
		} catch (Exception e) {
			//Log.e(DroneConsts.DroneLogTag, "Exception DroneLandBrick -> execute()", e);
		}
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			//			view = View.inflate(context, R.layout.toolbox_brick_drone_land, null);
		}
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		//		return View.inflate(context, R.layout.toolbox_brick_drone_land, null);
		return null;
	}

	@Override
	public Brick clone() {
		return new DroneLandBrick(getSprite());
	}

	@Override
	public int getRequiredResources() {
		return ARDRONE_SUPPORT;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.catrobat.catroid.content.bricks.Brick copyBrickForSprite(org.catrobat.catroid.content.Sprite sprite,
			Script script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAlphaValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		// TODO Auto-generated method stub

	}

	@Override
	public CheckBox getCheckBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCheckboxView(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnimationState(boolean animationState) {
		// TODO Auto-generated method stub

	}

}
