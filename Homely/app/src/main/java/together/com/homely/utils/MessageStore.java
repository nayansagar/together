package together.com.homely.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                    Constants.DB.SENDER + " TEXT," +
                    Constants.DB.MESSAGE + " TEXT," +
                    Constants.DB.SENT_TIME + " TEXT," +
            ")";

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

    public void storeMessage(String family, String sender, String sentAt, String msgContent) {
        SQLiteDatabase db = messageStore.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.DB.FAMILY, family);
        values.put(Constants.DB.SENDER, sender);
        values.put(Constants.DB.SENT_TIME, sentAt);
        values.put(Constants.DB.MESSAGE, msgContent);

        db.insert(Constants.DB.TABLE_NAME, null, values);
    }
}
