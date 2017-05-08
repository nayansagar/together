package together.com.homely.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 4/2/2017.
 */
public class MessageStore extends SQLiteOpenHelper {

    private static MessageStore messageStore;

    private static final String DB_NAME = "homely";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Constants.DB.TABLE_NAME + " (" +
                    Constants.DB._ID + " INTEGER PRIMARY KEY," +
                    Constants.DB.FAMILY + " TEXT," +
                    Constants.DB.AREA + " TEXT," +
                    Constants.DB.SENDER + " TEXT," +
                    Constants.DB.MESSAGE + " TEXT," +
                    Constants.DB.SENT_TIME + " TEXT," +
                    Constants.DB.MSG_TYPE + " TEXT" +
            " )";

    private MessageStore(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static void initialize(Context context){
        if(messageStore == null){
            messageStore = new MessageStore(context);
        }
    }

    public static MessageStore getInstance(){
        return messageStore;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeMessage(String family, String area, String sender, String sentAt, String type, String msgContent) {
        SQLiteDatabase db = messageStore.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.DB.FAMILY, family);
        values.put(Constants.DB.AREA, area);
        values.put(Constants.DB.SENDER, sender);
        values.put(Constants.DB.SENT_TIME, sentAt);
        values.put(Constants.DB.MSG_TYPE, type);
        values.put(Constants.DB.MESSAGE, msgContent);

        db.insert(Constants.DB.TABLE_NAME, null, values);
    }

    public List<Map<String, Object>> getMessages(String family, String area){
        SQLiteDatabase db = messageStore.getReadableDatabase();

        Cursor cursor = db.query(Constants.DB.TABLE_NAME,
                null,
                Constants.DB.FAMILY + "=? and "+Constants.DB.AREA + "=?",
                new String[] { family, area }, null, null, null, null);

        List<Map<String, Object>> messagesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> values = new HashMap<>();
                values.put(Constants.DB._ID, cursor.getString(0));
                values.put(Constants.DB.FAMILY, cursor.getString(1));
                values.put(Constants.DB.AREA, cursor.getString(2));
                values.put(Constants.DB.SENDER, cursor.getString(3));
                values.put(Constants.DB.SENT_TIME, cursor.getString(5));
                values.put(Constants.DB.MSG_TYPE, cursor.getString(6));
                values.put(Constants.DB.MESSAGE, cursor.getString(4));
                // Adding contact to list
                messagesList.add(values);
            } while (cursor.moveToNext());
        }
        return messagesList;
    }
}
