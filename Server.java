//Server is the main class

/*
The server must support multiple simultaneous connections with different clients.
        This can be done using the following algorithm:
        - The server creates a server socket connection.
        - In a loop, the server waits for some client to connect to the socket.
        - It creates a new Handler thread on which messages will be exchanged with the client.
        - It waits for another connection.
*/


package com.codegym.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();  //all the conections

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Input Server Port: ");


        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {  //creating SereverSocket conection with port(port)
            ConsoleHelper.writeMessage("Server started...");  // instead of println we use method,server started
            while (true) {       //In a loop, the server waits for some client to connect to the socket.
                new Handler(serverSocket.accept()).start();
            } //It creates a new Handler thread on which messages will be exchanged with the client.
            //It waits for another connection and start it.

        } catch (Exception e) {
            ConsoleHelper.writeMessage("Something wrong, Server socket closed.");
        }

    }


    public static void sendBroadcastMessage(Message message) {
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Handler extends Thread {  //class used fot multiple clients //The Handler class must implement the client communication protocol.

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            if (socket != null && socket.getRemoteSocketAddress() != null) {
                ConsoleHelper.writeMessage("Established a new connection to a remote socket address: " + socket.getRemoteSocketAddress());
                String userName = null;

                try (Connection connection = new Connection(socket)) {

                    userName = serverHandshake(connection);
                    sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                    notifyUsers(connection, userName);
                    serverMainLoop(connection, userName);
                } catch (IOException | ClassNotFoundException e) {
                    ConsoleHelper.writeMessage("An exchange of data error to a remote socket address");
                } finally {
                    if (userName != null) {
                        connectionMap.remove(userName);
                        sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                    }
                    ConsoleHelper.writeMessage("Closed connection to a remote socket address: "); // + socketAddress);
                }
            }
        }


        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException { //Protoocol -first stage when server meet the client
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));  //send a Nam_Request and wait
                Message answer = connection.receive();               //receive an object response

                if (answer.getType() == MessageType.USER_NAME) {             //loop while user doesn't introduce USER_NAME

                    if (!answer.getData().isEmpty()) {                        //while is not empty
                        if (!connectionMap.containsKey(answer.getData())) {    //while is not used
                            connectionMap.put(answer.getData(), connection); /*Once all the checks have been passed,
                                the serverHandshake() method should add a new (name, connection) pair to connectionMap
                        and send a message indicating that the name was accepted.*/
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return answer.getData();
                        }
                    }
                }
            }
        }



        private void notifyUsers(Connection connection, String userName) throws IOException {  /*sending information to the client (new participant) about
            the other clients (chat participants)*/
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if (!entry.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));

                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message != null && message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Error!");
                }
            }
        }
    }
}



