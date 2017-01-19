package pushit.pushit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class AddCard extends AppCompatActivity {
    String user_id, user_image, user_name, user_rank, user_email, user_company, user_phone, user_tel, user_address;

    public class CustomBitmapPool implements BitmapPool {
        @Override
        public int getMaxSize() {
            return 0;
        }

        @Override
        public void setSizeMultiplier(float sizeMultiplier) {

        }

        @Override
        public boolean put(Bitmap bitmap) {
            return false;
        }

        @Override
        public Bitmap get(int width, int height, Bitmap.Config config) {
            return null;
        }

        @Override
        public Bitmap getDirty(int width, int height, Bitmap.Config config) {
            return null;
        }

        @Override
        public void clearMemory() {

        }

        @Override
        public void trimMemory(int level) {

        }
    }


    private void fileUpload(final String uri) throws UnsupportedEncodingException {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                File path = new File(uri);
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://dkstjs2.vps.phps.kr/push-it/uploads/imageUpload.php");

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody bin = new FileBody(path);

                try {
                    entityBuilder.addPart("image", bin);
                    entityBuilder.addPart("user_id", new StringBody(user_id)); //POST 는 이런식으로 보내셈
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                HttpEntity entity = entityBuilder.build();

//                entity.addPart("posting_id", new StringBody(postDataParams.get("posting_id")));
                post.setEntity(entity);
                try {
                    HttpResponse response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();

                    String result = EntityUtils.toString(httpEntity);
                    Log.i("result", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AddCard.this.finish();

            }
        }.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_id = getIntent().getStringExtra("user_id");
        user_image = getIntent().getStringExtra("image_path");
        user_name = getIntent().getStringExtra("name");
        user_rank = getIntent().getStringExtra("company_rank");
        user_email = getIntent().getStringExtra("email");
        user_company = getIntent().getStringExtra("company_name");
        user_phone = getIntent().getStringExtra("phone_number");
        user_address = getIntent().getStringExtra("company_address");
        setContentView(R.layout.addcard);
        final LinearLayout cardLay = (LinearLayout) findViewById(R.id.Card_Layout);
        ImageView test = (ImageView) findViewById(R.id.btn);
        TextView name = (TextView) findViewById(R.id.txtName);
        TextView rank = (TextView) findViewById(R.id.txtRank);
        TextView email = (TextView) findViewById(R.id.txtEmail);
        TextView office = (TextView) findViewById(R.id.txtOffice);
        TextView phone = (TextView) findViewById(R.id.txtPhone);
        TextView address = (TextView) findViewById(R.id.txtAddress);
        ImageView image = (ImageView) findViewById(R.id.profileImage);

        name.setText(user_name);
        name.setSingleLine(true); //한줄로 나오게 하기.
        name.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        name.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기
        rank.setText(user_rank);
        rank.setSingleLine(true); //한줄로 나오게 하기.
        rank.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        rank.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기
        email.setText(Html.fromHtml("<u>" + user_email + "</u>"));
        email.setSingleLine(true); //한줄로 나오게 하기.
        email.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        email.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기
        office.setText(Html.fromHtml("<u>" + user_company + "</u>"));
        office.setSingleLine(true); //한줄로 나오게 하기.
        office.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        office.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기
        phone.setText(Html.fromHtml("<u>" + user_phone + "</u>"));
        phone.setSingleLine(true); //한줄로 나오게 하기.
        phone.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        phone.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        address.setText(Html.fromHtml("<u>" + user_address + "</u>"));
        address.setSingleLine(true); //한줄로 나오게 하기.
        address.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        address.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        Glide.with(this).load(user_image).bitmapTransform(
                new CropCircleTransformation(new CustomBitmapPool())).override(600, 400).into(image);



        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString() + "/Push_It/myCard/";
                File fileAsUploadPath = new File(path);
                if (!fileAsUploadPath.exists()) {
                    fileAsUploadPath.mkdirs();
                }
                cardLay.setDrawingCacheEnabled(true);
                cardLay.buildDrawingCache(true);
                Bitmap saveBm = Bitmap.createBitmap(cardLay.getDrawingCache());
                try {
                    path += "card.png";
                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    saveBm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileUpload(path);
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    cardLay.destroyDrawingCache();
                }
            }
        });
    }
}
