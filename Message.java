/*
A Message is data that one party sends and the other receives.
Each message must have a MessageType and some additional data,
for example, a text message must contain text.
Since the messages will be created in one program and read in another,
it will be convenient to use serialization to convert objects to sequences of bits and back again.
 */



package com.codegym.task.task30.task3008;

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
