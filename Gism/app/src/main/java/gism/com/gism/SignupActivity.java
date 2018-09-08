/*
package gism.com.gism;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gism.com.gism.utils.JSONUtils;
import gism.com.gism.utils.PersistenceUtils;
import gism.com.gism.utils.http.CreateUserTask;


public class SignupActivity extends ActionBarActivity {

    PersistenceUtils persistenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        persistenceUtils = new PersistenceUtils(this);

        String userId = persistenceUtils.getUserId();

        if(userId !=null && !userId.isEmpty()){
            //WSUtils.initialize(userId);
            Intent familyHomeIntent = new Intent(this, FamilyHomeActivity.class);
            startActivity(familyHomeIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void phoneNumberEntered(View view){
        EditText phone = (EditText)findViewById(R.id.user_identity);
        EditText userName = (EditText)findViewById(R.id.user_name);
        try {
            String phoneNumberTxt = phone.getText().toString();
            String userNameTxt = userName.getText().toString();
            AsyncTask userCreationMonitor = new CreateUserTask(phoneNumberTxt, userNameTxt).execute();
            Map<String, Object> response = (Map<String, Object>) userCreationMonitor.get();

            String userId = (String) response.get("user_id");
            persistenceUtils.storeUserId(userId);
            //WSUtils.initialize(userId);
            ArrayList<Map<String, String>> invites = (ArrayList<Map<String, String>>) response.get("invites");
            if(invites != null && !invites.isEmpty()){
                Intent joinFamilyIntent = new Intent(this, JoinFamilyActivity.class);
                joinFamilyIntent.putExtra("invites", JSONUtils.getInstance().toJSONString(invites));
                startActivity(joinFamilyIntent);
            }else {
                Intent createFamilyIntent = new Intent(this, CreateFamilyActivity.class);
                startActivity(createFamilyIntent);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
