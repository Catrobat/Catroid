package at.tugraz.ist.catroid.test.web;

import java.io.File;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.utils.UtilZip;

public class ZipTest extends AndroidTestCase {

	public ZipTest() {
		super();
	}

	@Override
    protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
    protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Throwable {
	}

	public void testZipUnzip() throws Throwable {
				
        String pathToTest = Consts.TMP_PATH + "/test1/";
		
		File testfile = new File(pathToTest+"test2/testfile.txt");
		testfile.getParentFile().mkdirs();
		testfile.createNewFile();
		
		String[] pathes = {pathToTest};
			
        String zipFileName = Consts.TMP_PATH + "/testzip.zip";
		File zipFile = new File(zipFileName);
    	if(zipFile.exists()) {
            zipFile.delete();
        }
    	
		zipFile.getParentFile().mkdirs();
		zipFile.createNewFile();
    	
		if (!UtilZip.writeToZipFile(pathes, zipFileName)) {
			zipFile.delete();
			assertFalse("zip failed", true);
			return;
		}
		testfile.delete();
		testfile.getParentFile().delete();
		
        if (!UtilZip.unZipFile(zipFileName, Consts.TMP_PATH + "/")) {
			zipFile.delete();
			assertFalse("unzip failed", true);  
			return;     
		}
		
		File checkfile = new File(pathToTest+"/test2/testfile.txt");
		
		assertTrue("File was not recreated from zip.", checkfile.exists());
		
		zipFile.delete();
		
	}

}