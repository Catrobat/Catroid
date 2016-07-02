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

package org.catrobat.catroid.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.io.Closeables;

import org.catrobat.catroid.web.ServerCalls;

public class WebImageLoader {

	private static final String TAG = WebImageLoader.class.getSimpleName();
	//private static final int PLACEHOLDER_IMAGE_RESOURCE = R.drawable.ic_launcher;
	private static final int MAX_NUM_OF_RETRIES = 2;
	private static final int MIN_DELAY = 1_000; // in ms

	private ExpiringLruMemoryImageCache memoryCache;
	private ExpiringDiskCache diskCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;

	public WebImageLoader(final ExpiringLruMemoryImageCache memoryCache,
			final ExpiringDiskCache fileCache, final ExecutorService executorService) {
		this.memoryCache = memoryCache;
		this.diskCache = fileCache;
		this.executorService = executorService;
	}

	private class ImageToLoad {
		public String url;
		public ImageView imageView;

		public ImageToLoad(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
		}
	}

	private class BitmapDisplayer implements Runnable {
		public Bitmap bitmap;
		public ImageToLoad imageToLoad;

		public BitmapDisplayer(Bitmap bitmap, ImageToLoad imageToLoad) {
			this.bitmap = bitmap;
			this.imageToLoad = imageToLoad;
		}

		public void run() {
			if (!Looper.getMainLooper().equals(Looper.myLooper())) {
				throw new AssertionError("You should not change the UI from any thread "
						+ "except UI thread!");
			}

			if (imageViewReused(imageToLoad)) {
				Log.d(TAG, "REUSED!");
				return;
			}
			if (bitmap != null) {
				Log.d(TAG, "Bitmap given!");
				imageToLoad.imageView.setImageBitmap(bitmap);
			} else {
				Log.d(TAG, "Bitmap NOT given!");
				imageToLoad.imageView.setImageBitmap(null);//.setImageResource(PLACEHOLDER_IMAGE_RESOURCE);
			}
		}
	}

	public void fetchAndShowImage(String url, ImageView imageView, int width, int height) {
		Log.d(TAG, "Look-up for image with URL: " + url);
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			Log.d(TAG, "Memory cache hit for: " + url);
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(null);//.setImageResource(PLACEHOLDER_IMAGE_RESOURCE);
			// enqueue image
			executorService.submit(new ImageLoader(new ImageToLoad(url, imageView), width, height));
		}
	}

	@Nullable
	private Bitmap decodeLoadedBitmap(byte[] byteArray, int width, int height) {
		Log.d(TAG, "Trying to decode file (width: " + width + ", height: " + height + ")");
		if (byteArray == null) {
			return null;
		}
		return ImageEditing.getScaledBitmapOfLoadedBitmap(byteArray, width, height,
				ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
	}

	boolean imageViewReused(ImageToLoad imageToLoad) {
		String tag = imageViews.get(imageToLoad.imageView);
		return tag == null || (!tag.equals(imageToLoad.url));
	}

	class ImageLoader implements Runnable {

		private int width;
		private int height;
		private ImageToLoad imageToLoad;

		ImageLoader(final ImageToLoad imageToLoad, final int width, final int height) {
			this.imageToLoad = imageToLoad;
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			if (imageViewReused(imageToLoad)) {
				return;
			}

			ExpiringDiskCache.BitmapEntry entry = null;
			if (diskCache != null) {
				try {
					entry = diskCache.getBitmap(imageToLoad.url);
				} catch (Throwable e) {
					Log.w(TAG, "Disk Cache not accessible");
					Log.w(TAG, e);
				}
			}

			Bitmap bitmap;
			if (entry == null || entry.getBitmap() == null) {
				Log.i(TAG, "NO Disk cache hit for: " + imageToLoad.url);
				if (entry != null) {
					Log.i(TAG, "BUT entry given for: " + imageToLoad.url);
				}
				byte[] imageData = fetchFileDataFromWeb(imageToLoad.url);
				if (imageData == null) {
					Log.d(TAG, "Cannot fetch file from web: " + imageToLoad.url);
					return; // give up!
				}

				// resize image!
				bitmap = decodeLoadedBitmap(imageData, width, height);
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
				if (byteArrayInputStream == null) {
					Log.e(TAG, "Unable to convert byte data to InputStream. This should never happen!");
					return; // give up!
				}

				ByteArrayOutputStream byteArrayOutputStream = null;
				try {
					byteArrayOutputStream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
					imageData = byteArrayOutputStream.toByteArray();
					Closeables.closeQuietly(byteArrayInputStream);
					byteArrayInputStream = new ByteArrayInputStream(imageData);

					if (diskCache != null) {
						diskCache.put(imageToLoad.url, byteArrayInputStream);
						Closeables.closeQuietly(byteArrayInputStream);
						byteArrayInputStream = new ByteArrayInputStream(imageData);
						Log.i(TAG, "Stored to disk cache for: " + imageToLoad.url);
					}
					bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
					if (bitmap == null) {
						return; // give up!
					}
				} catch (Throwable e) {
					Log.w(TAG, "Disk Cache not writeable!");
					Log.w(TAG, e);
				} finally {
					Closeables.closeQuietly(byteArrayInputStream);
					if (byteArrayOutputStream != null) {
						try {
							byteArrayOutputStream.close();
						} catch (IOException ex) {
						}
					}
				}
			} else {
				bitmap = entry.getBitmap();
				Log.i(TAG, "Disk cache hit for: " + imageToLoad.url);
			}
			memoryCache.put(imageToLoad.url, bitmap);
			if (imageViewReused(imageToLoad)) {
				return;
			}
			BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, imageToLoad);
			((Activity) imageToLoad.imageView.getContext()).runOnUiThread(bitmapDisplayer);
		}

		@Nullable
		private byte[] fetchFileDataFromWeb(final String url) {
			Log.d(TAG, "Downloading image from URL: " + url);
			if (url == null) {
				return null;
			}

			// exponential backoff
			for (int attempt = 0; attempt <= MAX_NUM_OF_RETRIES; attempt++) {
				try {
					return ServerCalls.getInstance().downloadFile(url);
				} catch (Throwable exception) {
					Log.d(TAG, exception.getLocalizedMessage() + "\n" + exception.getStackTrace());
					int delay = MIN_DELAY + (int) (MIN_DELAY * Math.random() * (attempt + 1));
					Log.i(TAG, "Retry #" + (attempt + 1) + " to fetch scratch project list scheduled in "
							+ delay + " ms due to " + exception.getLocalizedMessage());
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
					}
				}
			}
			Log.w(TAG, "Maximum number of " + (MAX_NUM_OF_RETRIES + 1)
					+ " attempts exceeded! Server not reachable?!");
			return null;
		}
	}
}
