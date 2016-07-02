/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.common.ScratchProjectData.ScratchRemixProjectData;
import org.catrobat.catroid.common.ScratchProjectPreviewData;
import org.catrobat.catroid.transfers.FetchScratchProjectDetailsTask.ScratchProjectDataFetcher;
import org.catrobat.catroid.ui.ScratchConverterActivity.ScratchConverterClient;
import org.catrobat.catroid.ui.ScratchProjectDetailsActivity;
import org.catrobat.catroid.ui.adapter.ScratchRemixedProjectAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.WebconnectionException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.flowtextview.FlowTextView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ScratchProjectDetailsActivityTest extends BaseActivityInstrumentationTestCase<ScratchProjectDetailsActivity> {
	private static final String TAG = ScratchProjectDetailsActivityTest.class.getSimpleName();

	private ScratchProjectPreviewData projectPreviewData;
	private ScratchProjectData projectData;
	private ScratchRemixProjectData remixedProjectData;

	private ScratchProjectDataFetcher fetcherMock;

	public ScratchProjectDetailsActivityTest() throws InterruptedIOException, WebconnectionException {
		super(ScratchProjectDetailsActivity.class);

		List<String> tags = new ArrayList<String>() {{
				add("animations");
				add("castle");
		}};
		long projectID = 10205819;
		projectData = new ScratchProjectData(projectID, "Dancin' in the Castle", "jschombs",
				"Click the flag to run the stack. Click the space bar to change it up!",
				"First project on Scratch! This was great.", 1_723_123, 37_239, 11, new Date(), new Date(), tags);
		Uri uri = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10211023_144x108.png?v=1368486334.0");
		remixedProjectData = new ScratchRemixProjectData(10211023, "Dancin' in the Castle remake",
				"Amanda69", new WebImage(uri, 150, 150));
		projectData.addRemixProject(remixedProjectData);
		projectPreviewData = new ScratchProjectPreviewData(projectID, projectData.getTitle(), "May 13, 2013 ... Click "
				+ "the flag to run the stack.");
		fetcherMock = Mockito.mock(ScratchProjectDataFetcher.class);
		when(fetcherMock.fetchScratchProjectDetails(any(Long.class))).thenReturn(projectData);
		ScratchProjectDetailsActivity.setDataFetcher(fetcherMock);
	}

	@Override
	public ScratchProjectDetailsActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Constants.SCRATCH_PROJECT_DATA, (Parcelable) projectPreviewData);
		setActivityIntent(intent);
		return super.getActivity();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAreAllDetailViewsPresentAndPopulatedWithProjectData() {
		// title
		final View titleView = solo.getView(R.id.scratch_project_title);
		assertEquals(titleView.getVisibility(), View.VISIBLE);
		assertTrue(titleView instanceof TextView);
		assertEquals(projectPreviewData.getTitle(), ((TextView) titleView).getText());

		// owner
		final View ownerView = solo.getView(R.id.scratch_project_owner);
		assertEquals(ownerView.getVisibility(), View.VISIBLE);
		assertTrue(ownerView instanceof TextView);
		assertEquals(getActivity().getString(R.string.by) + " " + projectData.getOwner(), ((TextView) ownerView).getText());

		// instructions
		final View instructionsLabelView = solo.getView(R.id.scratch_project_instructions_label);
		assertEquals(instructionsLabelView.getVisibility(), View.VISIBLE);
		assertTrue(instructionsLabelView instanceof TextView);
		assertEquals(getActivity().getString(R.string.instructions), ((TextView) instructionsLabelView).getText());

		final View instructionsView = solo.getView(R.id.scratch_project_instructions_flow_text);
		assertEquals(instructionsView.getVisibility(), View.VISIBLE);
		assertTrue(instructionsView instanceof FlowTextView);
		assertEquals(projectData.getInstructions(), ((FlowTextView) instructionsView).getText());

		// notes & credits
		final View notesAndCreditsLabelView = solo.getView(R.id.scratch_project_notes_and_credits_label);
		assertEquals(notesAndCreditsLabelView.getVisibility(), View.VISIBLE);
		assertTrue(notesAndCreditsLabelView instanceof TextView);
		assertEquals(getActivity().getString(R.string.notes_and_credits), ((TextView) notesAndCreditsLabelView).getText());

		final View notesAndCreditsView = solo.getView(R.id.scratch_project_notes_and_credits_text);
		assertEquals(notesAndCreditsView.getVisibility(), View.VISIBLE);
		assertTrue(notesAndCreditsView instanceof TextView);
		assertEquals(projectData.getNotesAndCredits(), ((TextView) notesAndCreditsView).getText());

		// sharing (favorites, loves and views)
		final View favoritesLabelView = solo.getView(R.id.scratch_project_favorites_text);
		final String expectedHumanReadableFavoritesNumber = "37k";
		assertEquals(favoritesLabelView.getVisibility(), View.VISIBLE);
		assertTrue(favoritesLabelView instanceof TextView);
		assertEquals(expectedHumanReadableFavoritesNumber, ((TextView) favoritesLabelView).getText());

		final View lovesLabelView = solo.getView(R.id.scratch_project_loves_text);
		assertEquals(lovesLabelView.getVisibility(), View.VISIBLE);
		assertTrue(lovesLabelView instanceof TextView);
		assertEquals(projectData.getLoves(), Integer.parseInt(((TextView) lovesLabelView).getText().toString()));

		final View viewsLabelView = solo.getView(R.id.scratch_project_views_text);
		final String expectedHumanReadableViewsNumber = "1M";
		assertEquals(viewsLabelView.getVisibility(), View.VISIBLE);
		assertTrue(viewsLabelView instanceof TextView);
		assertEquals(expectedHumanReadableViewsNumber, ((TextView) viewsLabelView).getText());

		// tags
		final View tagsLabelView = solo.getView(R.id.scratch_project_tags_text);
		assertEquals(tagsLabelView.getVisibility(), View.VISIBLE);
		assertTrue(tagsLabelView instanceof TextView);
		final StringBuilder tagList = new StringBuilder();
		int index = 0;
		for (String tag : projectData.getTags()) {
			tagList.append((index++ > 0 ? ", " : "") + tag);
		}
		assertEquals(tagList.toString(), ((TextView) tagsLabelView).getText());

		// shared date & modified date
		final String sharedDateString = Utils.formatDate(projectData.getSharedDate(), Locale.getDefault());
		final View sharedDateView = solo.getView(R.id.scratch_project_shared_text);
		final String sharedDateText = ((TextView) sharedDateView).getText().toString();
		assertEquals(sharedDateView.getVisibility(), View.VISIBLE);
		assertTrue(sharedDateView instanceof TextView);
		assertNotNull(((TextView) sharedDateView).getText());
		assertEquals(getActivity().getString(R.string.shared), sharedDateText.split(":")[0]);
		assertEquals(sharedDateString, sharedDateText.split(":")[1].trim());

		final String modifiedDateString = Utils.formatDate(projectData.getModifiedDate(), Locale.getDefault());
		final View modifiedDateView = solo.getView(R.id.scratch_project_modified_text);
		final String modifiedDateText = ((TextView) modifiedDateView).getText().toString();
		assertEquals(modifiedDateView.getVisibility(), View.VISIBLE);
		assertTrue(modifiedDateView instanceof TextView);
		assertNotNull(((TextView) modifiedDateView).getText());
		assertEquals(getActivity().getString(R.string.modified), modifiedDateText.split(":")[0]);
		assertEquals(modifiedDateString, sharedDateText.split(":")[1].trim());

		// remix label
		final View remixesLabelView = solo.getView(R.id.scratch_project_remixes_label);
		assertEquals(remixesLabelView.getVisibility(), View.VISIBLE);
		assertTrue(remixesLabelView instanceof TextView);
		assertEquals(getActivity().getString(R.string.remixes), ((TextView) remixesLabelView).getText());

		// convert button
		final View convertButtonView = solo.getView(R.id.scratch_project_convert_button);
		assertEquals(convertButtonView.getVisibility(), View.VISIBLE);
		assertTrue(convertButtonView instanceof Button);
		assertEquals(getActivity().getString(R.string.convert), ((TextView) convertButtonView).getText());
	}

	public void testRemixListViewPopulatedWithRemixProjectData() {
		// remixed list view
		View remixesListView = solo.getView(R.id.scratch_project_remixes_list_view);
		assertEquals(remixesListView.getVisibility(), View.VISIBLE);
		assertTrue(remixesListView instanceof ListView);
		ListAdapter listAdapter = ((ListView) remixesListView).getAdapter();
		assertNotNull(listAdapter);
		assertTrue(listAdapter.getCount() == 1);
		assertTrue(listAdapter instanceof ScratchRemixedProjectAdapter);
		ScratchRemixedProjectAdapter remixedProjectAdapter = (ScratchRemixedProjectAdapter) listAdapter;

		// remixed project
		ScratchRemixProjectData expectedRemixedProjectData = remixedProjectData;
		ScratchRemixProjectData remixedProjectData = remixedProjectAdapter.getItem(0);
		assertTrue(solo.searchText(expectedRemixedProjectData.getTitle()));
		assertEquals(expectedRemixedProjectData.getId(), remixedProjectData.getId());
		assertEquals(expectedRemixedProjectData.getTitle(), remixedProjectData.getTitle());
		assertEquals(expectedRemixedProjectData.getOwner(), remixedProjectData.getOwner());
		assertNotNull(remixedProjectData.getProjectImage());
		assertEquals(expectedRemixedProjectData.getProjectImage().getUrl().toString(),
				remixedProjectData.getProjectImage().getUrl().toString());
	}

	public void testConvertButtonClickable() {
		assertTrue("Convert button not clickable!", solo.getButton(solo.getString(R.string.convert)).isClickable());
		assertTrue("Convert button not enabled!", solo.getButton(solo.getString(R.string.convert)).isEnabled());
	}

	public void testClickOnConvertButtonShouldTriggersConvertMethodOfClient() {
		// setup mock-client and convert-method callback
		final ScratchProjectDetailsActivity activity = getActivity();
		final Object[] convertMethodParams = { null, null };
		ScratchConverterClient clientMock = Mockito.mock(ScratchConverterClient.class);
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertEquals(invocation.getArguments().length, 2);
				convertMethodParams[0] = invocation.getArguments()[0];
				convertMethodParams[1] = invocation.getArguments()[1];
				return null;
			}
		}).when(clientMock).convertProject(any(Long.class), any(String.class));
		ScratchProjectDetailsActivity.setConverterClient(clientMock);

		// click on convert button
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				convertButton.performClick();
				solo.sleep(1_000);
				assertTrue(convertMethodParams[0] instanceof Long);
				assertTrue(convertMethodParams[1] instanceof String);
				assertEquals(convertMethodParams[0], projectData.getId());
				assertEquals(convertMethodParams[1], projectData.getTitle());
				assertFalse(solo.getCurrentActivity() instanceof ScratchProjectDetailsActivity);
			}
		});
	}
}
