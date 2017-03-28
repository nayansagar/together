package com.together.ws.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.together.ws.exception.InvalidFamilyException;
import com.together.ws.service.UserService;
import com.together.ws.utils.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint(value = "/chat")
public class SocketServer {

	private UserService userService;

	public SocketServer(UserService userService){
		this.userService = userService;
	}

	// set to store all the live sessions
	private static final Set<Session> sessions = Collections
			.synchronizedSet(new HashSet<Session>());

	// Mapping between session and person name
	private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();

	private JSONUtils jsonUtils = new JSONUtils();

	private ObjectMapper objectMapper = new ObjectMapper();

	// Getting query params
	public static Map<String, String> getQueryMap(String query) {
		Map<String, String> map = Maps.newHashMap();
		if (query != null) {
			String[] params = query.split("&");
			for (String param : params) {
				String[] nameval = param.split("=");
				map.put(nameval[0], nameval[1]);
			}
		}
		return map;
	}

	/**
	 * Called when a socket connection opened
	 * */
	@OnOpen
	public void onOpen(Session session) {

		System.out.println(session.getId() + " has opened a connection");

		Map<String, String> queryParams = getQueryMap(session.getQueryString());

		String userId = "";

		if (!queryParams.containsKey("user_id") || StringUtils.isEmpty(queryParams.get("user_id"))) {
			try {
				session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "user_id query param missing"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			userService.updateSession(userId, session.getId());
		}catch (InvalidFamilyException e){
			try {
				session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "user_id "+userId+" invalid"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		List<Map<String, Object>> messages = userService.getPendingMessages(userId);

		try {
			String responseJson = objectMapper.writeValueAsString(messages);
			session.getBasicRemote().sendText(responseJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		userService.removePendingMessages(userId);

	}

	/**
	 * method called when new message received from any client
	 * 
	 * @param message
	 *            JSON message from client
	 * */
	@OnMessage
	public void onMessage(String message, Session session) {

		System.out.println("Message from " + session.getId() + ": " + message);

		String msg = null;

		// Parsing the json and getting message
		try {
			JSONObject jObj = new JSONObject(message);
			msg = jObj.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Sending the message to all clients
		sendMessageToAll(session.getId(), nameSessionPair.get(session.getId()),
				msg, false, false);
	}

	/**
	 * Method called when a connection is closed
	 * */
	@OnClose
	public void onClose(Session session) {
		
	}

	/**
	 * Method to send message to all clients
	 * 
	 * @param sessionId
	 * @param message
	 *            message to be sent to clients
	 * @param isNewClient
	 *            flag to identify that message is about new person joined
	 * @param isExit
	 *            flag to identify that a person left the conversation
	 * */
	private void sendMessageToAll(String sessionId, String name,
			String message, boolean isNewClient, boolean isExit) {

		// Looping through all the sessions and sending the message individually
		for (Session s : sessions) {
			String json = null;

			// Checking if the message is about new client joined
			if (isNewClient) {
				json = jsonUtils.getNewClientJson(sessionId, name, message,
						sessions.size());

			} else if (isExit) {
				// Checking if the person left the conversation
				json = jsonUtils.getClientExitJson(sessionId, name, message,
						sessions.size());
			} else {
				// Normal chat conversation message
				json = jsonUtils
						.getSendAllMessageJson(sessionId, name, message);
			}

			try {
				System.out.println("Sending Message To: " + sessionId + ", "
						+ json);

				s.getBasicRemote().sendText(json);
			} catch (IOException e) {
				System.out.println("error in sending. " + s.getId() + ", "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}
}