package com.example;

import org.junit.*;

import java.util.ArrayList;

/**
 * Created by rafaelandara on 4/27/15.
 */
public class ChatServerTest {

    static ChatServer server;

    @BeforeClass
    public static void setUp() {
        server = new ChatServer();
        server.start();
    }

    @AfterClass
    public static void serverShutdown() throws Exception {
        server.stop();
    }

    @Test
    public void baseTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void initialConnCountTest() {
        int totalConn = server.getConnCount();
        Assert.assertEquals(0, totalConn);
    }

    @Test
    public void connCountTest() throws Exception {

        server.addOnlineUsers("user01", null);

        Assert.assertEquals(1, server.getConnCount());

        Assert.assertTrue(server.getOnlineUsers().contains("user01"));

        Assert.assertFalse(server.getOnlineUsers().contains("otherUser"));

    }

    @Test
    public void singleContactSearchResultsetTest() {

        String searchName = "user0";

        server.addOnlineUsers("user01", null);

        Assert.assertEquals("user01", server.searchContact(searchName));

    }

    @Test
    public void multipleContactSearchResultsetTest() {

        String searchName = "user0";

        server.addOnlineUsers("user01", null);
        server.addOnlineUsers("user02", null);
        server.addOnlineUsers("otherUser", null);

        Assert.assertEquals("user01,user02", server.searchContact(searchName));

    }

    @Test
    public void notFoundContactSearchResultsetTest() {

        String searchName = "user02";

        Assert.assertEquals("", server.searchContact(searchName));

    }

    @Test
    public void searchResultsMessageTest() {

        server.addOnlineUsers("user01", null);
        server.addOnlineUsers("user02", null);
        server.addOnlineUsers("otherUser", null);

        Message msg = new Message("sender", "server", "user0", Message.MessageType.SEARCH);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("server", responses.get(0).getSender());
        Assert.assertEquals("sender", responses.get(0).getReceiver());
        Assert.assertEquals("user01,user02", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.SEARCH, responses.get(0).getMessageType());

    }

    @Test
    public void searchEmptyResultsMessageTest() {

        Message msg = new Message("sender", "server", "user01", Message.MessageType.SEARCH);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("server", responses.get(0).getSender());
        Assert.assertEquals("sender", responses.get(0).getReceiver());
        Assert.assertEquals("", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.SEARCH, responses.get(0).getMessageType());

    }

    @Test
    public void directMessageTest() {
        Message msg = new Message("sender", "receiver", "Hola", Message.MessageType.TEXT);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("Hola", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.TEXT, responses.get(0).getMessageType());

    }

    @Test
    public void inviteMessageTest() {
        Message msg = new Message("sender", "receiver", "", Message.MessageType.INVITE);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("sender wants to be in your contacts list.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.INVITE, responses.get(0).getMessageType());

    }
    
}
