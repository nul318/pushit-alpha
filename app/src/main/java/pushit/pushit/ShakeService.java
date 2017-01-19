package pushit.pushit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ShakeService extends Service implements SensorEventListener {
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    int SHAKE_THRESHOLD = 1000;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;
    SensorManager sensorManager;
    Sensor accelerormeterSensor;
    Handler handler = new Handler();

    String sender_id;
    String sender_name;
    String user_id;
    int cnt=0;

    // 서비스를 생성할 때 호출
    public void onCreate() {
        super.onCreate();
        Log.i("ShakeService", "Shake Service 시작");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }
//    public void onStart(Intent intent, int startId) {
//        // Log.e("MyService", "Service startId = " + startId);
//        super.onStart(intent, startId);
//        if (accelerormeterSensor != null)
//            sensorManager.registerListener(this, accelerormeterSensor,
//                    SensorManager.SENSOR_DELAY_GAME);
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime
                        * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    Log.e("감지", "흔듬감지됨");
                    cnt++;
                    try {
                        if(cnt>1) {
                            submit(user_id, sender_id);
                            cnt=-10000;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
        // }
    }
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);

        sender_id = intent.getStringExtra("sender_id");
        sender_name = intent.getStringExtra("sender_name");
        user_id = intent.getStringExtra("user_id");
        return startId;
    }


    private void submit(final String user_id, final String sender_id) throws UnsupportedEncodingException {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String URL = "http://dkstjs2.vps.phps.kr/push-it/friends_write/";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);

                //FCM
//                FirebaseMessaging.getInstance().subscribeToTopic("notice");
//                String token = FirebaseInstanceId.getInstance().getToken();
                //FCM


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                try {
                    nameValuePairs.add(new BasicNameValuePair("sender_id", URLEncoder.encode(sender_id, "UTF-8")));
                    nameValuePairs.add(new BasicNameValuePair("user_id", URLEncoder.encode(user_id, "UTF-8")));


//                    Log.i("user_name",URLEncoder.encode(user_name,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    Log.d("Http Post Request:", nameValuePairs.toString());
//                    Log.d("Http Post Response:", response.toString()); //서버 응답이 가공되지 않음

                    /*
                     * HttpResponse response 로 서버 응답 알아내는 코드
                     */
                    InputStream inputStream = response.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    final StringBuilder stringBuilder = new StringBuilder();
                    String bufferedStrChunk = null;
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }
                    Log.v("Http Post Response:", stringBuilder.toString());// 서버 응답

                    final String results = stringBuilder.toString().replaceAll("\\p{Z}", "");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(1000);

                            Intent intent = new Intent(ShakeService.this, BSCard.class);
                            //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ShakeService.this)
                                    .setSmallIcon(R.drawable.icon)
                                    .setContentTitle("Push It!")
                                    .setContentText(sender_name + " 님의 명함을 받았습니다.")
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setTicker("NotificationCompat.Builder")
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                    .setContentIntent(pendingIntent)
                                    .setPriority(Notification.PRIORITY_HIGH);
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

                            Toast.makeText(getApplicationContext(), "명함을 받았습니다.", Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }
            }
        }.start();
    }

}