/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetCostumeBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String imageName;
	@XStreamOmitField
	private transient Bitmap thumbnail;

	public SetCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setCostume(String imageName) {
		this.imageName = imageName;
		if (imageName != null) {
			thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
					Consts.THUMBNAIL_WIDTH);
		}
	}

	private Object readResolve() {
		if (imageName != null && ProjectManager.getInstance().getCurrentProject() != null) {
			String[] checksum = imageName.split("_");
			ProjectManager.getInstance().fileChecksumContainer.addChecksum(checksum[0], getAbsoluteImagePath());
		}
		return this;
	}

	public void execute() {
		this.sprite.getCostume().changeImagePath(getAbsoluteImagePath());
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getImagePath() {
		return getAbsoluteImagePath();
	}

	private String getAbsoluteImagePath() {
		if (imageName == null) {
			return null;
		}
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/" + imageName;
	}

	public View getView(final Context context, final int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_set_costume, null);

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				((Activity) context).startActivityForResult(intent, brickId);
			}
		};

		ImageView imageView = (ImageView) view.findViewById(R.id.costume_image_view);

		if (imageName != null) {
			if (thumbnail == null) {
				thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
						Consts.THUMBNAIL_WIDTH);
			}

			imageView.setImageBitmap(thumbnail);
			imageView.setBackgroundDrawable(null);
		}

		imageView.setOnClickListener(listener);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_set_costume, null);
		return view;
	}

	@Override
	public Brick clone() {
		SetCostumeBrick clonedBrick = new SetCostumeBrick(getSprite());
		if (sprite.getCostume() != null) {
			clonedBrick.setCostume(null);
		}

		return clonedBrick;
	}
}
