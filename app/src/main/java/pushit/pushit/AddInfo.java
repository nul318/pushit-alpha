package pushit.pushit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

public class AddInfo extends AppCompatActivity {
    Handler handler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        getSupportActionBar().hide();

        TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String PhoneNumber = systemService.getLine1Number();    //폰번호를 가져오는 겁니다..
        PhoneNumber = PhoneNumber.substring(PhoneNumber.length() - 10, PhoneNumber.length());
        PhoneNumber = "0" + PhoneNumber;
//        Toast.makeText(getApplicationContext(),PhoneNumber, Toast.LENGTH_SHORT).show();

        final TextView phone_number_txt = (TextView) findViewById(R.id.add_info_phonenumber);
        phone_number_txt.setText(PhoneNumber);

        final String phone_number = PhoneNumber;

        Button submit = (Button) findViewById(R.id.add_info_submit);

        final String user_id = getIntent().getStringExtra("user_id");

        final EditText company_name_ed = (EditText) findViewById(R.id.add_info_company_name);
        final EditText company_address_ed = (EditText) findViewById(R.id.add_info_company_address);
        final EditText company_rank_ed = (EditText) findViewById(R.id.add_info_company_rank);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    final String company_name = company_name_ed.getText().toString();
                    final String company_address = company_address_ed.getText().toString();
                    final String company_rank = company_rank_ed.getText().toString();
                    Log.i("company_name" , company_name);

                    submit(phone_number, company_name, company_address, company_rank, user_id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        };

        submit.setOnClickListener(listener);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void submit(final String phone_number, final String company_name, final String company_address, final String company_rank, final String user_id) throws UnsupportedEncodingException {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String URL = "http://dkstjs2.vps.phps.kr/push-it/add-info/";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);

//                //FCM
//                FirebaseMessaging.getInstance().subscribeToTopic("notice");
//                String token = FirebaseInstanceId.getInstance().getToken();
//                //FCM


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                try {
                    nameValuePairs.add(new BasicNameValuePair("phone_number", URLEncoder.encode(phone_number, "UTF-8")));
                    nameValuePairs.add(new BasicNameValuePair("user_id", URLEncoder.encode(user_id, "UTF-8")));
                    nameValuePairs.add(new BasicNameValuePair("company_address", URLEncoder.encode(company_address, "UTF-8")));
                    nameValuePairs.add(new BasicNameValuePair("company", URLEncoder.encode(company_name, "UTF-8")));
                    nameValuePairs.add(new BasicNameValuePair("company_rank", URLEncoder.encode(company_rank, "UTF-8")));
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

                    //-------------------------------------------------------------------
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Intent intent;

                            String user_email = getIntent().getStringExtra("email");
                            String user_name = getIntent().getStringExtra("name");
                            String user_image = getIntent().getStringExtra("image_path");
                            Log.i("image__path", user_image);

                            if (results.equals("true")) {



                                intent = new Intent(AddInfo.this, MyCardActivity.class);
                                intent.putExtra("phone_number", phone_number);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("company_address", company_address);
                                intent.putExtra("company_name", company_name);
                                intent.putExtra("company_rank", company_rank);

                                intent.putExtra("email", user_email);
                                intent.putExtra("name", user_name);
                                intent.putExtra("image_path", user_image);


                                startActivity(intent);

                                finish();
                            }

                        }
                    });


                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddInfo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
