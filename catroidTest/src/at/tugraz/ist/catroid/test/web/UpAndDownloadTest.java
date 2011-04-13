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
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.constructionSite.tasks.ProjectUploadTask;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UpAndDownloadTest extends AndroidTestCase {

	private MockConnection mMockConnection;
	private File mProjectZipOnMockServer;

	public UpAndDownloadTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mProjectZipOnMockServer = new File(Consts.TMP_PATH + "/projectSave.zip");
		mMockConnection = new MockConnection();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Throwable {
	}

	public void testUpAndDownload() throws Throwable {
		String testProjectName = "UpAndDownloadTest" + System.currentTimeMillis();
		String pathToDefaultProject = Consts.DEFAULT_ROOT + "/uploadtestProject";
		new File(pathToDefaultProject).mkdirs();
		String spfFilename = "test" + Consts.PROJECT_EXTENTION;
		new File(pathToDefaultProject + "/" + spfFilename).createNewFile();
		String projectDescription = "this is just a testproject";

		ProjectUploadTask uploadTask = new ProjectUploadTask(null, testProjectName, projectDescription, pathToDefaultProject) {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};

		ProjectDownloadTask downloadTask = new ProjectDownloadTask(null, "", testProjectName, Consts.TMP_PATH
				+ "/down.zip") {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};

		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		uploadTask.execute();
		Thread.sleep(3000);

		assertTrue("uploaded file does not exist", mProjectZipOnMockServer.exists());

		downloadTask.execute();
		Thread.sleep(3000);

		File downloadProjectRoot = new File(Consts.DEFAULT_ROOT + "/" + testProjectName);
		assertTrue("project does not exist after download", downloadProjectRoot.exists());
		File testSPFFile = new File(Consts.DEFAULT_ROOT + "/" + testProjectName + "/" + spfFilename);
		assertTrue("spf file does not exist after download", testSPFFile.exists());

		UtilFile.deleteDirectory(downloadProjectRoot);
		UtilFile.deleteDirectory(new File(pathToDefaultProject));
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doHttpPostFileUpload(String urlstring, HashMap<String, String> postValues, String filetag,
				String filePath) throws IOException, WebconnectionException {

			new File(filePath).renameTo(mProjectZipOnMockServer);
			return "";
		}

		@Override
		public void doHttpPostFileDownload(String urlstring, HashMap<String, String> postValues, String filePath)
		throws IOException {
			mProjectZipOnMockServer.renameTo(new File(filePath));
		}
	}
}