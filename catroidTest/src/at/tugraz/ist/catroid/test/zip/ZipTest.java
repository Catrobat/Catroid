package at.tugraz.ist.catroid.test.zip;

import java.io.File;
import java.io.FilenameFilter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.utils.ImageContainer;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.UtilZip;

public class ZipTest extends AndroidTestCase {

	public ZipTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Throwable {
	}

	public void testZipUnzip() throws Throwable {
		File dirPath = new File(ConstructionSiteActivity.DEFAULT_ROOT);
		String[] pathes = dirPath.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				if(filename.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING) || filename.equalsIgnoreCase("images")
						|| filename.equalsIgnoreCase("sounds"))
					return true;
				return false;
			}
		});
		if(pathes == null) {
			assertFalse("no default project", true);
			return;
		}
		for(int i=0;i<pathes.length;++i) {
			pathes[i] = dirPath +"/"+ pathes[i];
		}	
		
		String zipfile = ConstructionSiteActivity.TMP_PATH+"/testzip.zip";
		File file = new File(zipfile);
    	if(!file.exists()) {
    		file.getParentFile().mkdirs();
    		file.createNewFile();
    	}
    	
		if (!UtilZip.writeToZipFile(pathes, zipfile)) {
			file.delete();
			assertFalse("zip failed", true);
			return;
		}
		
		if (!UtilZip.unZipFile(zipfile, ConstructionSiteActivity.TMP_PATH+"/")) {
			file.delete();
			assertFalse("unzip failed", true);
			return;
		}
		
		//file.delete();
		assertTrue(true);
		
	}

}