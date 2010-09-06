package at.tugraz.ist.s2a.test.utils;


import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import at.tugraz.ist.s2a.utils.ImageContainer;
import at.tugraz.ist.s2a.utils.Utils;
import junit.framework.TestCase;

public class ImageContainerTest extends TestCase {
	
	private ImageContainer mImageContainer;
	
	public ImageContainerTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mImageContainer = new ImageContainer("/sdcard/");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testInit() throws Throwable{
	}
	public void testSaveImageGetImage() throws Throwable{
		Bitmap bitmap1 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		Utils.saveBitmapOnSDCardAsPNG("/sdcard/test2.png", bitmap2);
		mImageContainer.saveImage("/sdcard/test2.png");
		mImageContainer.deleteImage("test1.png");
		
		assertEquals(bitmap1.getNinePatchChunk(), mImageContainer.getImage("test2.png").getNinePatchChunk());
		
		boolean exists = false;
		File rootFile = new File("/sdcard");
		String[] rootFileList = rootFile.list();
		
		for(int i=0; i<rootFileList.length; i++)
    	{
    		if(rootFileList[i].contains("test2.png")){
    			exists = true;
    		}
    			
    	}
		assertTrue(exists);
		mImageContainer.deleteImage("test2.png");
	}
	public void testSaveImageOnSDCardAsPNG() throws Throwable{
		Bitmap bitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		File picture_path = new File("/sdcard/test1.png");
		Utils.saveBitmapOnSDCardAsPNG(picture_path.getAbsolutePath(), bitmap);
		
		
		boolean exists = false;
		File rootFile = new File("/sdcard");
		String[] rootFileList = rootFile.list();
		
		for(int i=0; i<rootFileList.length; i++)
    	{
    		if(rootFileList[i].contains("test1.png")){
    			exists = true;
    		}
    			
    	}
		assertTrue(exists);
		mImageContainer.deleteImage("test1.png");
	}
	
	
}