/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import android.view.Display;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.io.CustomKeyboard;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class SendToPcBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private SendToPcBrick sendToPcBrick;

	public SendToPcBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testGetInstance() {
		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		assertNotNull("PcConnectionManager could not be initialized!", connectionManager);
	}

	public void testSendToPcBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();
		assertEquals("Incorrect number of bricks!", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks!", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks!", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance!", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_send_to_pc)));

		checkKeyboard();
	}

	//	@SuppressLint("NewApi")
	public void checkKeyboard() {
		String ok = solo.getString(R.string.ok);
		int[] it = new int[4];
		int innerLoop;
		it[0] = 4;
		it[1] = 3;
		it[2] = 4;
		it[3] = 3;

		Display display = this.getActivity().getWindowManager().getDefaultDisplay();
		//		Point size = new Point();
		//		display.getSize(size);
		//		int displayWidth = size.x;
		//		int displayHeight = size.y;
		@SuppressWarnings("deprecation")
		int displayWidth = display.getWidth();
		@SuppressWarnings("deprecation")
		int displayHeight = display.getHeight();
		int xStep = displayWidth / 5;
		float yStep = (float) (displayHeight / 8.25);
		for (int j = 0; j < 4; j++) {
			innerLoop = j;

			j = (int) ((j + 3) * yStep);
			for (int i = 1; i < it[innerLoop] + 1; i++) {
				if (innerLoop == 3 && i == 3) {
					i += 1;
				}
				i *= xStep;
				solo.clickOnEditText(0);
				solo.clickOnScreen(i, j);
				solo.clickOnText(ok);
				checkKeyVaule(sendToPcBrick.getKey());
				i /= xStep;
			}
			j = (int) ((j / yStep) - 3 + 1);
		}
	}

	public void checkKeyVaule(int key) {
		switch (key) {
			case CustomKeyboard.KEY_ALT:
				assertEquals("Value of key ALT contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_alt));
				break;
			case CustomKeyboard.KEY_ALT_GR:
				assertEquals("Value of key ALT GR contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_alt_gr));
				break;
			case CustomKeyboard.KEY_BACKSPACE:
				assertEquals("Value of key BACKSPACE contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_back_space));
				break;
			case CustomKeyboard.KEY_TAB:
				assertEquals("Value of key TAB contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_tab));
				break;
			case CustomKeyboard.KEY_ENTER:
				assertEquals("Value of key ENTER contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_enter));
				break;
			case CustomKeyboard.KEY_SHIFT:
				assertEquals("Value of key SHIFT contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_shift));
				break;
			case CustomKeyboard.KEY_CONTROL:
				assertEquals("Value of key CTRL contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_control));
				break;
			case CustomKeyboard.KEY_CAPSLOCK:
				assertEquals("Value of key ALT contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_caps_lock));
				break;
			case CustomKeyboard.KEY_ESCAPE:
				assertEquals("Value of key ESC contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_escape));
				break;
			case CustomKeyboard.KEY_ARROW_UP:
				assertEquals("Value of key UP contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_arrow_up));
				break;
			case CustomKeyboard.KEY_SPACE:
				assertEquals("Value of key SPACE contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_space));
				break;
			case CustomKeyboard.KEY_ARROW_LEFT:
				assertEquals("Value of key LEFT contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_arrow_left));
				break;
			case CustomKeyboard.KEY_ARROW_RIGHT:
				assertEquals("Value of key RIGHT contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_arrow_right));
				break;
			case CustomKeyboard.KEY_ARROW_DOWN:
				assertEquals("Value of key DOWN contains wrong text!", solo.getEditText(0).getText().toString(),
						solo.getString(R.string.key_arrow_down));
				break;
			default:
				break;
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		sendToPcBrick = new SendToPcBrick(sprite);
		script.addBrick(sendToPcBrick);
		sprite.addScript(script);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
