package gism.com.gism.utils.http;

import android.os.AsyncTask;

import java.io.IOException;

public class GetGroupActivitiesTask extends AsyncTask {

    String groupId;

    public GetGroupActivitiesTask(String groupId) {
        this.groupId = groupId;
    }

    protected Object doInBackground(Object[] params) {
        try {
            return HttpUtils.getInstance().getActivities(groupId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
