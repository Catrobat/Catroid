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
package at.tugraz.ist.catroid.test.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;

public class XMLValidationUtil {
	private static final String XML_VALIDATING_URL = "http://catroidtestserver.ist.tugraz.at/xmlSchema/validateXml.php";
	private static final String LOG_TAG = XMLValidationUtil.class.getSimpleName();

	public static void sendProjectXMLToServerForValidating(Project projectToValidate) throws IOException, JSONException {
		String projectName = projectToValidate.getName();
		String fullPathFilename = Utils.buildPath(Utils.buildProjectPath(projectName), Consts.PROJECTCODE_NAME);
		sendProjectXMLToServerForValidating(fullPathFilename);
	}

	public static void sendProjectXMLToServerForValidating(String fullPathFilename) throws IOException, JSONException {

		String xmlContent = readTextFile(fullPathFilename);

		HashMap<String, String> postValues = new HashMap<String, String>();
		postValues.put("xmlToValidate", xmlContent);

		ConnectionWrapper connection = new ConnectionWrapper();
		String responce = connection.doHttpPost(XML_VALIDATING_URL, postValues);

		JSONObject jsonResponce = new JSONObject(responce);
		Log.i(LOG_TAG, "json responce: " + jsonResponce.toString());
		boolean valid = jsonResponce.getBoolean("valid");
		String message = jsonResponce.optString("message");

		Assert.assertTrue(message, valid);
	}

	private static String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(fullPathFilename));
		String text = null;

		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
		}
		reader.close();

		return contents.toString();

	}
}
