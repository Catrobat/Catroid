package at.tugraz.ist.catroid.test.web;

import java.io.File;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.tasks.ProjectUploadTask;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;
import at.tugraz.ist.catroid.utils.UtilFile;

public class UpAndDownloadTest extends AndroidTestCase {

	public UpAndDownloadTest() {
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

	public void testUpAndDownload() throws Throwable {
//		String testProjectName = "UpAndDownloadTest"+System.currentTimeMillis();
//		
//		ProjectUploadTask uploadTask = new ProjectUploadTask(null, testProjectName,ConstructionSiteActivity.DEFAULT_ROOT, 
//					ConstructionSiteActivity.TMP_PATH+"/tmp.zip");
//		
//		uploadTask.execute();		
//		Thread.sleep(8000);
//		String projectDownloadUrl = uploadTask.getResultString();
//		
//		assertTrue("No download url returned from the server. Connected to the Internt?", projectDownloadUrl != null && projectDownloadUrl.length() > 0);
//		
//		
//		new ProjectDownloadTask(null, projectDownloadUrl, testProjectName,
//					ConstructionSiteActivity.TMP_PATH+"/down.zip").execute();
//		Thread.sleep(8000);
//		
//		File downloadProjectRoot = new File(ConstructionSiteActivity.DEFAULT_ROOT + "/"+testProjectName+"/");
//		assertTrue("Download Project is not available.", downloadProjectRoot.exists());
//		
//		boolean spfFilePresent = false;
//		String[] projectFiles = downloadProjectRoot.list();
//		for (String fileName : projectFiles) {
//			if(fileName.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
//				spfFilePresent = true;
//		}
//		
//		assertTrue("No project file available.", spfFilePresent);
//		UtilFile.deleteDirectory(downloadProjectRoot);
	}

}