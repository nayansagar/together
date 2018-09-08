package gism.com.gism.utils.http;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Sagar on 4/7/2017.
 */

public class JoinFamilyTask extends AsyncTask {

    String userId;

    String familyId;

    public JoinFamilyTask(String userId, String familyId) {
        this.userId = userId;
        this.familyId = familyId;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            HttpUtils.getInstance().joinFamily(userId, familyId);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return false;
    }
}
