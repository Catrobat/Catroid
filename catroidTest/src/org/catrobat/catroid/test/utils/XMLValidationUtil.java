/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utils;

import android.util.Log;

import junit.framework.Assert;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public final class XMLValidationUtil {
	// TODO: change to new XML schema
	private static final String XML_VALIDATING_URL = "http://catroid.org/CatrobatLanguage/xmlSchema/version-0.3/validateXmlVersion3.php";
	private static final String LOG_TAG = XMLValidationUtil.class.getSimpleName();

	// Suppress default constructor for noninstantiability
	private XMLValidationUtil() {
		throw new AssertionError();
	}

	public static void sendProjectXMLToServerForValidating(Project projectToValidate) throws IOException,
			JSONException, WebconnectionException {
		String projectName = projectToValidate.getName();
		String fullPathFilename = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.PROJECTCODE_NAME);
		sendProjectXMLToServerForValidating(fullPathFilename);
	}

	public static void sendProjectXMLToServerForValidating(String fullPathFilename) throws IOException, JSONException,
			WebconnectionException {

		String xmlContent = readTextFile(fullPathFilename);

		HashMap<String, String> postValues = new HashMap<String, String>();
		postValues.put("xmlToValidate", xmlContent);

		String response = ServerCalls.getInstance().httpFormUpload(XML_VALIDATING_URL, postValues);

		JSONObject jsonResponse = new JSONObject(response);
		Log.i(LOG_TAG, "JSON response: " + jsonResponse.toString());
		boolean valid = jsonResponse.getBoolean("valid");
		String message = jsonResponse.optString("message");

		Assert.assertTrue(message, valid);
	}

	private static String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));
		String text;

		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
		}
		reader.close();

		return contents.toString();
	}
}
