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

package org.catrobat.catroid.sensing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Polygon;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.ImageEditing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.com.hjg.pngj.PngjInputException;

public class CollisionInformation {
	private static final String TAG = CollisionInformation.class.getSimpleName();

	public Polygon[] collisionPolygons;
	public Thread collisionPolygonCalculationThread;
	private boolean isCalculationThreadCancelled = true;
	private LookData lookData;

	public CollisionInformation(LookData lookData) {
		this.lookData = lookData;
	}

	public void calculate() {
		isCalculationThreadCancelled = false;
		CollisionPolygonCreationTask task = new CollisionPolygonCreationTask(lookData);
		collisionPolygonCalculationThread = new Thread(task);
		collisionPolygonCalculationThread.start();
	}

	public boolean isCalculationCancelled() {
		return isCalculationThreadCancelled;
	}

	public void cancelCalculation() {
		isCalculationThreadCancelled = true;
		Log.i(TAG, "Collision Polygon Calculation of " + lookData.getName() + " cancelled!");
	}

	public int getNumberOfVertices() {
		int size = 0;
		for (Polygon polygon : collisionPolygons) {
			size += polygon.getVertices().length / 2;
		}
		return size;
	}

	public void loadOrCreateCollisionPolygon() {
		isCalculationThreadCancelled = false;
		String path = lookData.getAbsolutePath();
		if (collisionPolygons == null) {
			if (!path.endsWith(".png")) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				collisionPolygons = createCollisionPolygonByHitbox(bitmap);
				return;
			}
			collisionPolygons = getCollisionPolygonFromPNGMeta(path);
			if (collisionPolygons.length == 0) {
				Log.i(TAG, "No Collision information from PNG file, creating new one.");
				if (isCalculationThreadCancelled) {
					return;
				}
				ArrayList<ArrayList<CollisionPolygonVertex>> boundingPolygon = createBoundingPolygonVertices(path, lookData);
				if (boundingPolygon.size() == 0) {
					return;
				}
				float epsilon = Constants.COLLISION_POLYGON_CREATION_EPSILON;
				do {

					if (isCalculationThreadCancelled) {
						return;
					}
					ArrayList<Polygon> temporaryCollisionPolygons = new ArrayList<Polygon>();
					for (int i = 0; i < boundingPolygon.size(); i++) {
						if (isCalculationThreadCancelled) {
							return;
						}
						ArrayList<PointF> points = getPointsFromPolygonVertices(boundingPolygon.get(i));
						ArrayList<PointF> simplified = simplifyPolygon(points, 0, points.size() - 1,
								epsilon);
						if (pointToPointDistance(simplified.get(0), simplified.get(simplified.size() - 1)) < epsilon) {
							simplified.remove(simplified.size() - 1);
						}
						if (simplified.size() < 3) {
							continue;
						}

						temporaryCollisionPolygons.add(createPolygonFromPoints(simplified));
					}

					collisionPolygons = temporaryCollisionPolygons.toArray(new Polygon[temporaryCollisionPolygons
							.size()]);
					epsilon *= 1.2f;
				}
				while (getNumberOfVertices() > Constants.COLLISION_VERTEX_LIMIT);

				if (collisionPolygons.length == 0) {
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					collisionPolygons = createCollisionPolygonByHitbox(bitmap);
				}

				if (isCalculationThreadCancelled) {
					return;
				}
				writeCollisionVerticesToPNGMeta(collisionPolygons, path);
				Log.i("CollsionPolygon", "Polygon size of look " + lookData.getName() + ": " + getNumberOfVertices());
			}
		}
	}

	public static ArrayList<ArrayList<CollisionPolygonVertex>> createBoundingPolygonVertices(String absoluteBitmapPath,
			LookData lookData) {
		Bitmap bitmap = BitmapFactory.decodeFile(absoluteBitmapPath);
		if (bitmap == null) {
			Log.e("CollisionPolygon", "bitmap " + absoluteBitmapPath + " is null. Cannot create Collision polygon");
			return new ArrayList<>();
		}

		Matrix matrix = new Matrix();
		matrix.preScale(1.0f, -1.0f);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		boolean[][] grid = createCollisionGrid(bitmap);

		if (lookData.getCollisionInformation().isCalculationThreadCancelled) {
			return new ArrayList<>();
		}

		ArrayList<CollisionPolygonVertex> vertical = createVerticalVertices(grid, bitmap.getWidth(), bitmap.getHeight());
		ArrayList<CollisionPolygonVertex> horizontal = createHorizontalVertices(grid, bitmap.getWidth(), bitmap.getHeight());

		if (lookData.getCollisionInformation().isCalculationThreadCancelled) {
			return new ArrayList<>();
		}

		ArrayList<ArrayList<CollisionPolygonVertex>> finalVertices = new ArrayList<>();
		finalVertices.add(new ArrayList<CollisionPolygonVertex>());
		int polygonNumber = 0;
		finalVertices.get(polygonNumber).add(vertical.get(0));
		vertical.remove(0);

		do {
			if (lookData.getCollisionInformation().isCalculationThreadCancelled) {
				return new ArrayList<>();
			}

			CollisionPolygonVertex end = finalVertices.get(polygonNumber).get(finalVertices.get(polygonNumber)
					.size() - 1);
			boolean found = false;
			for (int h = 0; h < horizontal.size(); h++) {
				if (end.isConnected(horizontal.get(h))) {
					finalVertices.get(polygonNumber).add(horizontal.get(h));
					horizontal.remove(h);
					found = true;
					break;
				}
			}

			if (found) {
				end = finalVertices.get(polygonNumber).get(finalVertices.get(polygonNumber).size() - 1);
			}

			for (int v = 0; v < vertical.size(); v++) {
				if (end.isConnected(vertical.get(v))) {
					finalVertices.get(polygonNumber).add(vertical.get(v));
					vertical.remove(v);
					found = true;
					break;
				}
			}
			if (!found) {
				polygonNumber++;
				finalVertices.add(new ArrayList<CollisionPolygonVertex>());
				finalVertices.get(polygonNumber).add(vertical.get(0));
				vertical.remove(0);
			}
		} while (horizontal.size() > 0);
		return finalVertices;
	}

	public static ArrayList<CollisionPolygonVertex> createHorizontalVertices(boolean[][] grid, int gridWidth, int
			gridHeight) {
		ArrayList<CollisionPolygonVertex> horizontal = new ArrayList<CollisionPolygonVertex>();
		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {
				if (grid[x][y]) {

					boolean topEdge = y == 0 || !grid[x][y - 1];
					if (topEdge) {
						boolean extendPrevious = horizontal.size() > 0
								&& horizontal.get(horizontal.size() - 1).endX == x
								&& horizontal.get(horizontal.size() - 1).endY == y;
						boolean extendPreviousOtherSide = horizontal.size() > 1
								&& horizontal.get(horizontal.size() - 2).endX == x
								&& horizontal.get(horizontal.size() - 2).endY == y;

						if (extendPrevious) {
							horizontal.get(horizontal.size() - 1).extend(x + 1, y);
						} else if (extendPreviousOtherSide) {
							horizontal.get(horizontal.size() - 2).extend(x + 1,
									horizontal.get(horizontal.size() - 2).endY);
						} else {
							horizontal.add(new CollisionPolygonVertex(x, y, x + 1, y));
						}
					}

					boolean bottomEdge = y == gridHeight - 1 || !grid[x][y + 1];
					if (bottomEdge) {
						boolean extendPrevious = horizontal.size() > 0
								&& horizontal.get(horizontal.size() - 1).endX == x
								&& horizontal.get(horizontal.size() - 1).endY == y + 1;
						boolean extendPreviousOtherSide = horizontal.size() > 1
								&& horizontal.get(horizontal.size() - 2).endX == x
								&& horizontal.get(horizontal.size() - 2).endY == y + 1;

						if (extendPrevious) {
							horizontal.get(horizontal.size() - 1).extend(x + 1, y + 1);
						} else if (extendPreviousOtherSide) {
							horizontal.get(horizontal.size() - 2).extend(x + 1,
									horizontal.get(horizontal.size() - 2).endY);
						} else {
							horizontal.add(new CollisionPolygonVertex(x, y + 1, x + 1, y + 1));
						}
					}
				}
			}
		}
		return horizontal;
	}

	public static ArrayList<CollisionPolygonVertex> createVerticalVertices(boolean[][] grid, int gridWidth, int
			gridHeight) {
		ArrayList<CollisionPolygonVertex> vertical = new ArrayList<CollisionPolygonVertex>();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				if (grid[x][y]) {
					boolean leftEdge = x == 0 || !grid[x - 1][y];
					if (leftEdge) {
						boolean extendPrevious = vertical.size() > 0
								&& vertical.get(vertical.size() - 1).endX == x
								&& vertical.get(vertical.size() - 1).endY == y;
						boolean extendPreviousOtherSide = vertical.size() > 1
								&& vertical.get(vertical.size() - 2).endX == x
								&& vertical.get(vertical.size() - 2).endY == y;

						if (extendPrevious) {
							vertical.get(vertical.size() - 1).extend(vertical.get(vertical.size() - 1).endX, y + 1);
						} else if (extendPreviousOtherSide) {
							vertical.get(vertical.size() - 2).extend(vertical.get(vertical.size() - 2).endX, y + 1);
						} else {
							vertical.add(new CollisionPolygonVertex(x, y, x, y + 1));
						}
					}

					boolean rightEdge = x == gridWidth - 1 || !grid[x + 1][y];
					if (rightEdge) {
						boolean extendPrevious = vertical.size() > 0
								&& vertical.get(vertical.size() - 1).endX == x + 1
								&& vertical.get(vertical.size() - 1).endY == y;
						boolean extendPreviousOtherSide = vertical.size() > 1
								&& vertical.get(vertical.size() - 2).endX == x + 1
								&& vertical.get(vertical.size() - 2).endY == y;

						if (extendPrevious) {
							vertical.get(vertical.size() - 1).extend(vertical.get(vertical.size() - 1).endX, y + 1);
						} else if (extendPreviousOtherSide) {
							vertical.get(vertical.size() - 2).extend(vertical.get(vertical.size() - 2).endX, y + 1);
						} else {
							vertical.add(new CollisionPolygonVertex(x + 1, y, x + 1, y + 1));
						}
					}
				}
			}
		}
		return vertical;
	}

	private static float pointToLineDistance(PointF lineStart, PointF lineEnd, PointF point) {
		float normalLength = (float) Math.sqrt((lineEnd.x - lineStart.x) * (lineEnd.x - lineStart.x)
				+ (lineEnd.y - lineStart.y) * (lineEnd.y - lineStart.y));
		return Math.abs((point.x - lineStart.x) * (lineEnd.y - lineStart.y)
				- (point.y - lineStart.y) * (lineEnd.x - lineStart.x)) / normalLength;
	}

	private static float pointToPointDistance(PointF p1, PointF p2) {
		return (float) Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
	}

	public static ArrayList<PointF> simplifyPolygon(ArrayList<PointF> points, int start, int end, float epsilon) {
		//Ramer-Douglas-Peucker Algorithm
		float dmax = 0f;
		int index = start;

		for (int i = index + 1; i < end; ++i) {
			float d = pointToLineDistance(points.get(start), points.get(end), points.get(i));
			if (d > dmax) {
				index = i;
				dmax = d;
			}
		}

		ArrayList<PointF> finalRes = new ArrayList<>();
		if (dmax > epsilon) {
			ArrayList<PointF> res1 = simplifyPolygon(points, start, index, epsilon);
			ArrayList<PointF> res2 = simplifyPolygon(points, index, end, epsilon);

			for (int i = 0; i < res1.size() - 1; i++) {
				finalRes.add(res1.get(i));
			}
			for (int i = 0; i < res2.size(); i++) {
				finalRes.add(res2.get(i));
			}
		} else {
			finalRes.add(points.get(start));
			finalRes.add(points.get(end));
		}
		return finalRes;
	}

	public static ArrayList<PointF> getPointsFromPolygonVertices(ArrayList<CollisionPolygonVertex> polygon) {
		ArrayList<PointF> points = new ArrayList<>();
		for (CollisionPolygonVertex vertex : polygon) {
			points.add(vertex.getStartPoint());
		}
		return points;
	}

	public static Polygon createPolygonFromPoints(ArrayList<PointF> points) {
		float[] polygonNodes = new float[points.size() * 2];
		for (int node = 0; node < points.size(); node++) {
			polygonNodes[node * 2] = points.get(node).x;
			polygonNodes[node * 2 + 1] = points.get(node).y;
		}
		return new Polygon(polygonNodes);
	}

	public static boolean[][] createCollisionGrid(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		boolean[][] grid = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (bitmap.getPixel(x, y) != 0) {
					grid[x][y] = true;
				}
			}
		}
		return grid;
	}

	public static void writeCollisionVerticesToPNGMeta(Polygon[] collisionPolygon,
			String absolutePath) {
		String metaToWrite = "";
		for (Polygon polygon : collisionPolygon) {
			for (int f = 0; f < polygon.getVertices().length; f++) {
				metaToWrite += polygon.getVertices()[f] + ";";
			}
			metaToWrite = metaToWrite.substring(0, metaToWrite.length() - 1);
			metaToWrite += "|";
		}
		if (!metaToWrite.equals("")) {
			metaToWrite = metaToWrite.substring(0, metaToWrite.length() - 1);
			ImageEditing.writeMetaDataStringToPNG(absolutePath, Constants.COLLISION_PNG_META_TAG_KEY, metaToWrite);
		}
	}

	public static Polygon[] getCollisionPolygonFromPNGMeta(String absolutePath) {
		String metadata;
		try {
			metadata = ImageEditing.readMetaDataStringFromPNG(absolutePath, Constants.COLLISION_PNG_META_TAG_KEY);
		} catch (PngjInputException e) {
			Log.e(TAG, "Error reading metadata from png!");
			return new Polygon[0];
		}

		boolean isMetadataValid = checkMetaDataString(metadata);
		if (!isMetadataValid) {
			return new Polygon[0];
		}

		String[] polygonStrings = metadata.split("\\|");
		Polygon[] collisionPolygon = new Polygon[polygonStrings.length];
		for (int polygonString = 0; polygonString < polygonStrings.length; polygonString++) {
			String[] pointStrings = polygonStrings[polygonString].split(";");
			float[] points = new float[pointStrings.length];
			for (int pointString = 0; pointString < pointStrings.length; pointString++) {
				points[pointString] = Float.valueOf(pointStrings[pointString]);
			}
			collisionPolygon[polygonString] = new Polygon(points);
		}
		Log.i(TAG, "Loaded CollisionPolygon from " + absolutePath + " successfully!");
		return collisionPolygon;
	}

	public static boolean checkMetaDataString(String metadata) {
		if (metadata == null || metadata.equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile(Constants.COLLISION_POLYGON_METADATA_PATTERN);
		Matcher matcher = pattern.matcher(metadata);
		if (matcher.find() && matcher.group().equals(metadata)) {
			return true;
		}
		Log.e("Collision Polygon", "Invalid Metadata, creating new Polygon");
		return false;
	}

	public static Polygon[] createCollisionPolygonByHitbox(Bitmap bitmap) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		float[] vertices = { 0f, 0f, width, 0f, width, height, 0f, height };
		Polygon polygon = new Polygon(vertices);
		Polygon[] polygons = new Polygon[1];
		polygons[0] = polygon;
		return polygons;
	}

	public void printDebugCollisionPolygons() {
		int polygonNr = 0;
		for (Polygon p : collisionPolygons) {
			Log.i(TAG, "Collision Polygon " + ++polygonNr + " :\n" + Arrays.toString(p.getTransformedVertices()));
		}
	}
}
