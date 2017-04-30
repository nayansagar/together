package together.com.homely.utils;

import android.util.Base64;
import android.widget.BaseAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 4/2/2017.
 */

public class WSUtils extends WebSocketClient {

    private static WSUtils wsUtils;
    private static String wsEndpoint = "ws://192.168.1.8:8080/chat?user_id=";
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
            Map<String, Object> messageMap = OBJECT_MAPPER.readValue(message, Map.class);
            String msgStr = (String) messageMap.get("message");
            byte[] msgContent = Base64.decode(msgStr, Base64.DEFAULT);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String family, String area, byte[] body){
        send(family, area, body, "text");
    }

    public void send(String family, String area, byte[] body, String messageType){

        MessageStore.getInstance().storeMessage(family, area, "self",
                GregorianCalendar.getInstance().toString(), messageType, body);

        String base64Body = Base64.encodeToString(body, Base64.DEFAULT);
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("family", family);
        message.put("body", base64Body);
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
