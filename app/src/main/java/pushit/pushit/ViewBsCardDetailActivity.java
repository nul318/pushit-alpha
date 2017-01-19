package pushit.pushit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

public class ViewBsCardDetailActivity extends AppCompatActivity {

    ImageView imageView;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bs_card_detail);

        final String phone_number;
        String image_url;

        Bundle data = getIntent().getBundleExtra("data");

        phone_number = data.getString("phone_number");
        image_url = data.getString("image_url");

        imageView = (ImageView) findViewById(R.id.iv_bscard_detail);
        imageButton = (ImageButton) findViewById(R.id.img_btn_call);

//        Glide.with(getApplicationContext()).load(image_url).into(imageView);
        Glide.with(getApplicationContext()).load(image_url)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("response", "tel:"+phone_number);
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+phone_number)));
            }
        });
    }
}