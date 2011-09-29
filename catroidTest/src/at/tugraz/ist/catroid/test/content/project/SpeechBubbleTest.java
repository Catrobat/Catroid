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
package at.tugraz.ist.catroid.test.content.project;

import android.test.InstrumentationTestCase;

public class SpeechBubbleTest extends InstrumentationTestCase {

	//	private String projectName;
	//	private static final int IMAGE_FILE_ID = R.raw.icon;
	//	private File testImage;
	//	public Sprite sprite = new Sprite("testSprite");
	//
	//	public void testUninitSpeechBubble() throws Exception {
	//		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);
	//
	//		if (projectFile.exists()) {
	//			UtilFile.deleteDirectory(projectFile);
	//		}
	//
	//		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
	//		StorageHandler.getInstance().saveProject(project);
	//		ProjectManager.getInstance().setProject(project);
	//
	//		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
	//				.getContext(), TestUtils.TYPE_IMAGE_FILE);
	//
	//		Values.SCREEN_HEIGHT = 800;
	//		Values.SCREEN_WIDTH = 480;
	//
	//		project.addSprite(sprite);
	//		Costume costume = sprite.getCostume();
	//		costume.changeImagePath(testImage.getAbsolutePath());
	//		SpeechBubble speechBubble = sprite.getBubble();
	//
	//		Paint textPaint = (Paint) TestUtils.getPrivateField("textPaint", speechBubble, false);
	//		Paint debugPaint = (Paint) TestUtils.getPrivateField("debugPaint", speechBubble, false);
	//		String speechBubbleText = (String) TestUtils.getPrivateField("speechBubbleText", speechBubble, false);
	//		int expectedDebugColor = Color.RED;
	//		String expectedspeechBubbleText = "";
	//
	//		assertEquals("Unexpected defaultText", expectedspeechBubbleText, speechBubbleText);
	//		assertNotNull("textPaint not initialized", textPaint);
	//		assertNotNull("textPaint not initialized", debugPaint);
	//		assertEquals("Unexpected DebugColor", expectedDebugColor, debugPaint.getColor());
	//	}
	//
	//	public void testInitSpeechBubble() {
	//		SpeechBubble speechBubble = sprite.getBubble();
	//		int speechBubblePictureID = 1;
	//		int speechBubblePictureInvID = -1;
	//		String speechBubbleText = "ABCDEFGHIJKLMNO";
	//		speechBubble.setSpeechBubble(speechBubbleText, speechBubblePictureID, speechBubblePictureInvID);
	//		int speechBubblePictureIDOut = (Integer) TestUtils
	//				.getPrivateField("speechBubblePictureID", speechBubble, false);
	//		int speechBubblePictureInvIDOut = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID", speechBubble,
	//				false);
	//		String speechBubbleTextOut = (String) TestUtils.getPrivateField("speechBubbleText", speechBubble, false);
	//
	//		assertEquals("Unexpected speechBubblePictureID", speechBubblePictureID, speechBubblePictureIDOut);
	//		assertEquals("Unexpected speechBubblePictureInvID", speechBubblePictureInvID, speechBubblePictureInvIDOut);
	//		assertEquals("Unexpected speechBubbleText", speechBubbleText, speechBubbleTextOut);
	//	}

}
