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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import org.catrobat.catroid.R;

public class IfOnEdgeBounceBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient View view;

	public IfOnEdgeBounceBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public IfOnEdgeBounceBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		float size = sprite.look.getSize();

		sprite.look.aquireXYWidthHeightLock();
		float width = sprite.look.getWidth() * size;
		float height = sprite.look.getHeight() * size;
		int xPosition = (int) sprite.look.getXPosition();
		int yPosition = (int) sprite.look.getYPosition();
		sprite.look.releaseXYWidthHeightLock();

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().virtualScreenHeight / 2;
		float rotationResult = -sprite.look.rotation + 90f;

		if (xPosition < -virtualScreenWidth + width / 2) {

			rotationResult = Math.abs(rotationResult);
			xPosition = -virtualScreenWidth + (int) (width / 2);

		} else if (xPosition > virtualScreenWidth - width / 2) {

			rotationResult = -Math.abs(rotationResult);

			xPosition = virtualScreenWidth - (int) (width / 2);
		}

		if (yPosition > virtualScreenHeight - height / 2) {

			if (Math.abs(rotationResult) < 90f) {
				if (rotationResult < 0f) {
					rotationResult = -180f - rotationResult;
				} else {
					rotationResult = 180f - rotationResult;
				}
			}

			yPosition = virtualScreenHeight - (int) (height / 2);

		} else if (yPosition < -virtualScreenHeight + height / 2) {

			if (Math.abs(rotationResult) > 90f) {
				if (rotationResult < 0f) {
					rotationResult = -180f - rotationResult;
				} else {
					rotationResult = 180f - rotationResult;
				}
			}

			yPosition = -virtualScreenHeight + (int) (height / 2);
		}

		sprite.look.rotation = -rotationResult + 90f;

		sprite.look.aquireXYWidthHeightLock();
		sprite.look.setXYPosition(xPosition, yPosition);
		sprite.look.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
	}

	@Override
	public Brick clone() {
		return new IfOnEdgeBounceBrick(sprite);
	}

}
