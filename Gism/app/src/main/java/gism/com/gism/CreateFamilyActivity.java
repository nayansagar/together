package gism.com.gism;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import gism.com.gism.utils.PersistenceUtils;
import gism.com.gism.utils.http.CreateFamilyTask;


public class CreateFamilyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static final int PICK_CONTACT = 1000;

    private List<Map<String, String>> invitesList = new ArrayList<>();

    private List<String> invitedNames = new ArrayList<>();

    ArrayAdapter<String> invitedNamesAdapter;

    private PersistenceUtils persistenceUtils;

    private String groupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family);

        Spinner spinner = (Spinner) findViewById(R.id.group_type);
        spinner.setOnItemSelectedListener(this);

        invitedNamesAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                invitedNames);
        ListView list = (ListView) findViewById(R.id.invites_sent);
        list.setAdapter(invitedNamesAdapter);

        persistenceUtils = new PersistenceUtils(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_family, menu);
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

    public void uploadInvites(View view){
        String userId = persistenceUtils.getUserId();
        EditText familyName = (EditText)findViewById(R.id.family_name);
        String fName = familyName.getText().toString();
        try {
            AsyncTask familyCreationMonitor = new CreateFamilyTask(userId, fName, invitesList).execute();
            String familyId = (String) familyCreationMonitor.get();
            persistenceUtils.storeFamilyId(familyId);
            Intent familyHomeIntent = new Intent(this, FamilyHomeActivity.class);
            startActivity(familyHomeIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void inviteContact(View view){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if(reqCode != PICK_CONTACT){
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            Cursor cursor = null;
            try {
                String phoneNo = null ;
                String name = null;
                // getData() method will have the Content Uri of the selected contact
                Uri uri = data.getData();
                //Query the content uri
                cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                // column index of the phone number
                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                // column index of the contact name
                int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
                // Set the value to the textviews
                EditText familyName = (EditText)findViewById(R.id.family_name);
                String joinCode = getRandomString(6);
                sendSMS(name, phoneNo, familyName.getText().toString(), String.valueOf(R.string.invite_message), joinCode);
                addToInvitesList(name, phoneNo, joinCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void addToInvitesList(String name, String phoneNo, String joinCode) {
        Map<String, String> inviteInfo = new HashMap<>();
        inviteInfo.put("name", name);
        inviteInfo.put("phone", phoneNo);
        inviteInfo.put("relationship", groupType);
        inviteInfo.put("jcode", joinCode);

        this.invitesList.add(inviteInfo);
        this.invitedNames.add(name);
        invitedNamesAdapter.notifyDataSetChanged();
    }

    public void sendSMS(String name, String phoneNo, String familyName, String msg, String joinCode) {
        try {
            String finalMessage = String.format(msg, name, familyName, "dummyURL", joinCode);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, finalMessage, null, null);
            Toast.makeText(getApplicationContext(), "Invite Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        String[] groupTypeArray = getApplicationContext().getResources().getStringArray(R.array.group_types);
        groupType = groupTypeArray[pos];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
