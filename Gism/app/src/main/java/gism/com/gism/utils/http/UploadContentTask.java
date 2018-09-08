package gism.com.gism.utils.http;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Sagar on 4/7/2017.
 */

public class UploadContentTask extends AsyncTask {

    byte[] content;

    String contentType;

    public UploadContentTask(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    protected Object doInBackground(Object[] params) {
        try {
            return HttpUtils.getInstance().uploadContent(content, contentType);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
