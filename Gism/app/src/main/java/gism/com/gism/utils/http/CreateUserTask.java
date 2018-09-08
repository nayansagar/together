package gism.com.gism.utils.http;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Sagar on 4/7/2017.
 */

public class CreateUserTask extends AsyncTask {

    String phone;

    String name;

    public CreateUserTask(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    protected Object doInBackground(Object[] params) {
        try {
            return HttpUtils.getInstance().createUser(phone, name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
