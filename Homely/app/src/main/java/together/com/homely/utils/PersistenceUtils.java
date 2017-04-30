package together.com.homely.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sagar on 4/10/2017.
 */

public class PersistenceUtils {

    public static final String ADMIN_FAMILY_ID = "admin_family_id";
    public static final String USER_ID = "user_id";
    private Context context;
        private SharedPreferences sharedPref;

        private static final String KEY_SHARED_PREF = Constants.HOMELY;
        private static final int KEY_MODE_PRIVATE = 0;
        private static final String KEY_SESSION_ID = "sessionId",
                FLAG_MESSAGE = "message";

        public PersistenceUtils(Context context) {
            this.context = context;
            sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
                    KEY_MODE_PRIVATE);
        }

        public void storeSessionId(String sessionId) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(KEY_SESSION_ID, sessionId);
            editor.commit();
        }

        public String getSessionId() {
            return sharedPref.getString(KEY_SESSION_ID, null);
        }

    public void storeFamilyId(String familyId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ADMIN_FAMILY_ID, familyId);
        editor.apply();
    }

    public String getFamilyId() {
        return sharedPref.getString(ADMIN_FAMILY_ID, null);
    }

    public void storeUserId(String userId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPref.getString(USER_ID, null);
    }

        public String getSendMessageJSON(String message) {
            String json = null;

            try {
                JSONObject jObj = new JSONObject();
                jObj.put("flag", FLAG_MESSAGE);
                jObj.put("sessionId", getSessionId());
                jObj.put("message", message);

                json = jObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
}
