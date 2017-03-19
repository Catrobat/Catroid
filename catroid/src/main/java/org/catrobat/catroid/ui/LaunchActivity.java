package org.catrobat.catroid.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;

public class LaunchActivity extends Activity {

    static final int MY_PERMISSIONS_REQUEST = 11;
    static final String ACTIVITY= "Launch Activity";
    ImageView imAppLogo;
    Button btProceed;

    String permissions[] = {android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.GET_ACCOUNTS
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        imAppLogo = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(R.drawable.ic_launcher).into(imAppLogo);
        btProceed = (Button) findViewById(R.id.buttonProceed);


        Log.e(ACTIVITY,"Process Initiated...");

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){

            Log.e(ACTIVITY,"Proceed due to lower version");
            permissionGranted();//no need for permission in lower android version

        }

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                )
        {
            Log.e(ACTIVITY,"Proceed due to all permissions granted");
            permissionGranted();
        }


        btProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission();

            }
        });
    }

    private void checkPermission(){

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                 ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
        }else{

            //Permission Already Granted
            Log.e(ACTIVITY,"Proceed due to Already Granted");
            permissionGranted();


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    Log.e("Permission -- ","Granted!");

                    permissionGranted();

                } else {


                    Log.e("Permission -- ","Denied!");

                     DisplayMsg("Permissions Access are mandatory","Application can't work properly if you will not allow the application" +
                             " to use specific permissions. Click on continue and grant access to all the permissions.");
                }

            }

        }
    }

    private void DisplayMsg(String Title, String Message){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(Message)
                .setTitle(Title)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

        AlertDialog alert = adb.create();
        alert.show();
    }

    private void permissionGranted(){
        Intent it = new Intent(LaunchActivity.this,MainMenuActivity.class);
        startActivity(it);
        finish();
    }

}
