package com.dorin.serverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        while(true)
            try {
                return reader.readLine();
            } catch (IOException e) {
                writeMessage("An error occurred while trying to enter text. Try again.");
            }
    }


    public static int readInt(){
        while(true) {
            try{
                return Integer.parseInt(readString()); }
            catch(NumberFormatException e){
                writeMessage("An error while trying to enter a number. Try again.");
            }
        }
    }


}

