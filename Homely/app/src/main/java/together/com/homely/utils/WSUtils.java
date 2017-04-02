package together.com.homely.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by Sagar on 4/2/2017.
 */

public class WSUtils extends WebSocketClient {

    private static WSUtils wsUtils;
    private static String wsEndpoint = "ws://localhost:8181/chat?user_id=";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private WSUtils(URI serverURI) {
        super(serverURI);
    }

    public static void initialize(String userId){
        URI uri = URI.create(wsEndpoint+userId);
        wsUtils = new WSUtils(uri);
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
            String msgContent = OBJECT_MAPPER.writeValueAsString(messageMap.get("message"));
            MessageStore.getInstance().storeMessage(
                    (String)messageMap.get("family"),
                    (String)messageMap.get("sender"),
                    (String)messageMap.get("sentAt"),
                    msgContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}
