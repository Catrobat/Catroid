/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.transfers.ProjectDownloadTask;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UpAndDownloadTest extends AndroidTestCase {

	private File projectZipOnMockServer;

	public UpAndDownloadTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		projectZipOnMockServer = new File(Consts.TMP_PATH + "/projectSave" + Consts.CATROID_EXTENTION);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject("uploadtestProject");
		super.tearDown();
	}

	public void testUpAndDownload() throws Throwable {
		String testProjectName = "UpAndDownloadTest" + System.currentTimeMillis();
		String pathToDefaultProject = Consts.DEFAULT_ROOT + "/uploadtestProject";
		new File(pathToDefaultProject).mkdirs();
		String projectFilename = "test" + Consts.PROJECT_EXTENTION;
		new File(pathToDefaultProject + "/" + projectFilename).createNewFile();
		String projectDescription = "this is just a testproject";

		//		ProjectUploadTask uploadTask = new ProjectUploadTask(null, testProjectName, projectDescription,
		//				pathToDefaultProject, null) {
		//			@Override
		//			protected ConnectionWrapper createConnection() {
		//				return mockConnection;
		//			}
		//		};
		//
		//		ProjectDownloadTask downloadTask = new ProjectDownloadTask(null, "", testProjectName, Consts.TMP_PATH + "/down"
		//				+ Consts.CATROID_EXTENTION) {
		//			@Override
		//			protected ConnectionWrapper createConnection() {
		//				return mockConnection;
		//			}
		//		};

		ServerCalls.getInstance().setConnectionToUse(new MockConnection());

		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		new ProjectUploadTask(null, testProjectName, projectDescription, pathToDefaultProject, "0").execute();
		Thread.sleep(3000);

		assertTrue("Uploaded file does not exist", projectZipOnMockServer.exists());

		new ProjectDownloadTask(null, "", testProjectName).execute();
		Thread.sleep(3000);

		File downloadProjectRoot = new File(Consts.DEFAULT_ROOT + "/" + testProjectName);
		assertTrue("Project does not exist after download", downloadProjectRoot.exists());
		File testProjectFile = new File(Consts.DEFAULT_ROOT + "/" + testProjectName + "/" + projectFilename);
		assertTrue("Project file does not exist after download", testProjectFile.exists());

		UtilFile.deleteDirectory(downloadProjectRoot);
		UtilFile.deleteDirectory(new File(pathToDefaultProject));
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doHttpPostFileUpload(String urlstring, HashMap<String, String> postValues, String filetag,
				String filePath) throws IOException, WebconnectionException {

			new File(filePath).renameTo(projectZipOnMockServer);
			return "";
		}

		@Override
		public void doHttpPostFileDownload(String urlstring, HashMap<String, String> postValues, String filePath)
				throws IOException {
			projectZipOnMockServer.renameTo(new File(filePath));
		}
	}

}