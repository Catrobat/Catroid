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

import java.io.IOException;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 * 
 */
public class SoundBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private PlaySoundBrick soundBrick;
	private String selectedTitle;
	private String title;
	private ArrayList<SoundInfo> soundlist;

	public SoundBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
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

	@Smoke
	public void testPlaySoundBrick() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.brick_play_sound)));

		assertTrue("Wrong title selected", solo.searchText(selectedTitle));

		StorageHandler.getInstance().setSoundContent(soundlist);

		solo.clickOnButton(selectedTitle);
		solo.clickInList(2);
		solo.sleep(500);
		assertTrue("Wrong title selected", solo.searchText(title));
	}

	private void createProject() throws IOException {
		title = "myTitle";
		soundlist = new ArrayList<SoundInfo>();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setId(5);
		soundInfo.setTitle("something");
		soundInfo.setPath("path/path/1/");
		soundlist.add(soundInfo);
		soundInfo = new SoundInfo();
		soundInfo.setId(6);
		soundInfo.setTitle(title);
		soundInfo.setPath("path/path/2/");
		soundlist.add(soundInfo);
		soundInfo = new SoundInfo();
		soundInfo.setId(7);
		selectedTitle = "selectedTitle";
		soundInfo.setTitle(selectedTitle);
		soundInfo.setPath("path/path/3/");
		soundlist.add(soundInfo);

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);
		soundBrick = new PlaySoundBrick(sprite);
		soundBrick.setPathToSoundfile(soundInfo.getTitle());
		soundBrick.setTitle(selectedTitle);
		script.addBrick(soundBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}