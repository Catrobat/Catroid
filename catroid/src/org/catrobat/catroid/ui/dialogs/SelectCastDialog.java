package org.catrobat.catroid.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.media.MediaRouter;
import android.widget.ArrayAdapter;

import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;

/**
 * Created by davidwittenbrink on 10.02.16.
 */
public class SelectCastDialog extends DialogFragment {

    private static final String dialog_tag = "cast_device_selector";
    ArrayAdapter<String> deviceAdapter;
    Context context;

    public void openDialog(Activity activity, ArrayAdapter<String> deviceAdapter) {
        this.deviceAdapter = deviceAdapter;
        context = activity;
        show(activity.getFragmentManager(), dialog_tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.cast_device_selector_dialog_title))
                .setAdapter(deviceAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                synchronized (this) {
                    MediaRouter.RouteInfo routeInfo = CastManager.getInstance().getRouteInfos().get(which);
                    CastManager.getInstance().selectRoute(routeInfo);
                }
            }
        });
        return builder.create();
    }

}
