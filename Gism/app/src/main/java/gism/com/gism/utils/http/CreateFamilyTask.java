package gism.com.gism.utils.http;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 4/7/2017.
 */

public class CreateFamilyTask extends AsyncTask {

    private String userId;

    private String familyName;

    private List<Map<String, String>> invites;

    public CreateFamilyTask(String userId, String familyName, List<Map<String, String>> invites) {
        this.userId = userId;
        this.familyName = familyName;
        this.invites = invites;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            return HttpUtils.getInstance().createFamily(userId, familyName, invites);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
