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

    @After
    public void cleanData() {
        server.init();
    }

    @Test
    public void baseTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void initialConnCountTest() {
        int totalConn = server.getOnlineCount();
        Assert.assertEquals(0, totalConn);
    }

    @Test
    public void connCountTest() throws Exception {

        server.addOnlineUsers("user01", null);

        Assert.assertEquals(1, server.getOnlineCount());

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

    @Test
    public void helloNoContactsMessageTest() {
        Message msg = new Message("sender", "", "", Message.MessageType.HELLO);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(0, responses.size());

        Assert.assertEquals(0, server.getContactsList("sender").size());

    }

    @Test
    public void helloSingleContactMessageTest() {

        server.addToContactsList("sender", "receiver");

        Message msg = new Message("sender", "", "", Message.MessageType.HELLO);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("sender is online.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.HELLO, responses.get(0).getMessageType());

        Assert.assertEquals(1, server.getContactsList("sender").size());
    }

    @Test
    public void helloMultipleContactsMessageTest() {

        server.addToContactsList("sender", "receiver01");
        server.addToContactsList("sender", "receiver02");


        Message msg = new Message("sender", "", "", Message.MessageType.HELLO);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(2, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver01", responses.get(0).getReceiver());
        Assert.assertEquals("sender is online.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.HELLO, responses.get(0).getMessageType());

        Assert.assertEquals("sender", responses.get(1).getSender());
        Assert.assertEquals("receiver02", responses.get(1).getReceiver());
        Assert.assertEquals("sender is online.", responses.get(1).getContent());
        Assert.assertEquals(Message.MessageType.HELLO, responses.get(1).getMessageType());

        Assert.assertEquals(2, server.getContactsList("sender").size());

    }

    @Test
    public void byeNoContactsMessageTest() {
        Message msg = new Message("sender", "", "", Message.MessageType.BYE);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(0, responses.size());

        Assert.assertEquals(0, server.getContactsList("sender").size());

    }

    @Test
    public void byeSingleContactMessageTest() {

        server.addToContactsList("sender", "receiver");

        Message msg = new Message("sender", "", "", Message.MessageType.BYE);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("sender is offline.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.BYE, responses.get(0).getMessageType());

        Assert.assertEquals(0, server.getContactsList("sender").size());

    }

    @Test
    public void byeMultipleContactsMessageTest() {

        server.addToContactsList("sender", "receiver01");
        server.addToContactsList("sender", "receiver02");

        Message msg = new Message("sender", "", "", Message.MessageType.BYE);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(2, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver01", responses.get(0).getReceiver());
        Assert.assertEquals("sender is offline.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.BYE, responses.get(0).getMessageType());

        Assert.assertEquals("sender", responses.get(1).getSender());
        Assert.assertEquals("receiver02", responses.get(1).getReceiver());
        Assert.assertEquals("sender is offline.", responses.get(1).getContent());
        Assert.assertEquals(Message.MessageType.BYE, responses.get(1).getMessageType());

        Assert.assertEquals(0, server.getContactsList("sender").size());

    }

    @Test
    public void acceptMessageTest() {
        Message msg = new Message("sender", "receiver", "", Message.MessageType.ACCEPT);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("sender accepted your invite.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.ACCEPT, responses.get(0).getMessageType());

        Assert.assertEquals(1, server.getContactsList("receiver").size());
        Assert.assertEquals(1, server.getContactsList("sender").size());

    }

    @Test
    public void denyMessageTest() {
        Message msg = new Message("sender", "receiver", "", Message.MessageType.DENY);

        ArrayList<Message> responses = server.getMessagesToSend(null, msg);

        Assert.assertEquals(1, responses.size());

        Assert.assertEquals("sender", responses.get(0).getSender());
        Assert.assertEquals("receiver", responses.get(0).getReceiver());
        Assert.assertEquals("sender denied your invite.", responses.get(0).getContent());
        Assert.assertEquals(Message.MessageType.DENY, responses.get(0).getMessageType());

        Assert.assertEquals(0, server.getContactsList("receiver").size());

    }

}
