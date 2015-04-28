package com.example;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.reflect.Array;
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

    public void init() {
        users = new HashMap<>();
        contactsList = new MultiValueMap<>();
    }

    @Override
    public void onMessage(WebSocket socket, String json) {

        Message msg = ChatUtils.parseMessage(json);

        ArrayList<Message> responses = getMessagesToSend(socket, msg);

        for(Message m : responses) {
            WebSocket to = users.get(msg.getReceiver());
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

    public void addToContactsList(String userName, String contactName) {
        contactsList.put(userName, contactName);
    }

    public void removeFromContactsList(String userName) {
        contactsList.remove(userName);
    }

    public void addOnlineUsers(String userName, WebSocket conn) {
        users.put(userName, conn);
    }

    public void removeOnlineUsers(String userName) {
        users.remove(userName);
    }

    public int getConnCount() {
        return users.size();
    }

    public Collection<String> getOnlineUsers() {
        return users.keySet();
    }

    public String searchContact(String searching) {
        ArrayList<String> results = new ArrayList<>();

        for(String s : users.keySet()) {
            if (s.contains(searching))
                results.add(s);

        }

        Collections.sort(results);

        return String.join(",", results);
    }

    public ArrayList<Message> getMessagesToSend(WebSocket socket, Message msg) {

        ArrayList<String> receivers = new ArrayList<>();

        switch (msg.getMessageType()) {
            case BYE:
                removeOnlineUsers(msg.getSender());
                receivers = getContactsList(msg.getSender());
                break;
            case HELLO:
                addOnlineUsers(msg.getSender(), socket);
                receivers = getContactsList(msg.getSender());
                break;
            case SEARCH:
                String results = searchContact(msg.getContent());
                msg.setContent(results);
                receivers.add(msg.getSender());
                msg.setSender(msg.getReceiver());
                break;
            case TEXT:
            case INVITE:
                receivers.add(msg.getReceiver());
                break;
            case ACCEPT:
                addToContactsList(msg.getReceiver(), msg.getSender());
                receivers.add(msg.getReceiver());
                break;
            case DENY:
                receivers.add(msg.getReceiver());
                break;
            default:
                msg.setMessageType(Message.MessageType.ERROR);
                receivers.add(msg.getSender());
                msg.setSender(msg.getReceiver());
                break;
        }

        ArrayList<Message> responses = ChatUtils.prepareResponses(msg, receivers);

        return responses;
    }

    public ArrayList<String> getContactsList(String userName) {
        ArrayList<String> contacts = new ArrayList<>();
        if (!contactsList.isEmpty()) {
            contacts = (ArrayList<String>) contactsList.get(userName);
        }
        return contacts;
    }

}
