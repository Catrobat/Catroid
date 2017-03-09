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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchVisibilityState;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.ScratchProgramDetailsActivity;
import org.catrobat.catroid.ui.adapter.ScratchRemixedProgramAdapter;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.Locale;

import uk.co.deanwild.flowtextview.FlowTextView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScratchProgramDetailsActivityTest extends BaseActivityInstrumentationTestCase<ScratchProgramDetailsActivity> {
	private ScratchProgramData programData;
	private ScratchProgramData remixedProgramData;
	private ScratchDataFetcher fetcherMock;
	private ConversionManager conversionManagerMock;

	public ScratchProgramDetailsActivityTest() {
		super(ScratchProgramDetailsActivity.class);
	}

	@Override
	public ScratchProgramDetailsActivity getActivity() {
		Intent intent = new Intent();
		intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) programData);
		setActivityIntent(intent);
		return super.getActivity();
	}

	@Override
	public void setUp() throws Exception {
		final long programID = 10205819;
		final Uri programImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10205819_480x360.png");
		programData = new ScratchProgramData(programID, "Dancin' in the Castle", "jschombs",
				new WebImage(programImageURL, 480, 360));
		programData.setNotesAndCredits("First project on Scratch! This was great.");
		programData.setInstructions("Click the flag to run the stack. Click the space bar to change it up!");
		programData.setViews(1_723_123);
		programData.setFavorites(37_239);
		programData.setLoves(11);
		programData.setSharedDate(new Date());
		programData.setModifiedDate(new Date());
		programData.setSharedDate(new Date());
		programData.addTag("animations");
		programData.addTag("castle");
		programData.setVisibilityState(ScratchVisibilityState.PUBLIC);

		Uri remixProgramImageURL = Uri.parse("https://cdn2.scratch.mit.edu/get_image/project/10211023_144x108.png?v=1368486334.0");
		remixedProgramData = new ScratchProgramData(10211023, "Dancin' in the Castle remake",
				"Amanda69", new WebImage(remixProgramImageURL, 144, 108));
		programData.addRemixProgram(remixedProgramData);

		// prepare mocks
		fetcherMock = Mockito.mock(ScratchDataFetcher.class);
		when(fetcherMock.fetchScratchProgramDetails(any(Long.class))).thenReturn(programData);

		conversionManagerMock = Mockito.mock(ConversionManager.class);
		when(conversionManagerMock.isJobInProgress(any(Long.class))).thenReturn(false);

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertNotNull("No arguments for addJobViewListener call given", invocation.getArguments());
				assertEquals("Invalid number of arguments", invocation.getArguments().length, 2);
				assertEquals("First argument program ID does not match the one of the currently loaded program!",
						invocation.getArguments()[0], programData.getId());
				assertTrue("Second argument must be an instance of the ScratchProgramDetailsActivity class",
						invocation.getArguments()[1] instanceof ScratchProgramDetailsActivity);
				return null;
			}
		}).when(conversionManagerMock).addJobViewListener(any(Long.class), any(JobViewListener.class));

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertNotNull("No arguments for addDwoanloadFinishedCallback call given", invocation.getArguments());
				assertEquals("Invalid number of arguments", invocation.getArguments().length, 1);
				assertTrue("First argument must be an instance of the ScratchProgramDetailsActivity class",
						invocation.getArguments()[0] instanceof ScratchProgramDetailsActivity);
				return null;
			}
		}).when(conversionManagerMock).addGlobalDownloadCallback(any(Client.DownloadCallback.class));

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertNotNull("No arguments for setCurrentActivity call given", invocation.getArguments());
				assertEquals("Invalid number of arguments", invocation.getArguments().length, 1);
				assertTrue("First argument must be an instance of the ScratchProgramDetailsActivity class",
						invocation.getArguments()[0] instanceof ScratchProgramDetailsActivity);
				return null;
			}
		}).when(conversionManagerMock).setCurrentActivity(any(Activity.class));

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertNotNull("No arguments for removeJobViewListener call given", invocation.getArguments());
				assertEquals("Invalid number of arguments", invocation.getArguments().length, 2);
				assertEquals("First argument program ID does not match the one of the currently loaded program!",
						invocation.getArguments()[0], programData.getId());
				assertTrue("Second argument must be an instance of the ScratchProgramDetailsActivity class",
						invocation.getArguments()[1] instanceof ScratchProgramDetailsActivity);
				return null;
			}
		}).when(conversionManagerMock).removeJobViewListener(any(Long.class), any(JobViewListener.class));

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				assertNotNull("No arguments for removeGlobalDownloadCallback call given", invocation.getArguments());
				assertEquals("Invalid number of arguments", invocation.getArguments().length, 1);
				assertTrue("First argument must be an instance of the ScratchProgramDetailsActivity class",
						invocation.getArguments()[0] instanceof ScratchProgramDetailsActivity);
				return null;
			}
		}).when(conversionManagerMock).removeGlobalDownloadCallback(any(Client.DownloadCallback.class));

		ScratchProgramDetailsActivity.setDataFetcher(fetcherMock);
		ScratchProgramDetailsActivity.setConversionManager(conversionManagerMock);
		super.setUp();
	}

	public void testIsTitleViewPresentAndHasCorrectContent() {
		final View titleView = solo.getView(R.id.scratch_project_title);
		assertEquals("Title view must be visible", titleView.getVisibility(), View.VISIBLE);
		assertTrue("Title view must be TextView", titleView instanceof TextView);
		assertEquals("Title view must contain program title", programData.getTitle(), ((TextView) titleView).getText());
	}

	public void testIsOwnerViewPresentAndHasCorrectContent() {
		final View ownerView = solo.getView(R.id.scratch_project_owner);
		assertEquals("Owner view must be visible", ownerView.getVisibility(), View.VISIBLE);
		assertTrue("Owner view must be TextView", ownerView instanceof TextView);
		assertEquals("Owner view must contain program author/owner name",
				getActivity().getString(R.string.by_x, programData.getOwner()), ((TextView) ownerView).getText());
	}

	public void testIsInstructionsViewPresentAndHasCorrectContent() {
		final View instructionsLabelView = solo.getView(R.id.scratch_project_instructions_label);
		assertEquals("Instructions label view must be visible", instructionsLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Instructions label view must be TextView", instructionsLabelView instanceof TextView);
		assertEquals("Instructions label view has wrong name",
				getActivity().getString(R.string.instructions), ((TextView) instructionsLabelView).getText());

		final View instructionsView = solo.getView(R.id.scratch_project_instructions_flow_text);
		assertEquals("Instructions view must be visible", instructionsView.getVisibility(), View.VISIBLE);
		assertTrue("Instructions view must be FlowTextView", instructionsView instanceof FlowTextView);
		assertEquals("Instructions label view must contain program instructions text",
				programData.getInstructions(), ((FlowTextView) instructionsView).getText());
	}

	public void testIsNotesAndCreditsViewPresentAndHasCorrectContent() {
		final View notesAndCreditsLabelView = solo.getView(R.id.scratch_project_notes_and_credits_label);
		assertEquals("Notes and credits view must be visible", notesAndCreditsLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Notes and credits view must be TextView", notesAndCreditsLabelView instanceof TextView);
		assertEquals("Notes and credits view has wrong name",
				getActivity().getString(R.string.notes_and_credits), ((TextView) notesAndCreditsLabelView).getText());

		final View notesAndCreditsView = solo.getView(R.id.scratch_project_notes_and_credits_text);
		assertEquals("Notes and credits view must be visible", notesAndCreditsView.getVisibility(), View.VISIBLE);
		assertTrue("Notes and credits view must be TextView", notesAndCreditsView instanceof TextView);
		assertEquals("Notes and credits view must contain program's notes and credits description",
				programData.getNotesAndCredits(), ((TextView) notesAndCreditsView).getText());
	}

	public void testIsSharingViewPresentAndHasCorrectContent() {
		final View favoritesLabelView = solo.getView(R.id.scratch_project_favorites_text);
		final String expectedHumanReadableFavoritesNumber = "37k";
		assertEquals("Favorites label view must be visible", favoritesLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Favorites label view must be TextView", favoritesLabelView instanceof TextView);
		assertEquals("Favorites label view has wrong value",
				expectedHumanReadableFavoritesNumber, ((TextView) favoritesLabelView).getText());

		final View lovesLabelView = solo.getView(R.id.scratch_project_loves_text);
		assertEquals("Loves label view must be visible", lovesLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Loves label view must be TextView", lovesLabelView instanceof TextView);
		assertEquals("Loves label view has wrong value",
				programData.getLoves(), Integer.parseInt(((TextView) lovesLabelView).getText().toString()));

		final View viewsLabelView = solo.getView(R.id.scratch_project_views_text);
		final String expectedHumanReadableViewsNumber = "1M";
		assertEquals("Views label view must be visible", viewsLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Views label view must be TextView", viewsLabelView instanceof TextView);
		assertEquals("Views label view has wrong value",
				expectedHumanReadableViewsNumber, ((TextView) viewsLabelView).getText());
	}

	public void testIsTagViewPresentAndHasCorrectContent() {
		final View tagsLabelView = solo.getView(R.id.scratch_project_tags_text);
		assertEquals("Tag label view must be visible", tagsLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Tag label view must be TextView", tagsLabelView instanceof TextView);
		final StringBuilder tagList = new StringBuilder();
		int index = 0;
		for (String tag : programData.getTags()) {
			tagList.append((index++ > 0 ? ", " : "") + tag);
		}
		assertEquals("Tag label view has wrong content",
				tagList.toString(), ((TextView) tagsLabelView).getText());
	}

	public void testIsSharedDateViewPresentAndHasCorrectContent() {
		final String sharedDateString = Utils.formatDate(programData.getSharedDate(), Locale.getDefault());
		final View sharedDateView = solo.getView(R.id.scratch_project_shared_text);
		assertEquals("Shared date text view must be visible", sharedDateView.getVisibility(), View.VISIBLE);
		assertTrue("Shared date text view must be TextView", sharedDateView instanceof TextView);
		assertNotNull("Shared date view is empty", ((TextView) sharedDateView).getText());
		final String sharedDateViewText = ((TextView) sharedDateView).getText().toString();
		assertEquals("Shared date text view has wrong content",
				getActivity().getString(R.string.shared_at_x, sharedDateString), sharedDateViewText);
	}

	public void testIsModifiedDateViewPresentAndHasCorrectContent() {
		final String modifiedDateString = Utils.formatDate(programData.getModifiedDate(), Locale.getDefault());
		final View modifiedDateView = solo.getView(R.id.scratch_project_modified_text);
		assertEquals("Modified date text view must be visible", modifiedDateView.getVisibility(), View.VISIBLE);
		assertTrue("Modified date text view must be TextView", modifiedDateView instanceof TextView);
		assertNotNull("Modified date view is empty", ((TextView) modifiedDateView).getText());
		final String modifiedDateViewText = ((TextView) modifiedDateView).getText().toString();
		assertEquals(getActivity().getString(R.string.modified_at_x, modifiedDateString), modifiedDateViewText);
	}

	public void testIsRemixViewPresentAndHasCorrectContent() {
		final View remixesLabelView = solo.getView(R.id.scratch_project_remixes_label);
		assertEquals("Remix-text-view is invisible!", remixesLabelView.getVisibility(), View.VISIBLE);
		assertTrue("Remix-text-view should be instance of TextView-class!", remixesLabelView instanceof TextView);
		assertEquals("Remix-text-view is not labeled correctly!", getActivity().getString(R.string.remixes),
				((TextView) remixesLabelView).getText());
	}

	public void testIsConvertButtonViewPresentAndHasCorrectContent() {
		// convert button
		final View convertButtonView = solo.getView(R.id.scratch_project_convert_button);
		assertEquals("Convert-button is invisible!", convertButtonView.getVisibility(), View.VISIBLE);
		assertTrue("Convert-button should be instance of Button-class!", convertButtonView instanceof Button);
		assertEquals("Wrong label name assigned to convert-button!", getActivity().getString(R.string.convert),
				((TextView) convertButtonView).getText());
	}

	public void testRemixListViewPopulatedWithRemixProjectData() {
		// remixed list view
		View remixesListView = solo.getView(R.id.scratch_project_remixes_list_view);
		assertEquals("ListView is not visible!", remixesListView.getVisibility(), View.VISIBLE);
		assertTrue("View is no list view!", remixesListView instanceof ListView);
		ListAdapter listAdapter = ((ListView) remixesListView).getAdapter();
		assertNotNull("ListView has no adapter!", listAdapter);
		assertTrue("Wrong number of remixes!", listAdapter.getCount() == 1);
		ScratchRemixedProgramAdapter remixedProjectAdapter = (ScratchRemixedProgramAdapter) listAdapter;

		// remixed project
		ScratchProgramData expectedRemixedProjectData = remixedProgramData;
		ScratchProgramData remixedProjectData = remixedProjectAdapter.getItem(0);
		assertTrue("Title not set!", solo.searchText(expectedRemixedProjectData.getTitle()));
		assertEquals("No or wrong project ID set!", expectedRemixedProjectData.getId(), remixedProjectData.getId());
		assertEquals("No or wrong project title set!", expectedRemixedProjectData.getTitle(),
				remixedProjectData.getTitle());
		assertEquals("No or wrong project owner set!", expectedRemixedProjectData.getOwner(),
				remixedProjectData.getOwner());
		assertNotNull("No project image set!", remixedProjectData.getImage());
		assertEquals("Wrong project image set!", expectedRemixedProjectData.getImage().getUrl().toString(),
				remixedProjectData.getImage().getUrl().toString());
	}

	public void testClickOnConvertButtonShouldDisableButton() throws InterruptedException {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		conversionManagerMock = Mockito.mock(ConversionManager.class);
		when(conversionManagerMock.getNumberOfJobsInProgress()).thenReturn(
				Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT - 1);
		ScratchProgramDetailsActivity.setConversionManager(conversionManagerMock);

		// before
		assertTrue("Convert button not clickable!", solo.getButton(solo.getString(R.string.convert)).isClickable());
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text not as expected!", convertButton.getText(),
				activity.getString(R.string.convert));

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				convertButton.performClick();
				synchronized (this) {
					notify();
				}
			}
		};
		synchronized (runnable) {
			activity.runOnUiThread(runnable);
			runnable.wait();
		}

		// after
		assertTrue("Activity not going to be closed", solo.getCurrentActivity().isFinishing());
		assertFalse("Convert button not disabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.converting));
		verify(conversionManagerMock, times(1)).getNumberOfJobsInProgress();
	}

	public void testClickOnConvertButtonWhenNumberOfRunningJobsLimitExceededShouldDisplayToastErrorNotification()
			throws InterruptedException {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		conversionManagerMock = Mockito.mock(ConversionManager.class);
		when(conversionManagerMock.getNumberOfJobsInProgress()).thenReturn(
				Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT);
		ScratchProgramDetailsActivity.setConversionManager(conversionManagerMock);

		// before
		assertTrue("Convert button not clickable!", solo.getButton(solo.getString(R.string.convert)).isClickable());
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text not as expected!", convertButton.getText(), activity.getString(R.string.convert));

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				convertButton.performClick();
				synchronized (this) {
					notify();
				}
			}
		};
		synchronized (runnable) {
			activity.runOnUiThread(runnable);
			runnable.wait();
		}

		// after
		assertFalse("Activity going to be closed unexpectedly", solo.getCurrentActivity().isFinishing());
		assertTrue("Convert button not enabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(), activity.getString(R.string.convert));
		assertTrue("No or unexpected toast error message shown!", solo.searchText(activity.getResources()
				.getQuantityString(R.plurals.error_cannot_convert_more_than_x_programs,
						Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
						Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT), true));
		verify(conversionManagerMock, times(1)).getNumberOfJobsInProgress();
	}

	// tests for JobViewListener interface methods
	@UiThreadTest
	public void testCallOnJobScheduledForCurrentlyViewedProgramShouldDisableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(programData.getId(), programData.getTitle(), programData.getImage());
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text not as expected!", convertButton.getText(), activity.getString(R.string.convert));

		activity.onJobScheduled(job);

		// after
		assertFalse("Convert button not disabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.converting));
	}

	@UiThreadTest
	public void testCallOnJobScheduledForDifferentProgramShouldNotDisableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(1234, "Some program bla bla...", null);
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text not as expected!", convertButton.getText(),
				activity.getString(R.string.convert));

		activity.onJobScheduled(job);

		// after
		assertTrue("Convert button not enabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.convert));
	}

	@UiThreadTest
	public void testCallOnJobFailedForCurrentlyViewedProgramShouldEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(programData.getId(), programData.getTitle(), programData.getImage());
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onJobFailed(job);

		// after
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.convert));
	}

	@UiThreadTest
	public void testCallOnJobFailedForDifferentProgramShouldNotEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(1234, "Some program bla bla...", null);
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onJobScheduled(job);

		// after
		assertFalse("Convert button not disabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.converting));
	}

	@UiThreadTest
	public void testCallOnUserCanceledJobForCurrentlyViewedProgramShouldEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(programData.getId(), programData.getTitle(), programData.getImage());
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onUserCanceledJob(job);

		// after
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.convert));
	}

	@UiThreadTest
	public void testCallOnUserCanceledJobForDifferentProgramShouldNotEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final Job job = new Job(1234, "Some program bla bla...", null);
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onUserCanceledJob(job);

		// after
		assertFalse("Convert button not disabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.converting));
	}

	@UiThreadTest
	public void testCallOnDownloadStartedForCurrentlyViewedProgramShouldSetConvertButtonTitleToDownloading() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + programData.getId() + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onDownloadStarted(downloadURL);

		// after
		assertFalse("Convert button not disabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(),
				activity.getString(R.string.status_downloading));
	}

	@UiThreadTest
	public void testCallOnDownloadStartedForDifferentProgramShouldNotSetConvertButtonTitleToDownloading() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final long otherJobID = 1234;
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + otherJobID + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);

		activity.onDownloadStarted(downloadURL);

		// after
		assertFalse("Convert button not disabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.converting));
	}

	@UiThreadTest
	public void testCallOnDownloadFinishedForCurrentlyViewedProgramShouldEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final String programTitle = programData.getTitle();
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + programData.getId() + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);

		activity.onDownloadFinished(programTitle, downloadURL);

		// after
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.convert));
	}

	@UiThreadTest
	public void testCallOnDownloadFinishedForDifferentProgramShouldNotEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final long otherJobID = 1234;
		final String programTitle = "Some program bla bla...";
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + otherJobID + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);

		activity.onDownloadFinished(programTitle, downloadURL);

		// after
		assertFalse("Convert button not disabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.status_downloading));
	}

	@UiThreadTest
	public void testCallOnUserCanceledDownloadForCurrentlyViewedProgramShouldEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + programData.getId() + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);

		activity.onUserCanceledDownload(downloadURL);

		// after
		assertTrue("Convert button not enabled!", convertButton.isEnabled());
		assertEquals("Convert button text did not change!", convertButton.getText(), activity.getString(R.string.convert));
	}

	@UiThreadTest
	public void testCallOnUserCanceledDownloadForDifferentProgramShouldNotEnableConvertButton() {
		final ScratchProgramDetailsActivity activity = getActivity();
		final long otherJobID = 1234;
		final String downloadURL = Constants.SCRATCH_CONVERTER_BASE_URL
				+ "/download?job_id=" + otherJobID + "&client_id=1&fname=My%20program";
		final Button convertButton = (Button) activity.findViewById(R.id.scratch_project_convert_button);

		// before
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);

		activity.onUserCanceledDownload(downloadURL);

		// after
		assertFalse("Convert button not disabled any more!", convertButton.isEnabled());
		assertEquals("Convert button text changed unexpectedly!", convertButton.getText(),
				activity.getString(R.string.status_downloading));
	}
}
