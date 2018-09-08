package gism.com.gism;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gism.com.gism.utils.JSONUtils;
import gism.com.gism.utils.PersistenceUtils;
import gism.com.gism.utils.http.CreateUserTask;


public class LoginActivity extends Activity {

    PersistenceUtils persistenceUtils;
    CallbackManager callbackManager;

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
        //String userId = null;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(userId !=null && !userId.isEmpty()){
            //WSUtils.initialize(userId);
            Intent familyHomeIntent = new Intent(this, FamilyHomeActivity.class);
            startActivity(familyHomeIntent);
        }else{

            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FBCallback(this));
            setContentView(R.layout.activity_login);
        }

    }

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        //uiHelper.onSaveInstanceState(savedState);
    }

    class FBCallback implements FacebookCallback<LoginResult> {

        private Activity activity;

        public FBCallback(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            System.out.println(loginResult.getAccessToken());
            if(loginResult.getAccessToken() !=null && !loginResult.getAccessToken().isExpired()){
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                try {
                                    phoneNumberEntered(response.getJSONObject().getString("name"),
                                            response.getJSONObject().getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields" +
                        "", "id,name,link,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }
        }

        public void phoneNumberEntered(String name, String email){
            try {
                AsyncTask userCreationMonitor = new CreateUserTask(email, name).execute();
                Map<String, Object> response = (Map<String, Object>) userCreationMonitor.get();

                String userId = (String) response.get("user_id");
                persistenceUtils.storeUserId(userId);
                //WSUtils.initialize(userId);
                ArrayList<Map<String, String>> invites = (ArrayList<Map<String, String>>) response.get("invites");
                if(invites != null && !invites.isEmpty()){
                    Intent joinFamilyIntent = new Intent(activity, JoinFamilyActivity.class);
                    joinFamilyIntent.putExtra("invites", JSONUtils.getInstance().toJSONString(invites));
                    startActivity(joinFamilyIntent);
                }else {
                    Intent createFamilyIntent = new Intent(activity, CreateFamilyActivity.class);
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

        @Override
        public void onCancel() {
            // App code
        }

        @Override
        public void onError(FacebookException exception) {
            exception.printStackTrace();
        }
    }
}
