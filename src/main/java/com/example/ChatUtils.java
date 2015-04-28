package com.example;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;

/**
 * Created by rafaelandara on 4/27/15.
 */
public class ChatUtils {


    //  Maps JSON received message into POJO

    public static Message parseMessage(String json) {
        Message msg;
        try {
            ObjectMapper mapper = new ObjectMapper();
            msg = mapper.readValue(json, Message.class);
        }
        catch (Exception e) {
            msg = null;
            //e.printStackTrace();
        }

        return msg;

    }


     //  Generates a List of POJO Message to be delivered as responses

    public static ArrayList<Message> prepareResponses(Message in, ArrayList<String> receivers){

        ArrayList<Message> out = new ArrayList<>();
        Message temp;

        String content;

        switch (in.getMessageType()) {
            case BYE:
                content = in.getSender() + " is offline.";
                break;
            case HELLO:
                content = in.getSender() + " is online.";
                break;
            case ERROR:
                content = "Something bad happened.";
                break;
            case INVITE:
                content = in.getSender() + " wants to be in your contacts list.";
                break;
            case ACCEPT:
                content = in.getSender() + " accepted your invite.";
                break;
            case DENY:
                content = in.getSender() + " denied your invite.";
                break;
            default:
                content = in.getContent();
                break;
        }

        for (String receiver : receivers) {
            temp = new Message(in.getSender(), receiver, content, in.getMessageType());
            out.add(temp);
        }

        return out;

    }

    // Maps POJO message into JSON to be delivered.

    public static String prepareJson(Message msg) {
        String jsonMessage = "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonMessage = mapper.writeValueAsString(msg);
        }
        catch (Exception e) {
            System.out.println("Error preparing JSON - JACKSON.");
            //e.printStackTrace();
        }

        return jsonMessage;
    }

}
