package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.filesystem.FileSystem;

/**
 * 
 * @author AlexanderKalchauer This class contains Images in different sizes for
 *         the Images in the root folder to use in scratch
 */

public class ImageContainer {

	private static int MAX_WIDTH;
	private static int MAX_HEIGHT;
	private static final int THUMBNAIL_WIDTH = 100;
	private static final int THUMBNAIL_HEIGHT = 100;
	static String rootpath;
	private HashMap<String, Bitmap> mImageMap;
	private FileSystem mFilesystem;
	private static ImageContainer mImageContainer = null;

	private ImageContainer() {
		mImageMap = new HashMap<String, Bitmap>();
		mFilesystem = new FileSystem();
		MAX_WIDTH = ConstructionSiteActivity.SCREEN_WIDTH * 1;
		MAX_HEIGHT = ConstructionSiteActivity.SCREEN_HEIGHT * 1;
	}

	public static ImageContainer getInstance() {
		if (mImageContainer == null) {
			mImageContainer = new ImageContainer();
		}
		return mImageContainer;
	}

	public void setRootPath(String rootpath) {
		ImageContainer.rootpath = rootpath;
	}

	public String saveImageFromPath(String path, Context context) {
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis()
				+ imagePath.getAbsolutePath().replace(folderPath, "")
						.replace("/", "");

		Bitmap bm = ImageEditing.getScaledBitmap(path, MAX_WIDTH, MAX_HEIGHT);
		
		String imageName = saveImageFromBitmap(bm, image, true, context);

		return imageName;
	}

	private ProgressDialog createProgressDialog(Context context) {
		if (context == null)
			return null;

		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading);
		ProgressDialog progressDialog = ProgressDialog.show(context, title,
				message);
		return progressDialog;
	}

	public String saveImageFromBitmap(final Bitmap bm, String name,
			final boolean recycle, Context context) {
		final ProgressDialog progressDialog = createProgressDialog(context);
		final String fileName = Utils.changeFileEndingToPng(name);
		new Thread(new Runnable() {
			public void run() {

				final String path = Utils.concatPaths(
						ConstructionSiteActivity.ROOT_IMAGES, fileName);

				Utils.saveBitmapOnSDCardAsPNG(path, bm);

				if (recycle)
					bm.recycle();

				if (progressDialog != null)
					progressDialog.dismiss();
			}
		}).start();

		return fileName;
	}

	public String saveThumbnailFromPath(String path, Context context) {
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis()
				+ imagePath.getAbsolutePath().replace(folderPath, "")
						.replace("/", "thumb");

		Bitmap bm = ImageEditing.getScaledBitmap(path, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
	
		return saveThumbnailFromBitmap(bm, image, true, context);
	}

	public String saveThumbnailFromBitmap(Bitmap bm, String name,
			boolean recycle, Context context) {
		name = Utils.changeFileEndingToPng(name);
		Bitmap newbm = null;
		newbm = ImageEditing.scaleBitmap(bm, THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
		mImageMap.put(name, newbm);

		final String path = Utils.concatPaths(
				ConstructionSiteActivity.ROOT_IMAGES, name);
		final Bitmap newbmToSave = newbm;
		new Thread(new Runnable() {

			public void run() {
				Utils.saveBitmapOnSDCardAsPNG(path, newbmToSave);
			}
		}).start();

		if (bm != null && bm != newbm && recycle)
			bm.recycle();

		return name;
	}

	public Bitmap getImage(String name) {
		if (!mImageMap.containsKey(name)) {
			Bitmap bm = BitmapFactory.decodeFile(getFullImagePath(name));
			if (bm != null)
				mImageMap.put(name, bm);
		}

		return mImageMap.get(name);
	}

	private String getFullImagePath(String path) {
		return (Utils.concatPaths(rootpath, path));
	}

	public void deleteImage(String name) {
		mFilesystem.deleteFile(getFullImagePath(name), null);
		mImageMap.remove(name);
	}

	public void deleteAll() {
		mImageMap.clear();
	}

}
