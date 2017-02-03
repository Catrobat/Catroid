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
package org.catrobat.catroid.uitest.ui.activity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.view.View;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.pocketmusic.ui.NoteView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.Random;

public class PocketMusicTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public PocketMusicTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();

		UiTestUtils.getIntoSoundsFromMainMenu(solo);
		prepareTest();
	}

	public void testOrientation() throws NameNotFoundException {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		assertEquals("PocketcodeActivity not in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, solo
				.getCurrentActivity().getResources().getConfiguration().orientation);

		PackageManager packageManager = solo.getCurrentActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(solo.getCurrentActivity().getComponentName(),
				PackageManager.GET_META_DATA);

		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);

		assertEquals(SoundRecorderActivity.class.getSimpleName()
						+ " not set to be in portrait mode in AndroidManifest.xml!",
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
				activityInfo.screenOrientation
		);
	}

	public void testPianoElement() {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		assertNotNull("Piano Element was not found.", solo.getCurrentActivity().findViewById(R.id.musicdroid_piano));
	}

	public void testPocketmusic() {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		TrackView trackView = (TrackView) solo.getCurrentActivity().findViewById(R.id.musicdroid_note_grid);
		int trackGridHashCode = trackView.getTrackGrid().hashCode();

		Random random = new Random();
		int randomRow = random.nextInt(TrackView.ROW_COUNT);
		int randomCol = random.nextInt(TrackRowView.QUARTER_COUNT);
		toggleNote(trackView, randomRow, randomCol, "Button not toggled");

		solo.goBack();

		ScriptActivity scriptActivity = (ScriptActivity) solo.getCurrentActivity();
		SoundFragment soundFragment = (SoundFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_SOUNDS);

		assertEquals("Saving Pocketmusic MIDI did not work.", 1, soundFragment.getSoundInfoList().size());

		View firstPocketMusicView = null;

		for (int i = 0; i < soundFragment.getSoundInfoList().size(); i++) {
			SoundInfo soundInfo = soundFragment.getSoundInfoList().get(i);
			if (soundInfo.getSoundFileName().matches(".*MUS-.*\\.midi")) {
				assertEquals("Wrong Pocketmusic title.", solo.getString(R.string.pocketmusic_recorded_filename),
						soundInfo.getTitle());
				firstPocketMusicView = soundFragment.getListView().getChildAt(i);
				break;
			}
		}

		solo.clickOnView(firstPocketMusicView);
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		trackView = (TrackView) solo.getCurrentActivity().findViewById(R.id.musicdroid_note_grid);

		int loadedTrackgridHashCode = trackView.getTrackGrid().hashCode();

		assertFalse("Loaded Track is the same.", trackGridHashCode == loadedTrackgridHashCode);

		toggleNote(trackView, randomRow, randomCol, "Button toggled");

		loadedTrackgridHashCode = trackView.getTrackGrid().hashCode();

		assertTrue("Loaded Track is not the same.", trackGridHashCode == loadedTrackgridHashCode);
	}

	public void testRandomButtonToggle() {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		TrackView trackView = (TrackView) solo.getCurrentActivity().findViewById(R.id.musicdroid_note_grid);

		Random random = new Random();
		int randomRow = random.nextInt(TrackView.ROW_COUNT);
		int randomCol = random.nextInt(TrackRowView.QUARTER_COUNT);
		toggleNote(trackView, randomRow, randomCol, "Button not toggled");
		toggleNote(trackView, randomRow, randomCol, "Button toggled");
	}

	private void toggleNote(TrackView trackView, int rowIndex, int columnIndex, String
			assertionText) {
		TrackRowView randomRowView = trackView.getTrackRowViews().get(rowIndex);
		NoteView randomNoteView = randomRowView.getNoteViews().get(columnIndex);
		boolean toggled = randomNoteView.isToggled();
		solo.clickOnView(randomNoteView);
		solo.sleep(200);
		assertEquals(assertionText, randomNoteView.isToggled(), !toggled);
	}

	public void testButtonCount() {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		TrackView trackView = (TrackView) solo.getCurrentActivity().findViewById(R.id.musicdroid_note_grid);
		assertEquals("TrackView size invalid", TrackView.ROW_COUNT, trackView.getTrackRowViews().size());

		for (TrackRowView trackRowView : trackView.getTrackRowViews()) {
			assertEquals("TrackRowView size invalid", trackRowView.getTactCount(), trackRowView.getNoteViews().size());
		}
	}

	public void testPlayButtonElement() {
		solo.waitForActivity(PocketMusicActivity.class.getSimpleName());

		assertNotNull("Play Button Element was not found.",
				solo.getCurrentActivity().findViewById(R.id.pocketmusic_play_button));
	}

	private void prepareTest() {
		UiTestUtils.waitForFragment(solo, R.id.fragment_sound);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		String pocketRecorderText = solo.getString(R.string.add_sound_pocketmusic);

		solo.waitForText(pocketRecorderText);
		assertTrue("Pocketmusic is not present", solo.searchText(pocketRecorderText));
		solo.clickOnText(pocketRecorderText);
	}
}
