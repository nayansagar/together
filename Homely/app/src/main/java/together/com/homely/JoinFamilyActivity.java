package together.com.homely;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import together.com.homely.adapter.FamilyListAdapter;
import together.com.homely.utils.Constants;
import together.com.homely.utils.JSONUtils;
import together.com.homely.utils.PersistenceUtils;


public class JoinFamilyActivity extends ActionBarActivity {

    private ArrayList<Map<String, String>> invites;

    private FamilyListAdapter familyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_family);

        Intent intent = getIntent();
        String invitesJSON = intent.getStringExtra("invites");
        try {
            invites = (ArrayList<Map<String, String>>) JSONUtils.getInstance().jsonStringToObject(invitesJSON, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PersistenceUtils persistenceUtils = new PersistenceUtils(this);
        familyListAdapter = new FamilyListAdapter(this, R.layout.invite_row, invites, persistenceUtils.getUserId());
    }

    public void gotoFamilyHome(View view){
        Intent familyHomeIntent = new Intent(this, FamilyHomeActivity.class);
        startActivity(familyHomeIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_family, menu);
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
}
