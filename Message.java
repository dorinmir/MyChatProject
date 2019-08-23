package com.dorin.serverclient;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public class Message implements Serializable  {    //we need to serialize objects
    private final MessageType type;   //from Enum
    private final String data;        //Data String

    public Message(MessageType type){
        this.type=type;
        this.data=null;
    }
    public Message(MessageType type,String data){
        this.type=type;
        this.data=data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }


}
