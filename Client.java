package com.codegym.task.task30.task3008.client;


import com.codegym.task.task30.task3008.Connection;
import com.codegym.task.task30.task3008.ConsoleHelper;
import com.codegym.task.task30.task3008.Message;
import com.codegym.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Error of stream");
            System.exit(1);
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Connection established.To exit enter 'exit'. ");
            while (clientConnected) {
                String message = ConsoleHelper.readString();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                } else {
                    if (shouldSendTextFromConsole()) {
                        sendTextMessage(message);
                    }
                }
            }
        } else
            ConsoleHelper.writeMessage("An error occurred while working with the client.");
    }


    protected String getServerAddress() {
        String serverAddress;
        ConsoleHelper.writeMessage("Enter server address to start");
        serverAddress = ConsoleHelper.readString();
        return serverAddress;
    }

    protected int getServerPort() {
        int serverPort;
        ConsoleHelper.writeMessage("Enter the server port");
        serverPort = ConsoleHelper.readInt();
        return serverPort;
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Enter your Name for join the chat room");
        return ConsoleHelper.readString();
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("A connection fails to send your message");
            clientConnected = false;
        }
    }






    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("Participant " + userName + " has entered the chat");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Participant " + userName + " has leaved the chat");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            synchronized (Client.this) {
                Client.this.clientConnected = clientConnected;
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            Message message;

            while (!clientConnected) {
                try {
                    message = connection.receive();
                } catch (ClassNotFoundException e) {
                    throw new IOException("Unexpected MessageType");
                }
                if (message.getType() == MessageType.NAME_REQUEST) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else {
                    if (message.getType() == MessageType.NAME_ACCEPTED) {notifyConnectionStatusChanged(true);}
                    else throw new IOException("Unexpected MessageType");}

            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            Message message;

            while (true) {

                try {
                    message = connection.receive();
                } catch (Exception e) {
                    break;
                }
                if (message.getType() == MessageType.TEXT) processIncomingMessage(message.getData());
                else {
                    if (message.getType() == MessageType.USER_ADDED) informAboutAddingNewUser(message.getData());
                    else {
                        if (message.getType() == MessageType.USER_REMOVED) informAboutDeletingNewUser(message.getData());
                        else break;
                    }
                }

            }
            throw new IOException("Unexpected MessageType");

        }

        public void run(){


                String serverAddress = getServerAddress();
                int serverPort = getServerPort();
                try {
                    Socket socket = new Socket(serverAddress,serverPort);
                    connection = new Connection(socket);
                    clientHandshake();
                    clientMainLoop();

                } catch (IOException  | ClassNotFoundException e) {
                    notifyConnectionStatusChanged(false);
                }
            }


    }
}