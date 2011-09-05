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
package at.tugraz.ist.catroid.service.requests;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.service.TransferService;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UploadRequest extends BillingRequest {

	private final static String TAG = UploadRequest.class.getName();

	private Context context;
	private String projectName;
	private String projectDescription;
	private String projectPath;
	private String token;

	public UploadRequest(TransferService transferService, String projectName, String projectDescription,
			String projectPath, String token) {
		super(transferService);
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		this.projectPath = projectPath;
		this.token = token;
		this.context = transferService;
	}

	@Override
	long run() throws WebconnectionException {
		//notifyManager.addDownloadNotification(edition_title, edition_id, page_count);

		File dirPath = new File(projectPath);
		String[] paths = dirPath.list();

		if (paths == null) {
			throw new WebconnectionException(WebconnectionException.ERROR_UNKNOWN);
		}

		for (int i = 0; i < paths.length; i++) {
			paths[i] = dirPath + "/" + paths[i];
		}

		String zipFileString = Consts.TMP_PATH + "/upload" + Consts.CATROID_EXTENTION;
		File zipFile = new File(zipFileString);

		try {
			if (!zipFile.exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
				throw new WebconnectionException(WebconnectionException.ERROR_UNKNOWN);
			}

			//String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
			String userEmail = UtilDeviceInfo.getUserEmail(context);
			String language = UtilDeviceInfo.getUserLanguageCode(context);

			ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString, userEmail,
					language, token);
			return 1;
		} catch (WebconnectionException e) {
			e.printStackTrace();
			//notifyManager.cancleDownloads();
		} catch (IOException e) {
			e.printStackTrace();
		}
		zipFile.delete();
		//stopSelf();
		return -1;
	}

}