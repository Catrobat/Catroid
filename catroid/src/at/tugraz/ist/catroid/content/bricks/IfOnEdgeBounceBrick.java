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
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class IfOnEdgeBounceBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public IfOnEdgeBounceBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		float size = sprite.costume.getSize();

		sprite.costume.aquireXYWidthHeightLock();
		float width = sprite.costume.getWidth() * size;
		float height = sprite.costume.getHeight() * size;
		int xPosition = (int) sprite.costume.getXPosition();
		int yPosition = (int) sprite.costume.getYPosition();
		sprite.costume.releaseXYWidthHeightLock();

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().virtualScreenHeight / 2;
		float rotationResult = -sprite.costume.rotation + 90f;

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

		sprite.costume.rotation = -rotationResult + 90f;

		sprite.costume.aquireXYWidthHeightLock();
		sprite.costume.setXYPosition(xPosition, yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
		}

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_on_edge_bounce, null);
	}

	@Override
	public Brick clone() {
		return new IfOnEdgeBounceBrick(sprite);
	}

}
