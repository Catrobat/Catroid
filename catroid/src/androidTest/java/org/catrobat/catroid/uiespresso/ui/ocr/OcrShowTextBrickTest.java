/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.ocr;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.screenshot.Screenshot;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class OcrShowTextBrickTest {

	private static final int ONE_TO_ZERO = 1234567890;
	private static final String HI = "السلام عليكم";
	private static final String HI_HINDI_NF = "العربية١٢٣";
	private static final String AR_ONE_TO_ZERO = "١٢٣٤٥٦٧٨٩٠";
	private static File dataDir = new File(Environment.getExternalStorageDirectory().getPath() + "/tesseract/tessdata/");
	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);
	private ScriptEvaluationGateBrick lastBrickInScript;
	private String[] arAlphabetAndNumbers = new String[] {"ا", "أ", "آ", "ب", "ت", "ث", "ج", "ح", "خ",
			"د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ",
			"ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "!", "~",
			"ه", "و", "ي", "لآ", "ة", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠", " "};

	@BeforeClass
	public static void beforAll() throws IOException {
		dataDir.mkdirs();
		copyDirOrFileFromAssetManager("tessdata", "/tesseract/tessdata/");
	}

	@AfterClass
	public static void afterAll() {
		deleteDirectory(new File(Environment.getExternalStorageDirectory().getPath() + "/tesseract/"));
		deleteDirectory(new File(Environment.getExternalStorageDirectory().getPath() + "/Pocket Code uiTest/"));
	}

	private static String copyDirOrFileFromAssetManager(String argAssetDir, String argDestinationDir) throws IOException {
		File externalStorageDirectory = Environment.getExternalStorageDirectory();
		String destDirPath = externalStorageDirectory + addLeadingSlash(argDestinationDir);
		File destDir = new File(destDirPath);

		createDir(destDir);

		AssetManager assetManager = InstrumentationRegistry.getContext().getResources().getAssets();
		String[] files = assetManager.list(argAssetDir);

		for (String file : files) {

			String absAssetFilePath = addTrailingSlash(argAssetDir) + file;
			String[] subFiles = assetManager.list(absAssetFilePath);

			if (subFiles.length == 0) {
				// It is a file
				String destFilePath = addTrailingSlash(destDirPath) + file;
				copyAssetFile(absAssetFilePath, destFilePath);
			} else {
				// It is a sub directory
				copyDirOrFileFromAssetManager(absAssetFilePath, addTrailingSlash(argDestinationDir) + file);
			}
		}

		return destDirPath;
	}

	private static void copyAssetFile(String assetFilePath, String destinationFilePath) throws IOException {
		InputStream in = InstrumentationRegistry.getContext().getResources().getAssets().open(assetFilePath);
		OutputStream out = new FileOutputStream(destinationFilePath);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private static String addTrailingSlash(String path) {
		if (path.charAt(path.length() - 1) != '/') {
			path += "/";
		}
		return path;
	}

	private static String addLeadingSlash(String path) {
		if (path.charAt(0) != '/') {
			path = "/" + path;
		}
		return path;
	}

	private static void createDir(File dir) throws IOException {
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IOException("Can't create directory, a file is in the way");
			}
		} else {
			dir.mkdirs();
			if (!dir.isDirectory()) {
				throw new IOException("Unable to create directory");
			}
		}
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return path.delete();
	}

	@Test
	public void testShowHindiNumberFormatAndArabicString() throws Exception {
		createProject(HI_HINDI_NF);
		baseActivityTestRule.launchActivity(null);

		lastBrickInScript.waitUntilEvaluated(3000);
		onView(isRoot())
				.perform(CustomActions.wait(1000));

		Bitmap bitmap = Screenshot.capture().getBitmap();
		TessOCR tessocr = new TessOCR("ara");
		final String srcText = tessocr.getOCRResult(bitmap);

		assertFalse("stage screenshot contains a text which is :" + srcText, srcText.isEmpty());

		for (int i = 0; i < srcText.length(); i++) {
			String str = String.valueOf(srcText.charAt(i));
			assertTrue(str + " does not belong to arAlphabetAndNumbers Array", Arrays.asList(arAlphabetAndNumbers).contains(str));
		}

		tessocr.onDestroy();
	}

	@Test
	public void testShowHindiNumberFormatAsEngNumberFormat() throws Exception {
		createProject(AR_ONE_TO_ZERO);
		baseActivityTestRule.launchActivity(null);

		lastBrickInScript.waitUntilEvaluated(3000);
		onView(isRoot())
				.perform(CustomActions.wait(1000));

		Bitmap bitmap = Screenshot.capture().getBitmap();
		TessOCR tessocr = new TessOCR("eng");
		final String srcText = tessocr.getOCRResult(bitmap);

		assertFalse("stage screenshot contains a text which is :" + srcText, srcText.isEmpty());
		assertEquals(String.valueOf(ONE_TO_ZERO), srcText);

		tessocr.onDestroy();
	}

	@Test
	public void testShowArabicString() throws Exception {
		createProject(HI);
		baseActivityTestRule.launchActivity(null);

		lastBrickInScript.waitUntilEvaluated(3000);
		onView(isRoot())
				.perform(CustomActions.wait(1000));

		Bitmap bitmap1 = Screenshot.capture().getBitmap();
		TessOCR tessocr = new TessOCR("ara");
		final String srcText = tessocr.getOCRResult(bitmap1);

		assertFalse("stage screenshot contains a text which is :" + srcText, srcText.isEmpty());
		for (int i = 0; i < srcText.length(); i++) {
			String str = String.valueOf(srcText.charAt(i));
			assertTrue(str + " does not belong to arAlphabetAndNumbers Array", Arrays.asList(arAlphabetAndNumbers).contains(str));
		}

		tessocr.onDestroy();
	}

	private void createProject(String showVariableValue) {
		Project project = new Project(null, "TestOcr");

		Sprite firstSprite = new Sprite("firstSprite");
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		UserVariable userVariable = dataContainer.addProjectUserVariable("userVariable");
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(showVariableValue), userVariable);
		ShowTextBrick showTextBrick = new ShowTextBrick(0, 0);
		showTextBrick.setUserVariable(userVariable);
		Script startScript1 = new StartScript();
		firstSprite.addScript(startScript1);
		startScript1.addBrick(setVariableBrick);
		startScript1.addBrick(showTextBrick);
		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(startScript1);
	}
}
