package com.together.ws.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.together.ws.exception.InvalidFamilyException;
import com.together.ws.service.UserService;
import com.together.ws.utils.JSONUtils;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint(value = "/chat", configurator = ServerConfigurator.class)
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

		userId = queryParams.get("user_id");

		try {
			userService.updateSession(userId, session);
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
			userService.removePendingMessages(userId);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		try {
			Map map = objectMapper.readValue(message, Map.class);
			userService.handleMessage(map, session.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}catch (InvalidFamilyException e){
			try {
				session.getBasicRemote().sendText("invalid family");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Method called when a connection is closed
	 * */
	@OnClose
	public void onClose(Session session) {
		String userId = userService.getUserBySessionId(session.getId());
		if(!StringUtils.isEmpty(userId)){
			userService.updateSession(userId, null);
		}
	}

}