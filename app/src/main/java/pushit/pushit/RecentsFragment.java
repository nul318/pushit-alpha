package pushit.pushit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin on 2015-07-08.
 */
@SuppressLint("ValidFragment")
public class RecentsFragment extends Fragment {

    ArrayList<BSCard> bsCardArrayList;
    View v;

    String myIdx;

    private final String domain = "http://www.seonaireverse.com";



    static RecentsFragment newInstance(Bundle data) {

        RecentsFragment f = new RecentsFragment(data);
        return f;
    }

    @SuppressLint("ValidFragment")
    public RecentsFragment(Bundle data) {

        this.myIdx = data.getString("myIdx");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.business_card_list, container, false);




        setBsCardArrayList();

        RecentsList recents = (RecentsList) v.findViewById(R.id.business_card_list);
        recents.setAdapter(new RecentsAdapter()  {

            @Override
            public String getTitle(int position) {
                return "Item " + position;
            }

            @Override
            public View getView(int position) {

                ImageView iv = new ImageView(getActivity());
                Glide.with(getActivity()).load(bsCardArrayList.get(position).getImgUrl()).into(iv);
                iv.setBackgroundColor(0xffffffff);
                return iv;
            }

            @Override
            public Drawable getIcon(int position) {
                return getResources().getDrawable(R.mipmap.ic_launcher);
            }

            @Override
            public int getHeaderColor(int position) {
                return 0xffffffff;
            }

            @Override
            public int getCount() {
                return bsCardArrayList.size();
            }
        });

        recents.setOnItemClickListener(new RecentsList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent = new Intent(getActivity(), ViewBsCardDetailActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("phone_number", bsCardArrayList.get(i).getPhoneNumber());
                bundle.putString("image_url", bsCardArrayList.get(i).getImgUrl());

                intent.putExtra("data", bundle);

                startActivity(intent);
            }
        });

        return v;
    }

    private void setBsCardArrayList()
    {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                bsCardArrayList = new ArrayList<>();

                HashMap<String, String> keyValue = new HashMap<>();

                keyValue.put("myIdx", myIdx);

                String json = PhpUrlConnection.sendPOST(domain+"/push-it/load-info/load_bscard.php", keyValue);

                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for(int idx = 0; idx < jsonArray.length(); idx++)
                    {
                        JSONObject jObject = jsonArray.getJSONObject(idx);
                        bsCardArrayList.add(new BSCard((String) jObject.get("sender_bscard_url"+idx), (String) jObject.get("phone_number"+idx)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

