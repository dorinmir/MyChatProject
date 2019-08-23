


package com.codegym.task.task30.task3008;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));//read from console

    public static void writeMessage(String message) {  // instead of writing System.out.println we use method
        System.out.println(message);
    }

    public static String readString() {       //read String from keyboard
        while(true)
        try {
            return reader.readLine();
        } catch (IOException e) {
            writeMessage("An error occurred while trying to enter text. Try again.");
        }
    }


    public static int readInt(){               // read Int from keyboard converting readString to Int
        while(true) {
            try{
            return Integer.parseInt(readString()); }
            catch(NumberFormatException e){
                writeMessage("An error while trying to enter a number. Try again.");
            }
        }
    }

   
}

