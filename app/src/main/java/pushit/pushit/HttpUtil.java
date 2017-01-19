package pushit.pushit;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class HttpUtil {

    private HashMap<String, String> postData;
    private String url;
    private Callback callback;
    private Post post = null;
    private HttpURLConnection conn;
    private final String fixedUrl = "http://dkstjs2.vps.phps.kr/push-it/fcm/";

    public HttpUtil(Context context){
        postData = new HashMap<>();
    }

    public HttpUtil setUrl(String url){
        this.url = fixedUrl + url;
        return this;
    }

    public HttpUtil setData(String key, String value){
        postData.put(key, value);
        return this;
    }

    public HttpUtil setCallback(Callback callback){
        this.callback = callback;
        return this;
    }

    public void postData(){
        post = null;
        post = new Post();
        post.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class Post extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            return executePost(url, postData);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            callback.callback(s);
        }
    }

    private String executePost(String requestURL, HashMap<String, String> postData){
        URL url;
        String response ="";
        try{
            url = new URL(requestURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            if(postData.size() != 0){
                writer.write(getPostDataString(postData));
            }
            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response = "";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        Uri.Builder builder = new Uri.Builder();
        for(Map.Entry<String, String> entry : params.entrySet()){
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getEncodedQuery();
    }

    public interface Callback {
        void callback(String response);
    }

}