package pushit.pushit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by gddjr on 2016-10-14.
 */

public class IOBitmap {

    public static HashMap<String, Bitmap> fetchImage(String path) {

        File file = new File(path);
        String imgName;

        int imgCount = file.listFiles().length;

        HashMap<String, Bitmap> ret_value = new HashMap<>();

        if(imgCount > 0) {
            for(File f : file.listFiles()) {
                imgName = f.getName();
                ret_value.put(imgName, BitmapFactory.decodeFile(path+imgName));
            }
        }

        return ret_value;
    }

    static Bitmap fetchOneImage(String path) {

        return BitmapFactory.decodeFile(path);
    }

    static Bitmap fetchOnImageByServer(String address) {

        Log.d("fetchOnImageByServer", address);

        try {
            URL url = new URL(address);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);

            conn.connect();

            return BitmapFactory.decodeStream(conn.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    static String getAbsoluteDirectory() {

        return Environment.getExternalStorageDirectory().toString();
    }

    static void storeImage(final String path, final String imgName, final Bitmap bitmap) {

        //make directory.

        File file = new File(path);

        if(!file.exists()) {

            file.mkdirs();
        }


        //store bitmap image.

        FileOutputStream out;

        try {
            out = new FileOutputStream(path + imgName + ".png");

            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
