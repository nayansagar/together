package together.com.homely;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import together.com.homely.utils.JSONUtils;
import together.com.homely.utils.PersistenceUtils;
import together.com.homely.utils.http.CreateUserTask;


public class LoginActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private UiLifecycleHelper uiHelper;
    private String name, birthday, email, location;
    Session fbSession;
    PersistenceUtils persistenceUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        persistenceUtils = new PersistenceUtils(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.spouzee",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("@@@@@@@@@@@@KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));// g
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String userId = persistenceUtils.getUserId();

        if(userId !=null && !userId.isEmpty()){
            //WSUtils.initialize(userId);
            Intent familyHomeIntent = new Intent(this, FamilyHomeActivity.class);
            startActivity(familyHomeIntent);
        }else{
            setContentView(R.layout.activity_login);
        }
        /*getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getActionBar().hide();*/

    }

    private void openFacebookSession(final View view){
        fbSession = openActiveSession(this, true,
                Arrays.asList("public_profile", "email"), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    Log.d("Facebook", exception.getMessage());
                }
                Log.d("Facebook", "Session State: " + session.getState());
                if(fbSession.isOpened()){
                    Request.newMeRequest(fbSession, new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            name = getName(user);
                            birthday = user.getBirthday();
                            email = (String) user.getProperty("email");
                            location = getLocation(user);
                            Log.d("Name", name);
                            Log.d("Location", location);
                            Log.d("EMAIL_ID", email);
                            Log.d("FACEBOOK_ID",user.getId());
                            sendIntent(view);
                        }
                    }).executeAsync();
                    //sendIntent(view);
                }

                // you can make request to the /me API or do other stuff like post, etc. here
            }
        });
    }

    private static Session openActiveSession(Activity activity, boolean allowLoginUI, List permissions, Session.StatusCallback callback) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

    public void onFacebookLoginButtonPress(View view){
        openFacebookSession(view);
    }

    private void raiseToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void sendIntent(View view) {
        phoneNumberEntered(view);
        /*Intent startIntent = new Intent(this, StartActivity.class);
        startIntent.putExtra("name", name);
        startIntent.putExtra("email", email);
        startIntent.putExtra("location", location);
        startIntent.putExtra("birthday", birthday);

        startActivity(startIntent);*/
    }

    private String getLocation(GraphUser user) {
        String location=null;
        GraphPlace locationGP = (GraphPlace) user.getLocation();
        try {
            location = locationGP.getInnerJSONObject().getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }

    private String getName(GraphUser user) {
        StringBuilder name = new StringBuilder();
        if(user.getFirstName() != null && !user.getFirstName().isEmpty()){
            name.append(user.getFirstName()).append(" ");
        }
        if(user.getMiddleName() != null && !user.getMiddleName().isEmpty()){
            name.append(user.getMiddleName()).append(" ");
        }
        if(user.getLastName() != null && !user.getLastName().isEmpty()){
            name.append(user.getLastName());
        }
        return name.toString();
    }

    public void phoneNumberEntered(View view){
        try {
            AsyncTask userCreationMonitor = new CreateUserTask(email, name).execute();
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

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("LoginActivity", "Facebook session opened.");
            } else if (state.isClosed()) {
                Log.d("LoginActivity", "Facebook session closed.");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        //uiHelper.onSaveInstanceState(savedState);
    }
}
