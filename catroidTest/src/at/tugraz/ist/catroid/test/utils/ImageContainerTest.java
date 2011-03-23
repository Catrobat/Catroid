//package at.tugraz.ist.catroid.test.utils;
//
//import java.io.File;
//
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.test.AndroidTestCase;
//import at.tugraz.ist.catroid.utils.ImageContainer;
//import at.tugraz.ist.catroid.utils.Utils;
//
//public class ImageContainerTest extends AndroidTestCase {
//
//	private ImageContainer mImageContainer;
//
//	public ImageContainerTest() {
//		super();
//	}
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		mImageContainer = ImageContainer.getInstance();
//		mImageContainer.setRootPath("/sdcard/");
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	public void testInit() throws Throwable {
//	}
//
//	public void testSaveImageGetImage() throws Throwable {
//		Bitmap bitmap1 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
//		Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
//		Utils.saveBitmapOnSDCardAsPNG("/sdcard/test2.png", bitmap2);
//		mImageContainer.saveImageFromPath("/sdcard/test2.png", null);
//		Thread.sleep(500); // wait for save thread to finish
//		mImageContainer.deleteImage("test1.png");
//
//		assertEquals(bitmap1.getNinePatchChunk(), mImageContainer.getImage("test2.png").getNinePatchChunk());
//
//		boolean exists = false;
//		File rootFile = new File("/sdcard");
//		String[] rootFileList = rootFile.list();
//
//		for (int i = 0; i < rootFileList.length; i++) {
//			if (rootFileList[i].contains("test2.png")) {
//				exists = true;
//			}
//
//		}
//		assertTrue(exists);
//		mImageContainer.deleteImage("test2.png");
//	}
//
//	public void testSaveImageOnSDCardAsPNG() throws Throwable {
//		Bitmap bitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
//		File picture_path = new File("/sdcard/test1.png");
//		Utils.saveBitmapOnSDCardAsPNG(picture_path.getAbsolutePath(), bitmap);
//
//		boolean exists = false;
//		File rootFile = new File("/sdcard");
//		String[] rootFileList = rootFile.list();
//
//		for (int i = 0; i < rootFileList.length; i++) {
//			if (rootFileList[i].contains("test1.png")) {
//				exists = true;
//			}
//
//		}
//		assertTrue(exists);
//		mImageContainer.deleteImage("test1.png");
//	}
//
//}