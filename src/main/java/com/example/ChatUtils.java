package com.example;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafaelandara on 4/27/15.
 */
public class ChatUtils {

    public static Message unused(String json) {

        Message msg = parseMessage(json);
        Message reply = new Message();


        return reply;
    }

    public static Message parseMessage(String json) {
        Message msg = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            msg = mapper.readValue(json, Message.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return msg;

    }

    public static ArrayList<Message> prepareResponses(Message in, List<String> receivers){

        ArrayList<Message> out = new ArrayList<>();
        Message temp;

        String content;

        switch (in.getMessageType()) {
            case BYE:
                content = in.getSender() + "is offline.";
                break;
            case HELLO:
                content = in.getSender() + "is online.";
                break;
            case ERROR:
                content = "Something bad happend.";
                break;
            case INVITE:
                content = in.getSender() + " wants to be in your contacts list.";;
                break;
            default:
                content = in.getContent();
                break;
        }

        for(String receiver : receivers) {
            temp = new Message(in.getSender(), receiver, content, in.getMessageType());
            out.add(temp);
        }
        return out;
    }

    public static String prepareJson(Message msg) {
        String jsonMessage = "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonMessage = mapper.writeValueAsString(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonMessage;
    }

}
