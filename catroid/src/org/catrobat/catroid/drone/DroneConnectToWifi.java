package org.catrobat.catroid.drone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.stage.PreStageActivity;

/**
 * Created by rahman on 23.07.15.
 */
public class DroneConnectToWifi extends AsyncTask<String, Void, Boolean> {

    private final PreStageActivity context;
    private DroneInitializer droneInitializer = null;
    WifiManager wifiManager;

    public DroneConnectToWifi(PreStageActivity context) {
        this.context = context;
    };

    @Override
    protected Boolean doInBackground(String... strings) {
        Integer networkID = Integer.parseInt(strings[0]);
        String ssid = strings[1];
        Log.d("DroneConnectToWifi", "network ID is = "+networkID);
        Log.d("DroneConnectToWifi", "ssid is = "+ssid);

        wifiManager = wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        wifiManager.enableNetwork(networkID, true);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return wifiManager.isWifiEnabled();
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        Log.d("DroneConectToWifi", "is connected "+bool);

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.d("DroneConnectToWifi", "before is connected");

        //Log.d("DroneConnectToWifi", mWifi.getReason().toString());
        Log.d("DroneConnectToWifi", mWifi.getDetailedState().toString());

        while (!mWifi.isConnected()) ;
        Log.d("DroneConnectToWifi", "is connected");
        CatroidApplication.loadNativeLibs();
        if (CatroidApplication.parrotLibrariesLoaded) {
            if (droneInitializer == null) {
                droneInitializer = new DroneInitializer(context);
            }

            droneInitializer.initialise();
        }


    }

}
