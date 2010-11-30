package at.tugraz.ist.catroid.test.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.tasks.ProjectUploadTask;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UpAndDownloadTest extends AndroidTestCase {

	private MockConnection mMockConnection;
	
	public UpAndDownloadTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		// generate mock object
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Throwable {
	}

	public void testUpAndDownload() throws Throwable {
		String testProjectName = "UpAndDownloadTest"+System.currentTimeMillis();
		
		ProjectUploadTask uploadTask = new ProjectUploadTask(null, testProjectName,
				ConstructionSiteActivity.DEFAULT_ROOT+"/defaultSaveFile", 
				ConstructionSiteActivity.TMP_PATH+"/tmp.zip") {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};
		ProjectDownloadTask downloadTask = new ProjectDownloadTask(null, 
				"", testProjectName, ConstructionSiteActivity.TMP_PATH+"/down.zip") {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};
		
		uploadTask.execute();		
		//Thread.sleep(8000);
		
		
		downloadTask.execute();
		//Thread.sleep(8000);
		
		File downloadProjectRoot = new File(ConstructionSiteActivity.DEFAULT_ROOT + "/"+testProjectName+"/");
		//assertTrue("Download Project is not available.", downloadProjectRoot.exists());
		
		boolean spfFilePresent = false;
//		String[] projectFiles = downloadProjectRoot.list();
//		for (String fileName : projectFiles) {
//			if(fileName.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
//				spfFilePresent = true;
//		}
//		
//		assertTrue("No project file available.", spfFilePresent);
//		UtilFile.deleteDirectory(downloadProjectRoot);
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doHttpPostFileUpload(String urlstring,
				HashMap<String, String> postValues, String filetag,
				String filePath) throws IOException, WebconnectionException {
			
			return "";
		}
		@Override
		public void doHttpPostFileDownload(String urlstring,
				HashMap<String, String> postValues, String filePath)
				throws IOException {
			
		}
	}
}