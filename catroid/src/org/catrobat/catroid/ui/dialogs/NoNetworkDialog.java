package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;

import java.util.ArrayList;

public class NoNetworkDialog extends AlertDialog {

	private ArrayList<Brick> brickList;

	public NoNetworkDialog(Context context) {
		super(context);
	}

	protected NoNetworkDialog(Context context, int theme) {
		super(context, theme);
	}

	protected NoNetworkDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public void setBrickList(ArrayList<Brick> brickList) {
		this.brickList = brickList;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_error_networkconnection, null);
		LinearLayout imageLayout = (LinearLayout) dialogLayout
				.findViewById(R.id.dialog_error_network_brickimages_layout);

		for (Brick networkBrick : brickList) {
			imageLayout.addView(networkBrick.getPrototypeView(getContext()));
		}

		setTitle(R.string.error_no_network_title);
		if (brickList.size() > 1) {
			TextView brickDescription = (TextView) dialogLayout.findViewById(R.id.dialog_text_headtext_bricks);
			brickDescription.setText(this.getContext().getText(R.string.error_no_network_multiple_bricks));
		}
		setCancelable(false);
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel_button),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				});
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.main_menu_settings),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(Settings.ACTION_SETTINGS);
						getContext().startActivity(i);
					}
				});
		setView(dialogLayout);
		super.onCreate(savedInstanceState);
	}
}
