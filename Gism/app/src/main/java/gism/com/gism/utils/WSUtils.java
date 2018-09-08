package gism.com.gism.utils;

import android.widget.BaseAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 4/2/2017.
 */

public class WSUtils extends WebSocketClient {

    private static WSUtils wsUtils;
    private static String wsEndpoint = "ws://192.168.56.1:8080/chat?user_id=";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private WSUtils(URI serverURI) {
        super(serverURI, new Draft_17());
    }

    public static void initialize(String userId){
        URI uri = URI.create(wsEndpoint+userId);
        wsUtils = new WSUtils(uri);
        wsUtils.connect();
    }

    public static WSUtils getInstance(){
        return wsUtils;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        try {
            List<Map<String, Object>> pendingMessages = OBJECT_MAPPER.readValue(message, List.class);
            for(Map<String, Object> messageMap : pendingMessages){
                String msgContent = (String) messageMap.get("body");
                MessageStore.getInstance().storeMessage(
                        (String)messageMap.get("family"),
                        (String)messageMap.get("area"),
                        (String)messageMap.get("sender"),
                        (String)messageMap.get("sentAt"),
                        (String)messageMap.get("type"),
                        msgContent);
                BaseAdapter adapter = MessageListAdapterCache.getInstance().getAdapter((String)messageMap.get("family"),
                        (String)messageMap.get("area"));
                adapter.notifyDataSetChanged();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String family, String area, String body, String messageType){

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("family", family);
        message.put("body", body);
        message.put("area", area);
        message.put("type", messageType);
        try {
            super.send(JSONUtils.getInstance().toJSONString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BaseAdapter adapter = MessageListAdapterCache.getInstance().getAdapter(family,area);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}
