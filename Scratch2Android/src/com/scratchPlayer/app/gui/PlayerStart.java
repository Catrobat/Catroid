package com.scratchPlayer.app.gui;



import java.io.File;
import java.io.IOException;

import com.scratchPlayer.app.R;
import com.scratchPlayer.app.R.id;
import com.scratchPlayer.app.R.layout;
import com.scratchPlayer.app.objectReader.ProjectFile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class PlayerStart extends Activity implements OnClickListener {
	TextView projectFileText, comment, author, history, scratchVersion;
	Button fileOpenButton, startButton;
	int PICK_REQUEST_CODE = 0;
	ProjectFileParams projectFileParams = null;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start);

		projectFileParams = new ProjectFileParams();

		projectFileText = (TextView) this.findViewById(R.id.projectFileText);
		 projectFileText.setVisibility(TextView.GONE); // hide text

		comment = (TextView) this.findViewById(R.id.projFileComment);
		 comment.setVisibility(TextView.GONE); // hide text

		author = (TextView) this.findViewById(R.id.projFileAuthor);
		 author.setVisibility(TextView.GONE); // hide text

		history = (TextView) this.findViewById(R.id.projFileHistory);
		 history.setVisibility(TextView.GONE); // hide text

		scratchVersion = (TextView) this.findViewById(R.id.projFileVersion);
		 scratchVersion.setVisibility(TextView.GONE); // hide text

		fileOpenButton = (Button) this.findViewById(R.id.FileOpenButton);
		fileOpenButton.setOnClickListener(this);
		
		startButton = (Button) this.findViewById(R.id.startStageButton);
		startButton.setOnClickListener(this);
		
	}

	public void onClick(View v) {
		if (v.getId() == fileOpenButton.getId()) {
			System.out.println("in onClick - fileOpenButton");
			projectFileText.setText("Clicked");
			Intent fileBrowser = new Intent(PlayerStart.this,
					com.scratchPlayer.app.gui.ListScratchFiles.class);
			startActivityForResult(fileBrowser, 0);
			System.out.println("end onClick - fileOpenButton");
		}
		if (v.getId() == startButton.getId()) {
			Intent stage = new Intent(this,
					com.scratchPlayer.app.stage.RunProject.class);
			startActivity(stage);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// todo: Abfrage wenn zur�ckbutton bet�tigt!!
		ProjectFileParams.filePath = data.getExtras().getString("filePath");

		try {
			ProjectFileParams.project = new ProjectFile(new File(
					ProjectFileParams.filePath));
		} catch (IOException e) {
			ProjectFileParams.filePath = null;
			projectFileText.setText("Fehler beim �ffnen des Files!");
			projectFileText.setVisibility(0); // view text
			return;
		}

		comment.setText("Kommentar:\n"
				+ ProjectFileParams.project.getInfo("comment"));
		comment.setVisibility(TextView.VISIBLE); // view text

		author
				.setText("Autor:\n"
						+ ProjectFileParams.project.getInfo("author"));
		author.setVisibility(TextView.VISIBLE); // view text

		history.setText("History:\n"
				+ ProjectFileParams.project.getInfo("history"));
		history.setVisibility(TextView.VISIBLE); // view text

		scratchVersion.setText("Scratch Version:\n"
				+ ProjectFileParams.project.getInfo("scratch-version"));
		scratchVersion.setVisibility(TextView.VISIBLE); // view text

		projectFileText.setText("Prokekt:\n" + ProjectFileParams.filePath);

	}
	
	

}