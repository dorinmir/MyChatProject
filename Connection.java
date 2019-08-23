/*The client and server will communicate through a socket connection.
        One side will write data to the socket, while the other will read. They interact by exchanging Messages.
        The Connection class will wrap the java.net.Socket class,
        which needs to be able to serialize and deserialize Message objects to/from the socket.
        The methods of this class should be callable from different threads.*/



package com.codegym.task.task30.task3008;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {  //to close atumaticaly the streams
    private final Socket socket;  //Socket-the endpoint between a client and a Server
    private final ObjectOutputStream out; // Client and server are comunicating with I/O streams
    private final ObjectInputStream in;   // Client and server are comunicating with I/O streams

    public Connection(Socket socket) throws IOException {         //The Constructor will initialie the I/O streams
        this.socket=socket;
        this.out=new ObjectOutputStream(socket.getOutputStream());
        this.in=new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) throws IOException{ //It writes (serialize) the message to the .out Stream
synchronized (out){
    out.writeObject(message);
}
    }

    public Message receive() throws IOException, ClassNotFoundException {   //it will read(deserialize) objects from .in
        synchronized (in) {
           return (Message) in.readObject();

        }
    }
    public SocketAddress getRemoteSocketAddress(){   //return remote address of the socket connection

        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {    //close streams
        in.close();
        out.close();
        socket.close();
    }
}
