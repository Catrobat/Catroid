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
// this class is based on: https://github.com/fhucho/simple-disk-cache (Apache 2.0 License)

package org.catrobat.catroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

import org.catrobat.catroid.common.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpiringDiskCache {

	public static final String EXPIRATION_DATE_KEY = "expirationDate";

	private static final String TAG = ExpiringDiskCache.class.getSimpleName();
	private static final int APP_VERSION = 1;
	private static final long EXPIRE_TIME = Constants.DISK_IMAGE_CACHE_EXPIRE_TIME;
	private static final long CACHE_SIZE = Constants.DISK_IMAGE_CACHE_MAX_SIZE;
	private static final int VALUE_IDX = 0;
	private static final int METADATA_IDX = 1;
	private static final List<File> usedDirs = new ArrayList<>();
	private static ExpiringDiskCache instance = null; // singleton

	private final int appVersion;
	private final long expireTime;
	private final Object diskCacheLock = new Object();
	private boolean diskCacheStarting = true;
	private final long diskCacheSize;
	private DiskCacheBackendInterface diskCache;
	private ClockInterface clock;

	public interface ClockInterface {
		long elapsedRealtime();
	} // unit testing

	private ExpiringDiskCache(final Context context, final int appVersion, final long diskCacheSize,
			final long expireTime, final DiskCacheBackendInterface diskCache,
			final ClockInterface clock) {
		this.appVersion = appVersion;
		this.diskCacheSize = diskCacheSize;
		this.expireTime = expireTime;
		this.diskCache = diskCache;
		this.clock = clock;

		if (clock == null) {
			this.clock = new ClockInterface() {
				@Override
				public long elapsedRealtime() {
					return SystemClock.elapsedRealtime();
				}
			};
		}

		if (diskCache == null) { // lazily create cache backend!
			File diskCacheDir = context.getCacheDir();
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				diskCacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "ScratchProjectImagesCache");
			}
			if (usedDirs.contains(diskCacheDir)) {
				throw new IllegalStateException("Cache directory " + diskCacheDir.getAbsolutePath() + " was used before.");
			}
			usedDirs.add(diskCacheDir);
			if (!diskCacheDir.exists()) {
				if (!diskCacheDir.mkdirs()) {
					throw new IllegalStateException("Unable to create cache directory "
							+ diskCacheDir.getAbsolutePath());
				}
			}
			new InitDiskCacheTask().execute(diskCacheDir);
		} else {
			diskCacheStarting = false; // finished initialization
		}
	}

	@Nullable
	public static ExpiringDiskCache getInstance(final Context context) {
		Preconditions.checkArgument(context != null, "You have to set context before you call getInstance!");
		if (instance == null) {
			// do it in a thread safe way
			synchronized (ExpiringDiskCache.class) {
				if (instance == null) {
					try {
						instance = new ExpiringDiskCache(context, APP_VERSION, CACHE_SIZE, EXPIRE_TIME, null, null);
					} catch (Exception ex) {
						Log.w(TAG, "Unable to create disk cache!");
						return null;
					}
				}
			}
		}
		return instance;
	}

	private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
		@Override
		protected Void doInBackground(File... params) {
			synchronized (diskCacheLock) {
				File cacheDir = params[0];
				try {
					diskCache = DiskLruCache.open(cacheDir, appVersion, 2, diskCacheSize);
					diskCacheStarting = false; // finished initialization
					diskCacheLock.notifyAll(); // wake any waiting threads
				} catch (IOException exception) {
					Log.e(TAG, "Unable to create LruDiskCache!");
				}
			}
			return null;
		}
	}

	public void clear() throws IOException {
		synchronized (diskCacheLock) {
			if (diskCache != null) {
				File dir = diskCache.getDirectory();
				long maxSize = diskCache.maxSize();
				diskCache.delete();
				diskCache = DiskLruCache.open(dir, appVersion, 2, maxSize);
			}
		}
	}

	public boolean contains(String key) throws IOException {
		return (get(key) != null);
	}

	public InputStreamEntry get(String key) throws IOException {
		synchronized (diskCacheLock) {
			while (diskCacheStarting) { // wait while disk cache is started from background thread
				try {
					diskCacheLock.wait();
				} catch (InterruptedException e) { /* Nothing to do */ }
			}
			if (diskCache == null) {
				return null;
			}
			String hashedKey = Utils.md5Checksum(key);
			DiskCacheSnapshotInterface snapshot = diskCache.get(hashedKey);
			if (snapshot == null) {
				return null;
			}

			Map<String, Serializable> metaData = readMetadata(snapshot);
			Serializable expirationDate = metaData.get(EXPIRATION_DATE_KEY);
			if (expirationDate == null) {
				return null;
			}

			long expDate = (Long) expirationDate;
			if (clock.elapsedRealtime() >= expDate) {
				diskCache.remove(hashedKey);
				return null;
			}

			return new InputStreamEntry(snapshot, metaData);
		}
	}

	public BitmapEntry getBitmap(String key) throws IOException {
		InputStreamEntry entry = get(key);
		if (entry == null) {
			return null;
		}

		try {
			Bitmap bitmap = BitmapFactory.decodeStream(entry.getInputStream());
			Map<String, Serializable> metaData = entry.getMetadata();
			Log.d(TAG, "Expiration date: " + metaData.get(EXPIRATION_DATE_KEY));
			return new BitmapEntry(bitmap, metaData);
		} finally {
			entry.close();
		}
	}

	public boolean remove(String key) throws IOException {
		synchronized (diskCacheLock) {
			String hashedKey = Utils.md5Checksum(key);
			return (diskCache != null) && (diskCache.get(hashedKey) != null) && diskCache.remove(hashedKey);
		}
	}

	public void put(String key, InputStream is, Map<String, Serializable> annotations) throws IOException {
		synchronized (diskCacheLock) {
			String hashedKey = Utils.md5Checksum(key);
			if (diskCache != null && diskCache.get(hashedKey) == null) {
				OutputStream os = null;
				try {
					annotations.put(ExpiringDiskCache.EXPIRATION_DATE_KEY, clock.elapsedRealtime() + expireTime);
					os = openStream(hashedKey, annotations);
					ByteStreams.copy(is, os);
				} finally {
					DiskLruCache.closeQuietly(os);
				}
			}
		}
	}

	public void put(String key, InputStream is) throws IOException {
		put(key, is, new HashMap<String, Serializable>());
	}

    /* Uncomment this and use these methods if you want to enable direct support for strings as well!
	   And don't forget to write tests!!
    public void put(String key, String value, Map<String, Serializable> annotations) throws IOException {
        InputStream is = new ByteArrayInputStream(value.getBytes());
        try {
            put(key, is, annotations);
        } finally {
            DiskLruCache.closeQuietly(is);
        }
    }

    public void put(String key, String value) throws IOException {
        put(key, value, new HashMap<String, Serializable>());
    }*/

	private OutputStream openStream(String hashedKey, Map<String, ? extends Serializable> metadata) throws IOException {
		DiskCacheEntryEditorInterface editor = diskCache.edit(hashedKey);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(METADATA_IDX)));
			oos.writeObject(metadata);
			BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(VALUE_IDX));
			return new CacheOutputStream(bos, editor);
		} catch (IOException e) {
			editor.abort();
			throw e;
		} finally {
			DiskLruCache.closeQuietly(oos);
		}
	}

	private Map<String, Serializable> readMetadata(DiskCacheSnapshotInterface snapshot)
			throws IOException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					snapshot.getInputStream(METADATA_IDX)));
			@SuppressWarnings("unchecked")
			Map<String, Serializable> annotations = (Map<String, Serializable>) ois.readObject();
			return annotations;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			DiskLruCache.closeQuietly(ois);
		}
	}

	public static class InputStreamEntry {
		private final DiskCacheSnapshotInterface snapshot;
		private final Map<String, Serializable> metadata;

		public InputStreamEntry(DiskCacheSnapshotInterface snapshot, Map<String, Serializable> metadata) {
			this.metadata = metadata;
			this.snapshot = snapshot;
		}

		public InputStream getInputStream() {
			return snapshot.getInputStream(VALUE_IDX);
		}

		public Map<String, Serializable> getMetadata() {
			return metadata;
		}

		public void close() {
			snapshot.close();
		}
	}

	public static class BitmapEntry {
		private final Bitmap bitmap;
		private final Map<String, Serializable> metadata;

		public BitmapEntry(Bitmap bitmap, Map<String, Serializable> metadata) {
			this.bitmap = bitmap;
			this.metadata = metadata;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public Map<String, Serializable> getMetadata() {
			return metadata;
		}
	}

	private class CacheOutputStream extends FilterOutputStream {
		private final DiskCacheEntryEditorInterface editor;
		private boolean failed = false;

		private CacheOutputStream(OutputStream os, DiskCacheEntryEditorInterface editor) {
			super(os);
			this.editor = editor;
		}

		@Override
		public void close() throws IOException {
			IOException closeException = null;
			try {
				super.close();
			} catch (IOException e) {
				closeException = e;
			}
			if (failed) {
				editor.abort();
			} else {
				editor.commit();
			}
			if (closeException != null) {
				throw closeException;
			}
		}

		@Override
		public void flush() throws IOException {
			try {
				super.flush();
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(int oneByte) throws IOException {
			try {
				super.write(oneByte);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(@NonNull byte[] buffer) throws IOException {
			try {
				super.write(buffer);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}

		@Override
		public void write(@NonNull byte[] buffer, int offset, int length) throws IOException {
			try {
				super.write(buffer, offset, length);
			} catch (IOException e) {
				failed = true;
				throw e;
			}
		}
	}
}
