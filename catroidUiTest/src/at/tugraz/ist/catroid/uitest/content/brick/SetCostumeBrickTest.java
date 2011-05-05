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
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class SetCostumeBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private ImageView setCostumeImageView;

	/*
	 * public class MockGalleryActivity extends Activity {
	 * private static final String RESOURCE_LOCATION = "res/drawable/catroid_sunglasses";
	 * 
	 * @Override
	 * protected void onCreate(android.os.Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * finish();
	 * }
	 * 
	 * @Override
	 * protected void onDestroy() {
	 * Intent resultIntent = new Intent();
	 * File resourceFile = new File(RESOURCE_LOCATION);
	 * if (!resourceFile.exists() || !resourceFile.canRead()) {
	 * throw new RuntimeException("Could not open resource file: " + resourceFile.getAbsolutePath());
	 * }
	 * resultIntent.setData(Uri.fromFile(resourceFile));
	 * 
	 * setResult(RESULT_OK, resultIntent);
	 * super.onDestroy();
	 * }
	 * }
	 */
	public SetCostumeBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		List<Brick> brickList = Utils.createTestProject();

		solo = new Solo(getInstrumentation(), getActivity());

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_set_costume);
		final int setCostumeBrickIndex = brickList.size();

		//setCostumeImageView = solo.getCurrentImageViews().get(imageViewIndex);
		setCostumeImageView = (ImageView) solo.getView(R.id.costume_image_view);
		setCostumeImageView.setDrawingCacheEnabled(true);

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				//				Intent intent = new Intent(getInstrumentation().getContext(),
				//						at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
				//				getActivity().startActivityForResult(intent, setCostumeBrickIndex);

				Intent intent = new Intent();
				intent.setComponent(new ComponentName("at.tugraz.ist.catroid.uitest",
						"at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity"));
				getActivity().startActivityForResult(intent, setCostumeBrickIndex);

			}
		};

		assertNotNull("ImageView of change costume brick was not found", setCostumeImageView);
		setCostumeImageView.setOnClickListener(listener);
	}

	@Override
	public void tearDown() throws Exception {
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
		assertNotNull(bitmapToTest);

		Bitmap originalBitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
				at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses);
		assertNotNull(originalBitmap);

		//		assertEquals("Wrong width in Bitmap.", originalBitmap.getWidth(), bitmapToTest.getWidth());
		//		assertEquals("Wrong height in Bitmap.", originalBitmap.getHeight(), bitmapToTest.getHeight());
		//		for (int y = 0; y < originalBitmap.getHeight(); ++y) {
		//			for (int x = 0; x < originalBitmap.getWidth(); ++x) {
		//				assertEquals("Wrong Pixel at Position " + x + "," + y, originalBitmap.getPixel(x, y), bitmapToTest
		//						.getPixel(x, y));
		//			}
		//		}

	}
}
