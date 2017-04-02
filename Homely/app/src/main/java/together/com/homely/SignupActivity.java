package together.com.homely;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import together.com.homely.utils.Constants;
import together.com.homely.utils.HttpUtils;


public class SignupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
            Map<String, Object> response = HttpUtils.getInstance().createUser(
                    phone.getText().toString(), userName.getText().toString());

            storeUserId((String) response.get("user_id"));

            ArrayList<Map<String, String>> invites = (ArrayList<Map<String, String>>) response.get("invites");
            if(invites != null && !invites.isEmpty()){
                Intent joinFamilyIntent = new Intent(this, JoinFamilyActivity.class);
                joinFamilyIntent.putExtra("invites", invites);
                startActivity(joinFamilyIntent);
            }else {
                Intent createFamilyIntent = new Intent(this, CreateFamilyActivity.class);
                startActivity(createFamilyIntent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeUserId(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.HOMELY, MODE_PRIVATE).edit();
        editor.putString("user_id", userId);
        editor.apply();
    }
}
