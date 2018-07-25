/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.embroidery;

import android.content.res.Resources;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.XmlHeader;

import java.util.ArrayList;

public final class EmbroideryManager {
	private static final EmbroideryManager INSTANCE = new EmbroideryManager();
	private ArrayList<PointF> stitchPoints = new ArrayList<>();
	private float stitchSize = BrickValues.STITCH_SIZE * calculateScreenRatio();
	private int lastIndex = 0;

	private EmbroideryManager() {
	}

	public static EmbroideryManager getInstance() {
		return INSTANCE;
	}

	private float calculateScreenRatio() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		float deviceDiagonalPixel = (float) Math.sqrt(Math.pow(metrics.widthPixels, 2) + Math.pow(metrics.heightPixels, 2));
		float creatorDiagonalPixel = (float) Math.sqrt(Math.pow(header.getVirtualScreenWidth(), 2)
				+ Math.pow(header.getVirtualScreenHeight(), 2));
		return creatorDiagonalPixel / deviceDiagonalPixel;
	}

	public void addStitchPoint(PointF point) {
		if (stitchPoints.isEmpty() || isNewStitchPoint(point)) {
			stitchPoints.add(point);
			lastIndex = stitchPoints.size() - 1;
		}
	}

	private Boolean isNewStitchPoint(PointF point) {
		if (point.x != stitchPoints.get(lastIndex).x || point.y != stitchPoints.get(lastIndex).y) {
			return true;
		}
		return false;
	}

	public ArrayList<PointF> getStitchPoints() {
		return stitchPoints;
	}

	public void clearStitchPoints() {
		stitchPoints.clear();
		lastIndex = 0;
	}

	public float getStitchSize() {
		return stitchSize;
	}

	public int getLastIndex() {
		return lastIndex;
	}
}
