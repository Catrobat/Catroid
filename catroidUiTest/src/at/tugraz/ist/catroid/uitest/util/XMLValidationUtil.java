package at.tugraz.ist.catroid.uitest.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;

public class XMLValidationUtil {
	private static final String XML_VALIDATING_URL = "http://catroidtestserver.ist.tugraz.at/tests/catroid/validateXml.php";

	public static void sendProjectXMLToServerForValidating(Project projectToValidate) throws IOException, JSONException {
		String projectName = projectToValidate.getName();
		String fullPathFilename = Utils.buildPath(Consts.DEFAULT_ROOT, projectName, projectName
				+ Consts.PROJECT_EXTENTION);
		sendProjectXMLToServerForValidating(fullPathFilename);
	}

	public static void sendProjectXMLToServerForValidating(String fullPathFilename) throws IOException, JSONException {

		String xmlContent = readTextFile(fullPathFilename);

		HashMap<String, String> postValues = new HashMap<String, String>();
		postValues.put("xmlToValidate", xmlContent);

		ConnectionWrapper connection = new ConnectionWrapper();
		String responce = connection.doHttpPost(XML_VALIDATING_URL, postValues);

		JSONObject jsonResponce = new JSONObject(responce);
		System.out.println("responce: " + jsonResponce.toString());
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
