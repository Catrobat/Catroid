/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.common;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.catrobat.catroid.utils.ImageEditing;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION;
import static org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER;
import static org.catrobat.catroid.formulaeditor.Functions.COLOR_AT_XY;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LookDataTest {
	private final String fileName = "collision_donut.png";
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_LOOKS);
	private LookData lookData;
	private File imageFolder;

	private Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] {MediaStore.Images.Media._ID},
				MediaStore.Images.Media.DATA + "=? ",
				new String[] {filePath}, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			cursor.close();
			return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		StorageOperations.deleteDir(ApplicationProvider.getApplicationContext().getCacheDir());
		imageFolder = new File(ApplicationProvider.getApplicationContext().getCacheDir(), IMAGE_DIRECTORY_NAME);
		if (!imageFolder.exists()) {
			imageFolder.mkdirs();
		}

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.collision_donut,
				imageFolder, fileName, 1.0);

		lookData = new LookData("test", imageFile);
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(ApplicationProvider.getApplicationContext().getCacheDir());
	}

	@Test
	public void testCollisionInformation() {
		String metadata = ImageEditing.readMetaDataStringFromPNG(imageFolder.getAbsolutePath() + "/" + fileName,
				Constants.COLLISION_PNG_META_TAG_KEY);

		assertEquals("", metadata);

		lookData.getCollisionInformation().loadCollisionPolygon();

		metadata = ImageEditing.readMetaDataStringFromPNG(imageFolder.getAbsolutePath() + "/" + fileName,
				Constants.COLLISION_PNG_META_TAG_KEY);

		final String expectedMetadata = "0.0;228.0;9.0;321.0;57.0;411.0;136.0;474.0;228.0;500.0;305.0;495.0;375.0;"
				+ "468.0;436.0;419.0;474.0;364.0;497.0;295.0;499.0;218.0;481.0;151.0;443.0;89.0;385.0;38.0;321.0;9.0;"
				+ "179.0;9.0;115.0;38.0;57.0;89.0;19.0;151.0|125.0;248.0;154.0;330.0;201.0;365.0;248.0;375.0;313.0;"
				+ "358.0;365.0;299.0;374.0;234.0;346.0;170.0;285.0;130.0;206.0;133.0;150.0;175.0";

		assertEquals(expectedMetadata, metadata);
	}

	@Test
	public void testWebPImageLookDataIsVisible() throws IOException {
		Project project = createProject();
		Intents.init();
		baseActivityTestRule.launchActivity();

		final InputStream inputStream = baseActivityTestRule.getActivity().getResources().getAssets().open("penguin.webp");
		File outFile = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "penguin.webp");
		FileOutputStream outputStream = new FileOutputStream(outFile);
		BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
		onView(withId(R.id.button_add))
				.perform(click());

		Intent chooserResultData = new Intent();
		Uri uri = getImageContentUri(baseActivityTestRule.getActivity(), outFile);
		chooserResultData.setDataAndType(uri, "image/webp");

		Instrumentation.ActivityResult chooserResult =
				new Instrumentation.ActivityResult(Activity.RESULT_OK, chooserResultData);
		intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(chooserResult);

		onView(withId(R.id.dialog_new_look_gallery))
				.perform(click());
		onView(withId(R.id.button_play))
				.perform(click());
		assertNotSame("#ffffff", project.getUserVariable("color").getValue());
		Intents.release();
		baseActivityTestRule.finishActivity();
	}

	private Formula getColorAtXYFormula() {
		FormulaElement colorAtXYLeftChild = new FormulaElement(NUMBER, "0", null);
		FormulaElement colorAtXYRightChild = new FormulaElement(NUMBER, "0", null);
		FormulaElement colorAtXY = new FormulaElement(FUNCTION, COLOR_AT_XY.name(), null);
		colorAtXY.setLeftChild(colorAtXYLeftChild);
		colorAtXY.setRightChild(colorAtXYRightChild);
		return new Formula(colorAtXY);
	}

	private Project createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), "LookDataTest");
		ProjectManager.getInstance().setCurrentProject(project);
		Script script = new StartScript();
		Sprite sprite = new Sprite("testSprite");
		Formula colorAtXYFormula = getColorAtXYFormula();

		UserVariable variable = new UserVariable("color");
		project.addUserVariable(variable);

		script.addBrick(new SetVariableBrick(colorAtXYFormula, variable));

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}
}
