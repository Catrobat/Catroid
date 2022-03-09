/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.util.Log;
import android.widget.ImageView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.koin.java.KoinJavaComponent.inject;

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

	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private int thumbnailWidth;
	private int thumbnailHeight;

	private static int[] placeholderImages = {R.drawable.catrobat, R.drawable.elephant, R.drawable.lynx,
			R.drawable.panda, R.drawable.pingu, R.drawable.racoon};

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

	public String getScreenshotSceneName(File projectDir) {
		FilenameFilter filter = new FilenameFilter() {

			public boolean accept(File f, String name) {
				return name.endsWith(DEFAULT_IMAGE_EXTENSION);
			}
		};
		File[] projectScreenshots = projectDir.listFiles(filter);
		if (projectScreenshots == null || projectScreenshots.length != 0) {
			return null;
		}
		List<File> screenshots = new ArrayList<>();
		for (File scene : projectDir.listFiles()) {
			File[] sceneScreenshots = scene.listFiles(filter);
			if (sceneScreenshots != null && sceneScreenshots.length > 0) {
				screenshots.addAll(Arrays.asList(sceneScreenshots));
			}
		}
		if (screenshots.isEmpty()) {
			return null;
		}
		Collections.sort(screenshots, new Comparator<File>() {
			@Override
			public int compare(File screenshot2, File screenshot1) {
				return Long.compare(screenshot1.lastModified(), screenshot2.lastModified());
			}
		});
		for (File screenshot : screenshots) {
			if (screenshot.getName().equals(SCREENSHOT_MANUAL_FILE_NAME)) {
				return screenshot.getParentFile().getName();
			}
		}
		return screenshots.get(0).getParentFile().getName();
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
						ImageEditing.ResizeType.FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
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
				if (!automaticScreenShotFile.exists()) {
					int random = new Random().nextInt(placeholderImages.length);
					try {
						final ProjectManager projectManager = inject(ProjectManager.class).getValue();
						ResourceImporter.createImageFileFromResourcesInDirectory(
								projectManager.getApplicationContext().getResources(),
								placeholderImages[random],
								projectDir,
								SCREENSHOT_AUTOMATIC_FILE_NAME,
								1);
					} catch (IOException e) {
						Log.e(ProjectAndSceneScreenshotLoader.class.getSimpleName(),
								"Cannot create placeholder image for project" + projectDir.getAbsolutePath(), e);
					}
					automaticScreenShotFile = new File(projectDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
				}
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
