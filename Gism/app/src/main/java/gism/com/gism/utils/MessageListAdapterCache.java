package gism.com.gism.utils;

import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 4/11/2017.
 */

public class MessageListAdapterCache {

    private static MessageListAdapterCache instance = new MessageListAdapterCache();
    private static Map<String, BaseAdapter> familyAreaAapterCache = new HashMap<>();

    private MessageListAdapterCache(){}

    public static MessageListAdapterCache getInstance(){
        if(instance == null){
            synchronized (instance){
                instance = new MessageListAdapterCache();
            }
        }
        return instance;
    }

    public void addAdapter(String family, String area, BaseAdapter adapter){
        familyAreaAapterCache.put(family+"_:_"+area, adapter);
    }

    public BaseAdapter getAdapter(String family, String area){
        return familyAreaAapterCache.get(family+"_:_"+area);
    }
}
