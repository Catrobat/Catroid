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
package org.catrobat.catroid.physics.shapebuilder;

import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;

import java.util.HashMap;
import java.util.Map;

public final class PhysicsShapeBuilder {

	private static final String TAG = PhysicsShapeBuilder.class.getSimpleName();
	private static final float[] ACCURACY_LEVELS = { 0.125f, 0.25f, 0.50f, 0.75f, 1.0f };

	private static PhysicsShapeBuilder instance = null;

	public static PhysicsShapeBuilder getInstance() {
		if (instance == null) {
			instance = new PhysicsShapeBuilder();
		}
		return instance;
	}

	private PhysicsShapeBuilderStrategy strategy = new PhysicsShapeBuilderStrategyFastHull();
	private Map<String, ImageShapes> imageShapesMap = new HashMap<>();

	private PhysicsShapeBuilder() {
	}

	public void reset() {
		strategy = new PhysicsShapeBuilderStrategyFastHull();
		imageShapesMap = new HashMap<>();
	}

	public synchronized Shape[] getScaledShapes(LookData lookData, float scaleFactor) throws RuntimeException {
		if (scaleFactor < 0) {
			throw new RuntimeException("scaleFactor can not be smaller than 0");
		} else if (lookData == null) {
			throw new RuntimeException("get shape for null lookData not possible");
		}

		Pixmap pixmap = lookData.getPixmap();
		if (pixmap == null) {
			Log.e(TAG, "pixmap should not be null");
			return null;
		}

		String imageIdentifier = lookData.getChecksum();
		if (!imageShapesMap.containsKey(imageIdentifier)) {
			imageShapesMap.put(imageIdentifier, new ImageShapes(pixmap));
		}

		float accuracyLevel = getAccuracyLevel(scaleFactor);
		Shape[] shapes = imageShapesMap.get(imageIdentifier).getShapes(accuracyLevel);

		if (shapes == null) {
			Log.e(TAG, "shapes should not be null");
			return null;
		}

		return PhysicsShapeScaleUtils.scaleShapes(shapes, scaleFactor);
	}

	private static float getAccuracyLevel(float scaleFactor) {
		if (ACCURACY_LEVELS.length == 0) {
			return 0;
		}

		if (ACCURACY_LEVELS.length == 1) {
			return ACCURACY_LEVELS[0];
		}

		for (int accuracyIdx = 0; accuracyIdx < ACCURACY_LEVELS.length - 1; accuracyIdx++) {
			float average = (ACCURACY_LEVELS[accuracyIdx] + ACCURACY_LEVELS[accuracyIdx]) / 2;
			if (scaleFactor < average) {
				return ACCURACY_LEVELS[accuracyIdx];
			}
		}
		return ACCURACY_LEVELS[ACCURACY_LEVELS.length - 1];
	}

	/**
	 * Saves computed shapes in different accuracies for one image. (All in baseline -> 100%)
	 */
	private class ImageShapes {

		private static final int MAX_ORIGINAL_PIXMAP_SIZE = 512;

		private Map<String, Shape[]> shapeMap = new HashMap<>();
		private Pixmap pixmap;
		private float sizeAdjustmentScaleFactor = 1;

		public ImageShapes(Pixmap pixmap) {
			if (pixmap == null) {
				throw new RuntimeException("Pixmap must not null");
			}
			this.pixmap = pixmap;
			int width = this.pixmap.getWidth();
			int height = this.pixmap.getHeight();
			if (width > MAX_ORIGINAL_PIXMAP_SIZE || height > MAX_ORIGINAL_PIXMAP_SIZE) {
				if (width > height) {
					sizeAdjustmentScaleFactor = (float) MAX_ORIGINAL_PIXMAP_SIZE / width;
				} else {
					sizeAdjustmentScaleFactor = (float) MAX_ORIGINAL_PIXMAP_SIZE / height;
				}
			}
		}

		private String getShapeKey(float accuracyLevel) {
			return String.valueOf((int) (accuracyLevel * 100));
		}

		private Shape[] computeNewShape(float accuracy) {
			int width = pixmap.getWidth();
			int height = pixmap.getHeight();
			int scaledWidth = Math.round(width * sizeAdjustmentScaleFactor * accuracy);
			int scaledHeight = Math.round(height * sizeAdjustmentScaleFactor * accuracy);

			if (scaledWidth < 1) {
				scaledWidth = 1;
			}
			if (scaledHeight < 1) {
				scaledHeight = 1;
			}

			Pixmap.setFilter(Pixmap.Filter.NearestNeighbour);
			Pixmap scaledPixmap = new Pixmap(scaledWidth, scaledHeight, pixmap.getFormat());
			scaledPixmap.drawPixmap(pixmap, 0, 0, width, height, 0, 0, scaledWidth, scaledHeight);
			Shape[] scaledShapes = strategy.build(scaledPixmap, 1.0f);

			return PhysicsShapeScaleUtils.scaleShapes(scaledShapes, 1.0f, sizeAdjustmentScaleFactor * accuracy);
		}

		public Shape[] getShapes(float accuracyLevel) throws RuntimeException {
			String shapeKey = getShapeKey(accuracyLevel);

			if (!shapeMap.containsKey(shapeKey)) {
				Shape[] shapes = computeNewShape(accuracyLevel);
				if (shapes == null) {
					return null;
				}
				shapeMap.put(shapeKey, shapes);
			}

			return shapeMap.get(shapeKey);
		}
	}
}
