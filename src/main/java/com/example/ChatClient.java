package com.example;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by rafaelandara on 4/27/15.
 */
public class ChatClient  extends WebSocketClient {

    private static String HOST = "localhost";
    private static int PORT = 8887;


    public ChatClient() throws Exception{
        super(new URI("ws://localhost:8887"), new Draft_75());
    }

    @Override
    public void onMessage( String message ) {
        // TODO
    }

    @Override
    public void onOpen( ServerHandshake handshake ) {
        // TODO );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // TODO
    }

    @Override
    public void onError( Exception ex ) {
        // TODO
    }

}
