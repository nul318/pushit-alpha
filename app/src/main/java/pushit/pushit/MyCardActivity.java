package pushit.pushit;


import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

public class MyCardActivity extends AppCompatActivity{

    ImageView cardView;
    RelativeLayout containerLayout;
    EditText phoneNumberET;

    HttpUtil httpUtil;
    HttpUtil cardPathHttpUtil;

    private final static float MIN_SLIDE = 300;



    private void permissionCheck(){
        int permissionCheck;

        permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.READ_SMS, android.Manifest.permission.INTERNET}, 0);
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_card_activity);
        getSupportActionBar().hide();

//        permissionCheck();


        containerLayout = (RelativeLayout)findViewById(R.id.container_layout);
        cardView = (ImageView)findViewById(R.id.card);

        phoneNumberET = (EditText)findViewById(R.id.phone_number_et);

        httpUtil = new HttpUtil(this);
        httpUtil.setUrl("send.php");
        httpUtil.setCallback(new HttpUtil.Callback() {
            @Override
            public void callback(String response) {

            }
        });

        cardPathHttpUtil = new HttpUtil(this);
        cardPathHttpUtil.setUrl("index.php")
                .setCallback(new HttpUtil.Callback() {
                    @Override
                    public void callback(String response) {
                        if(response != null && response.length() != 0){
                            Glide.with(MyCardActivity.this).load(response)
                                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                    .error(R.drawable.camera).into(cardView);
                        }
                    }
                });


        cardView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardView);
                    cardView.startDrag(data, shadowBuilder, cardView, 0);
                    return false;
                }
                return false;
            }
        });

        containerLayout.setOnDragListener(new View.OnDragListener() {

            float startY;
            float endY;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:{
                        Log.i("chat", "startedY:"+event.getY());
                        break;
                    }
                    case DragEvent.ACTION_DRAG_ENTERED: {
                        startY = event.getY();
                        Log.i("chat", "eneteredY:"+event.getY());
                        break;
                    }
                    case DragEvent.ACTION_DRAG_EXITED: {
                        Log.i("chat", "exitedY:"+event.getY());
                        endY = event.getY();
                        if (startY - endY > MIN_SLIDE) {
                            if (phoneNumberET.getText().toString().equals("")) {
                                httpUtil.setData("user_id", getIntent().getStringExtra("user_id"))
                                        .setData("sender_name", getIntent().getStringExtra("name"))
                                        .postData();
                            } else {
                                sendMMS(Environment.getExternalStorageDirectory().toString() + "/Push_It/myCard/card.png",
                                        phoneNumberET.getText().toString());
                            }
                            Toast.makeText(MyCardActivity.this, "명함을 보냈습니다", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case DragEvent.ACTION_DROP:{
                        Log.i("chat", "dropY:"+event.getY());
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cardPathHttpUtil.setData("user_id", getIntent().getStringExtra("user_id"))
                .postData();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.cardList : {
                Intent intent = new Intent(MyCardActivity.this, BSCardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("myIdx", getIntent().getStringExtra("user_id"));

                intent.putExtra("data", bundle);

                startActivity(intent);
                break;
            }
            case R.id.from_camera : {
                break;
            }
            case R.id.from_template : {
                Intent intent;
                String user_email = getIntent().getStringExtra("email");
                String user_id = getIntent().getStringExtra("user_id");
                String user_name = getIntent().getStringExtra("name");
                String user_image = getIntent().getStringExtra("image_path");

                String phone_number = getIntent().getStringExtra("phone_number");
                String company_address = getIntent().getStringExtra("company_address");
                String company_name = getIntent().getStringExtra("company_name");
                String company_rank = getIntent().getStringExtra("company_rank");

                intent = new Intent(MyCardActivity.this, AddCard.class);

                intent.putExtra("email", user_email);
                intent.putExtra("user_id", user_id);
                intent.putExtra("name", user_name);
                intent.putExtra("image_path", user_image);

                intent.putExtra("phone_number", phone_number);
                intent.putExtra("user_id", user_id);
                intent.putExtra("company_address", company_address);
                intent.putExtra("company_name", company_name);
                intent.putExtra("company_rank", company_rank);


//                                intent.putExtra("gender", user_gender);
//                                intent.putExtra("age", user_age);

                startActivity(intent);
                break;
            }
        }
    }

    private void sendMMS(String path, String phoneNumber){
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType ("image/*");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("exit_on_sent", true);
        intent.putExtra("address", phoneNumber);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        Log.e("sendMMS", uri.toString());
        startActivity(intent);

    }

}
