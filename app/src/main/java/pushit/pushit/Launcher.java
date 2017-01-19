package pushit.pushit;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class Launcher extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

//        permissionCheck();

        getSupportActionBar().hide();
        activityStart();
    }

    protected void activityStart(){
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Intent intent = new Intent(Launcher.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void permissionCheck() {

        int permissionCheck;
        String permissions[] = {
                android.Manifest.permission.INTERNET, android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE };

        for (String permission : permissions) {

            permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            }
        }
    }
}
