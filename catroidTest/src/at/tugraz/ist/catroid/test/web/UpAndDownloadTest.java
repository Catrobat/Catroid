/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.os.ResultReceiver;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UpAndDownloadTest extends AndroidTestCase {

	private File projectZipOnMockServer;

	public UpAndDownloadTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		projectZipOnMockServer = new File(Constants.TMP_PATH + "/projectSave" + Constants.CATROID_EXTENTION);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject("uploadtestProject");
		super.tearDown();
	}

	public void testUpAndDownloadWithService() throws Throwable {
		// service not ready now

		//		String testProjectName = "UpAndDownloadTest" + System.currentTimeMillis();
		//		String pathToDefaultProject = Constants.DEFAULT_ROOT + "/uploadtestProject";
		//		new File(pathToDefaultProject).mkdirs();
		//		String projectFilename = "test" + Constants.PROJECT_EXTENTION;
		//		new File(pathToDefaultProject + "/" + projectFilename).createNewFile();
		//		String projectDescription = "this is just a testproject";
		//
		//		ServerCalls.getInstance().setConnectionToUse(new MockConnection());
		//
		//		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		//
		//		TransferService service = new TransferService();
		//		boolean bindOk = service.bindToMarketBillingService();
		//		assertTrue("service binding failed. ", bindOk);
		//
		//		service.uploadRequest(testProjectName, projectDescription, pathToDefaultProject, "0");
		//
		//		Thread.sleep(5000);
		//
		//		//assertTrue("upload call failed", service.getLastCallOk);
	}

	public void testUpAndDownload() throws Throwable {
		//must be rewritten to test using the services
		/*
		 * String testProjectName = "UpAndDownloadTest" + System.currentTimeMillis();
		 * String pathToDefaultProject = Constants.DEFAULT_ROOT + "/uploadtestProject";
		 * new File(pathToDefaultProject).mkdirs();
		 * String projectFilename = Constants.PROJECTCODE_NAME;
		 * new File(pathToDefaultProject + "/" + projectFilename).createNewFile();
		 * String projectDescription = "this is just a testproject";
		 * 
		 * ServerCalls.getInstance().setConnectionToUse(new MockConnection());
		 * 
		 * assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		 * 
		 * new ProjectUploadService(null, testProjectName, projectDescription, pathToDefaultProject, "0").execute();
		 * Thread.sleep(3000);
		 * 
		 * assertTrue("Uploaded file does not exist", projectZipOnMockServer.exists());
		 * 
		 * new ProjectDownloadService(null, "", testProjectName).execute();
		 * Thread.sleep(3000);
		 * 
		 * File downloadProjectRoot = new File(Constants.DEFAULT_ROOT + "/" + testProjectName);
		 * assertTrue("Project does not exist after download", downloadProjectRoot.exists());
		 * File testProjectFile = new File(Constants.DEFAULT_ROOT + "/" + testProjectName + "/" + projectFilename);
		 * assertTrue("Project file does not exist after download", testProjectFile.exists());
		 * 
		 * UtilFile.deleteDirectory(downloadProjectRoot);
		 * UtilFile.deleteDirectory(new File(pathToDefaultProject));
		 */
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doFtpPostFileUpload(String urlstring, HashMap<String, String> postValues, String filetag,
				String filePath, ResultReceiver receiver, String httpPostUrl, Integer notificationId)
				throws IOException, WebconnectionException {

			new File(filePath).renameTo(projectZipOnMockServer);
			return "";
		}

		@Override
		public void doHttpPostFileDownload(String urlstring, HashMap<String, String> postValues, String filePath,
				ResultReceiver receiver, Integer notificationId, String projectName) throws IOException {
			projectZipOnMockServer.renameTo(new File(filePath));
		}
	}

}
