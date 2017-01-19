package pushit.pushit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseReceiveService extends FirebaseMessagingService implements SensorEventListener {

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 1000; // 작을 수록 느린 스피드에서도 감지를 한다.

    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    private TextView textview=null;
    private int count;

    private PowerManager pm; // 조명 유지
    private PowerManager.WakeLock wl; // 조명 유지
    Handler handler = new Handler();




    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("pushit.pushit.shakeservice".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Log.i("image_path", remoteMessage.getData().get("image_path"));
        Log.i("receiver_id", remoteMessage.getData().get("receiver_id"));
        Log.i("sender_id", remoteMessage.getData().get("sender_id"));
        Log.i("sender_name", remoteMessage.getData().get("sender_name"));
//        String image_path="image_path";

        String sender_id=remoteMessage.getData().get("sender_id");
        String sender_name=remoteMessage.getData().get("sender_name");
        String user_id=remoteMessage.getData().get("receiver_id");



        if(!isServiceRunningCheck()) {
            Intent intent = new Intent();
            intent.putExtra("sender_id", sender_id);
            intent.putExtra("sender_name", sender_name);
            intent.putExtra("user_id", user_id);

            intent.setPackage("pushit.pushit");
            startService(intent);
        }

        final Intent intent = new Intent(this, ShakeService.class);
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopService(intent);
                    }
                });
            }
        }.start();


    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 정확도 설정
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Sensor 정보가 변하면 실행됨.
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            //최근 측정한 시간과 현재 시간을 비교하여 0.1초 이상되었을 때 흔듬을 감지한다.
            if(gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ)/gabOfTime * 10000;

                if(speed>SHAKE_THRESHOLD) {
                    // 이벤트 발생!!
//                    textview = (TextView)findViewById(R.id.mainTextView);
//                    textview.setText(Integer.toString(++count));
                    Log.i("count", Integer.toString(++count));
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }



}

