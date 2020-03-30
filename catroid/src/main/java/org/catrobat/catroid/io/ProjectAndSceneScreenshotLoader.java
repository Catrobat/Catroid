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
package org.catrobat.catroid.io;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_MANUAL_FILE_NAME;

public class ProjectAndSceneScreenshotLoader {

	private class ScreenshotData {
		public String projectName;
		public String sceneName;
		public boolean isBackpackScene;
		public ImageView imageView;

		ScreenshotData(String projectName, String sceneName, boolean isBackpackScene, ImageView imageView) {
			this.projectName = projectName;
			this.sceneName = sceneName;
			this.isBackpackScene = isBackpackScene;
			this.imageView = imageView;
		}
	}

	private static final int POOL_SIZE = 5;
	private static final int CACHE_MAX_SIZE = 25;
	private static final float LOAD_FACTOR = .75f;
	private static final int INITIAL_VALUE = 13; // (N / LOAD_FACTOR) + 1

	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<>());
	private ExecutorService executorService;
	private int thumbnailWidth;
	private int thumbnailHeight;

	private Map<String, Bitmap> imageCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(
			INITIAL_VALUE, LOAD_FACTOR, true) {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			return size() > CACHE_MAX_SIZE;
		}
	});

	public ProjectAndSceneScreenshotLoader(int thumbnailWidth, int thumbnailHeight) {
		executorService = Executors.newFixedThreadPool(POOL_SIZE);
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;
	}

	public void loadAndShowScreenshot(String projectName, String sceneName, boolean isBackpackScene, ImageView
			imageView) {

		String screenShotName = "";
		if (projectName != null) {
			screenShotName = projectName;
		}
		if (sceneName != null) {
			screenShotName = screenShotName.concat(sceneName);
		}

		imageViews.put(imageView, screenShotName);
		Bitmap bitmap = imageCache.get(screenShotName);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			//set a dummy or null in the meantime
			imageView.setImageBitmap(null);
			//queue the loading and showing process
			ScreenshotData screenshotData = new ScreenshotData(projectName, sceneName, isBackpackScene, imageView);
			executorService.submit(new ScreenshotLoader(screenshotData));
		}
	}

	public File getScreenshotFile(String projectName, String sceneName, boolean isBackpackScene) {
		ScreenshotData screenshotData = new ScreenshotData(projectName, sceneName, isBackpackScene, null);
		ScreenshotLoader screenshotLoader = new ScreenshotLoader(screenshotData);
		return screenshotLoader.getScreenshotFile();
	}

	class ScreenshotLoader implements Runnable {
		ScreenshotData projectAndSceneScreenshotData;

		ScreenshotLoader(ScreenshotData screenshotData) {
			this.projectAndSceneScreenshotData = screenshotData;
		}

		@Override
		public void run() {
			if (imageViewReused(projectAndSceneScreenshotData)) {
				return;
			}

			File projectAndSceneImageFile = getScreenshotFile();
			String pathOfScreenshot = projectAndSceneImageFile.getAbsolutePath();

			final Bitmap projectAndSceneImage;
			if (!projectAndSceneImageFile.exists() || ImageEditing.getImageDimensions(pathOfScreenshot)[0] < 0) {
				projectAndSceneImage = null;
			} else {
				projectAndSceneImage = ImageEditing.getScaledBitmapFromPath(pathOfScreenshot, thumbnailWidth, thumbnailHeight,
						ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
			}

			String screenshotName = "";
			if (projectAndSceneScreenshotData.projectName != null) {
				screenshotName = projectAndSceneScreenshotData
						.projectName;
			}
			if (projectAndSceneScreenshotData.sceneName != null) {
				screenshotName = screenshotName.concat(projectAndSceneScreenshotData.sceneName);
			}

			imageCache.put(screenshotName, projectAndSceneImage);
			if (imageViewReused(projectAndSceneScreenshotData)) {
				return;
			}

			Activity uiActivity = UiUtils.getActivityFromView(projectAndSceneScreenshotData.imageView);

			if (uiActivity != null) {
				uiActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (imageViewReused(projectAndSceneScreenshotData)) {
							return;
						}
						if (projectAndSceneImage != null) {
							projectAndSceneScreenshotData.imageView.setImageBitmap(projectAndSceneImage);
						} else {
							projectAndSceneScreenshotData.imageView.setImageBitmap(null);
						}
					}
				});
			}
		}

		File getScreenshotFile() {
			File manualScreenshotFile;
			File automaticScreenShotFile;
			if (projectAndSceneScreenshotData.sceneName != null) {
				if (projectAndSceneScreenshotData.isBackpackScene) {
					File sceneDir =
							new File(BackpackListManager.getInstance().backpackSceneDirectory,
							projectAndSceneScreenshotData.sceneName);
					manualScreenshotFile = new File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME);
					automaticScreenShotFile = new File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
				} else {
					File sceneDir = new File(new File(DEFAULT_ROOT_DIRECTORY,
							projectAndSceneScreenshotData.projectName), projectAndSceneScreenshotData.sceneName);
					manualScreenshotFile = new File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME);
					automaticScreenShotFile = new File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
				}
			} else {
				File projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectAndSceneScreenshotData.projectName);
				manualScreenshotFile = new File(projectDir, SCREENSHOT_MANUAL_FILE_NAME);
				automaticScreenShotFile = new File(projectDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
			}

			if (manualScreenshotFile.exists() && manualScreenshotFile.length() > 0) {
				return manualScreenshotFile;
			} else {
				manualScreenshotFile.delete();
				return automaticScreenShotFile;
			}
		}
	}

	private boolean imageViewReused(ScreenshotData projectScreenshotData) {
		String tag = imageViews.get(projectScreenshotData.imageView);
		String screenshotName = "";
		if (projectScreenshotData.projectName != null) {
			screenshotName = projectScreenshotData.projectName;
		}
		if (projectScreenshotData.sceneName != null) {
			screenshotName = screenshotName.concat(projectScreenshotData.sceneName);
		}
		return (tag == null || !tag.equals(screenshotName));
	}
}
