package together.com.homely.utils.http;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Sagar on 4/7/2017.
 */

public class DownloadContentTask extends AsyncTask {

    String contentId;

    public DownloadContentTask(String contentId) {
        this.contentId = contentId;
    }

    protected Object doInBackground(Object[] params) {
        try {
            return HttpUtils.getInstance().downloadContent(contentId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
