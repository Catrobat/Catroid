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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.io.File;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class SetCostumeBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private ImageView setCostumeImageView;
	private File imageFile;

	public SetCostumeBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		List<Brick> brickList = UiTestUtils.createTestProject();

		solo = new Solo(getInstrumentation(), getActivity());
		solo.clickOnText("Costumes");
		solo.clickOnText("Script");
		solo.sleep(100);
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_costume);
		final int setCostumeBrickIndex = brickList.size();

		//setCostumeImageView = (ImageView) solo.getCurrentActivity().findViewById(R.id.costume_image_view);
		//setCostumeImageView.setDrawingCacheEnabled(true);

		// Copy test image to be used

		final int RESOURCE_LOCATION = R.drawable.catroid_sunglasses;
		String imageFilePath = Consts.DEFAULT_ROOT + "/" + UiTestUtils.DEFAULT_TEST_PROJECT_NAME
				+ Consts.IMAGE_DIRECTORY + "/" + "catroid_sunglasses.png";
		imageFile = UtilFile.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_LOCATION, getActivity(), TestUtils.TYPE_IMAGE_FILE);

		final File imageFileReference = imageFile;

		// Override OnClickListener to launch MockGalleryActivity
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent("android.intent.action.MAIN");
				intent.setComponent(new ComponentName("at.tugraz.ist.catroid.uitest",
						"at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity"));
				intent.putExtra("filePath", imageFileReference.getAbsolutePath());
				solo.getActivityMonitor().getLastActivity().startActivityForResult(intent, setCostumeBrickIndex);
			}
		};

		assertNotNull("ImageView of change costume brick was not found", setCostumeImageView);
		setCostumeImageView.setOnClickListener(listener);
	}

	@Override
	public void tearDown() throws Exception {
		if (imageFile.exists()) {
			imageFile.delete();
		}

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	public void testChangeCostume() {
		solo.sleep(1000);
		solo.clickOnView(setCostumeImageView);
		solo.sleep(5000);

		Bitmap bitmapToTest = setCostumeImageView.getDrawingCache();
		assertNotNull("Bitmap of costume ImageView is null", bitmapToTest);

		Bitmap originalBitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
				at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses);
		assertNotNull("Decoding the costume resource failed", originalBitmap);

		solo.sleep(1000);

	}
}
