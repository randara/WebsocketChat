package com.example;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import java.lang.Exception;
import java.util.*;

/**
 * Created by rafaelandara on 4/27/15.
 */
public class ChatServer extends WebSocketServer {

    private static int PORT = 8887;

    private Map<String, WebSocket> users;

    private MultiMap<String, String> contactsList;


    public ChatServer() {
        super(new InetSocketAddress(PORT));
        init();
    }

    // Initialize user control structure on server. Needs to be persisted.

    public void init() {
        users = new HashMap<>();
        contactsList = new MultiValueMap<>();
    }

    // Message handler. Important.

    @Override
    public void onMessage(WebSocket socket, String json) {

        Message msg = ChatUtils.parseMessage(json);
        ArrayList<Message> responses = getMessagesToSend(socket, msg);

        for (Message m : responses) {

            WebSocket to;

            if (m.getReceiver() == null || !contactsList.containsValue(m.getReceiver())) {
                to = socket;
            }
            else {
                 to = users.get(m.getReceiver());
            }
                to.send(ChatUtils.prepareJson(m));
        }

    }

    @Override
    public void onError(WebSocket socket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onClose(WebSocket socket, int code, String message, boolean status) {
        // TODO
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake client) {
        // TODO
    }

    // Contacts List methods

    public void addToContactsList(String userName, String contactName) {
        contactsList.put(userName, contactName);
    }

    public void removeFromContactsList(String userName) {
        contactsList.remove(userName);
    }

    public ArrayList<String> getContactsList(String userName) {
        ArrayList<String> contacts = new ArrayList<>();
        if (!contactsList.isEmpty()) {
            contacts = (ArrayList<String>) contactsList.get(userName);
        }
        return contacts;
    }

    // Online users (connections) methods

    public void addOnlineUsers(String userName, WebSocket conn) {
        users.put(userName, conn);
    }

    public void removeOnlineUsers(String userName) {
        users.remove(userName);
    }

    public int getOnlineCount() {
        return users.size();
    }

    public Collection<String> getOnlineUsers() {
        return users.keySet();
    }

    // Search in online users. Need to search on persisted data.

    public String searchContact(String searching) {
        ArrayList<String> results = new ArrayList<>();

        for(String s : users.keySet()) {
            if (s.contains(searching))
                results.add(s);

        }

        Collections.sort(results);

        return String.join(",", results);
    }

    // Message payload assembly

    public ArrayList<Message> getMessagesToSend(WebSocket socket, Message msg) {

        if(msg == null || msg.isEmpty()){
            msg = new Message();
            msg.setMessageType(Message.MessageType.ERROR);
        }

        ArrayList<String> receivers = new ArrayList<>();

        switch (msg.getMessageType()) {
            case BYE:
                removeOnlineUsers(msg.getSender());
                receivers = getContactsList(msg.getSender());
                removeFromContactsList(msg.getSender());
                break;
            case HELLO:
                addOnlineUsers(msg.getSender(), socket);
                receivers = getContactsList(msg.getSender());
                break;
            case SEARCH:
                String results = searchContact(msg.getContent());
                msg.setContent(results);
                receivers.add(msg.getSender());
                msg.setSender("server");
                break;
            case TEXT:
                if(!getContactsList(msg.getSender()).contains(msg.getReceiver())){
                    msg.setMessageType(Message.MessageType.ERROR);
                }
            case INVITE:
                receivers.add(msg.getReceiver());
                break;
            case ACCEPT:
                addToContactsList(msg.getReceiver(), msg.getSender());
                addToContactsList(msg.getSender(), msg.getReceiver());
                receivers.add(msg.getReceiver());
                break;
            case DENY:
                receivers.add(msg.getReceiver());
                break;
            default:
                msg.setMessageType(Message.MessageType.ERROR);
                receivers.add(msg.getSender());
                msg.setSender("server");
                break;
        }

        ArrayList<Message> responses = ChatUtils.prepareResponses(msg, receivers);

        return responses;
    }

}
